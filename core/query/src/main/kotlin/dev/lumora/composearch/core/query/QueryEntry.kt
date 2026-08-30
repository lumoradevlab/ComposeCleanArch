/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.query

import dev.lumora.composearch.core.common.result.AppError
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * One cache slot, keyed by query key — the in-memory engine behind a hook and
 * the hand-rolled equivalent of a React-Query query state (named [QueryEntry] so it
 * does not clash with the UI-facing [QueryState] sealed type). It is **plain Kotlin**,
 * so the caching/TTL/dedup logic is unit-testable without Compose — only the
 * subscription in [useQuery] is `@Composable`.
 *
 * Responsibilities (fetchers throw and we map to [AppError]; state is the
 * [QueryState] union):
 * - holds the current [state] and exposes it as a [StateFlow] every observer shares;
 * - de-duplicates concurrent fetches (one in-flight request per key);
 * - ref-counts observers so the slot is reclaimed [cacheTime] ms after the last
 *   screen leaves (cancel reclamation if a screen comes back);
 * - enforces a [staleTime] TTL — a read within the window serves cache untouched;
 * - optionally mirrors a Room [source] of truth (the persistent/offline tier);
 * - supports interval refetching for live screens (e.g. a dashboard that polls).
 */
internal class QueryEntry<T>(
    private val key: String,
    private val fetcher: suspend () -> T,
    private val staleTime: Long,
    private val cacheTime: Long,
    private val scope: CoroutineScope,
    private val source: SourceOfTruth<T>? = null,
    private val maxAttempts: Int = 1,
    private val retryDelayMs: Long = QueryClient.DEFAULT_RETRY_DELAY_MS,
    private val now: () -> Long = System::currentTimeMillis,
    private val recordMetric: ((key: String, durationMs: Long, fromCache: Boolean, isError: Boolean) -> Unit)? = null,
    /**
     * Called when this slot has gone unobserved for [cacheTime] and is ready to be
     * reclaimed. The owner ([QueryClient]) uses it to drop the slot from its map so the
     * cache actually shrinks; the owner is responsible for calling [cleanup]. When null
     * (a standalone entry, e.g. in a unit test) we fall back to [cleanup] so the entry
     * still releases its jobs.
     */
    private val onEvict: ((key: String) -> Unit)? = null,
) {
    private val _state = MutableStateFlow<QueryState<T>>(QueryState.Loading)
    val state: StateFlow<QueryState<T>> = _state.asStateFlow()

    /** True only while a user-triggered (manual) refetch is in flight — drives pull-to-refresh spinners. */
    private val _isManualRefetch = MutableStateFlow(false)
    val isManualRefetch: StateFlow<Boolean> = _isManualRefetch.asStateFlow()

    // Touched from multiple threads (fetch coroutine on IO, observe/unobserve on main,
    // interval loop), so these are volatile/atomic rather than plain vars — see class
    // docs. `intervalJob`/`intervalCount` below already follow this pattern.
    @Volatile private var lastFetch = 0L
    private val observers = AtomicInteger(0)
    private val cleanupJob = AtomicReference<Job?>(null)
    private val inFlight = AtomicReference<Deferred<Unit>?>(null)
    private val sourceJob = AtomicReference<Job?>(null)

    private val intervalJob = AtomicReference<Job?>(null)
    private val intervalCount = AtomicInteger(0)

    // Monotonic write sequence. A fetch captures its epoch when it wins the in-flight slot
    // and only commits its result if it is still the latest — so a superseded (older) fetch
    // that finishes late cannot clobber fresher data. setData bumps it too, so an optimistic
    // write wins over an in-flight fetch that lands right after it.
    private val latestEpoch = AtomicLong(0)

    private val current: T? get() = (_state.value as? QueryState.Success)?.data

    /** True when no screen is observing — the precondition for the owner to evict this slot. */
    internal fun hasNoObservers(): Boolean = observers.get() <= 0

    /** A screen started observing. The first observer wires up Room and kicks off a fetch if stale. */
    fun observe() {
        val count = observers.incrementAndGet()
        cleanupJob.getAndSet(null)?.cancel()
        if (count == 1) {
            startSourceCollection()
            if (shouldFetch()) fetch()
        }
    }

    /** A screen stopped observing. After [cacheTime] with no observers the slot is evicted. */
    fun unobserve() {
        if (observers.decrementAndGet() <= 0) {
            // Stop mirroring Room while nobody is watching — otherwise the collector keeps
            // running (and holding a DB subscription) for the whole cacheTime window.
            // observe() restarts it via startSourceCollection() when a screen comes back.
            sourceJob.getAndSet(null)?.cancel()
            val job = scope.launch {
                delay(cacheTime)
                // Hand back to the owner so the slot leaves the cache map (not just
                // emptied in place) — otherwise dynamic keys accumulate forever.
                // `isActive` guards the case where observe() cancelled this job after
                // the delay but before we ran (a screen came back).
                if (isActive && observers.get() <= 0) {
                    onEvict?.invoke(key) ?: cleanup()
                }
            }
            cleanupJob.getAndSet(job)?.cancel()
        }
    }

    /**
     * Run the fetcher. Skips if cache is still fresh (unless [force]); de-duplicates
     * against an in-flight request. [manual] flips [isManualRefetch] so the UI can
     * show a pull-to-refresh indicator distinct from the first-load spinner.
     */
    fun fetch(force: Boolean = false, manual: Boolean = false) {
        if (manual) _isManualRefetch.value = true

        if (!force && inFlight.get()?.isActive == true) {
            if (manual) _isManualRefetch.value = false
            return
        }

        val servedFromCache = !force && !shouldFetch() && current != null
        if (servedFromCache) {
            recordMetric?.invoke(key, 0L, true, false)
            if (manual) _isManualRefetch.value = false
            return
        }

        // This fetch's epoch, assigned once it wins the in-flight slot below. The body only
        // commits if it is still the latest epoch — so a superseded fetch drops its write.
        val epoch = AtomicLong(-1L)

        // Built lazily so the body never starts until we've atomically claimed the
        // in-flight slot below — that is what makes the single-flight guarantee hold
        // when fetch() is called from several threads at once.
        val deferred: Deferred<Unit> = scope.async(start = CoroutineStart.LAZY) {
            val start = now()
            // Keep showing existing data (refreshing) instead of flashing a spinner.
            _state.value = current?.let { QueryState.Success(it, refreshing = true) } ?: QueryState.Loading
            try {
                var attempt = 1
                while (true) {
                    try {
                        val data = fetcher()
                        // Drop the result if a newer fetch (or setData) has superseded us —
                        // otherwise an older response landing late clobbers fresher data.
                        if (epoch.get() != latestEpoch.get()) break
                        lastFetch = now()
                        source?.writer(data) // Room emits the new value through reader; we also set it directly.
                        _state.value = QueryState.Success(data, refreshing = false)
                        recordMetric?.invoke(key, now() - start, false, false)
                        break
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (throwable: Throwable) {
                        // Retry transient failures silently (staying in Loading/refreshing)
                        // so the user only ever sees an error once we've truly given up.
                        if (attempt < maxAttempts && isRetryable(throwable)) {
                            delay(retryDelayMs * attempt) // linear backoff: 1×, 2×, …
                            attempt++
                            continue
                        }
                        // A superseded fetch must not overwrite fresher state with a stale error.
                        if (epoch.get() != latestEpoch.get()) break
                        // A failed refetch keeps the last good data on screen (offline-tolerant);
                        // only a first load with nothing cached surfaces the error.
                        val existing = current
                        _state.value =
                            if (existing != null) QueryState.Success(existing, refreshing = false)
                            else QueryState.Failure(AppError.from(throwable))
                        recordMetric?.invoke(key, now() - start, false, true)
                        break
                    }
                }
            } finally {
                if (manual) _isManualRefetch.value = false
            }
        }

        // Atomically install as THE in-flight request. A non-forced fetch that loses the
        // race to one already running drops its (not-yet-started) coroutine and yields;
        // a forced fetch always replaces AND cancels the one it supersedes. Only the
        // winner's coroutine is ever started.
        while (true) {
            val running = inFlight.get()
            if (!force && running?.isActive == true) {
                deferred.cancel()
                if (manual) _isManualRefetch.value = false
                return
            }
            if (inFlight.compareAndSet(running, deferred)) {
                // Cancel the fetch we just replaced so it can't keep running and write late.
                running?.cancel()
                // Claim the latest epoch AFTER install, so losing racers never bump it.
                epoch.set(latestEpoch.incrementAndGet())
                break
            }
        }
        deferred.start()

        scope.launch {
            try {
                deferred.join()
            } finally {
                inFlight.compareAndSet(deferred, null)
            }
        }
    }

    /** Drop the freshness stamp and refetch immediately if anyone is watching (used by invalidation). */
    fun invalidate() {
        lastFetch = 0L
        if (observers.get() > 0) fetch(force = true, manual = true)
    }

    /** Optimistically set the cached value (and persist it if there is a source of truth). */
    fun setData(data: T) {
        lastFetch = now()
        // Supersede any in-flight fetch so its (older) response can't overwrite this write.
        latestEpoch.incrementAndGet()
        _state.value = QueryState.Success(data, refreshing = false)
        source?.let { src -> scope.launch { src.writer(data) } }
    }

    fun startInterval(interval: Long) {
        if (observers.get() <= 0 || interval <= 0) return
        if (intervalCount.incrementAndGet() == 1) {
            intervalJob.getAndSet(null)?.cancel()
            val job = scope.launch {
                while (isActive && intervalCount.get() > 0 && observers.get() > 0) {
                    delay(interval)
                    if (observers.get() <= 0) break
                    // Let a still-running fetch finish rather than forcing past it. A forced
                    // fetch cancels the one in flight, so against a connection slower than
                    // [interval] every attempt would be killed just before it landed and the
                    // value would never refresh at all — silently, since a cancelled fetch
                    // surfaces no error. Skipping the tick costs one period; forcing costs
                    // every period.
                    if (inFlight.get()?.isActive != true) fetch(force = true, manual = false)
                }
            }
            intervalJob.set(job)
        }
    }

    fun stopInterval() {
        if (intervalCount.decrementAndGet() <= 0) {
            intervalCount.set(0)
            intervalJob.getAndSet(null)?.cancel()
        }
    }

    fun cleanup() {
        inFlight.getAndSet(null)?.cancel()
        cleanupJob.getAndSet(null)?.cancel()
        sourceJob.getAndSet(null)?.cancel()
        observers.set(0)
        intervalCount.set(0)
        intervalJob.getAndSet(null)?.cancel()
    }

    /** Stream Room into [state] so external DB writes (and our own [writer]) show up live. */
    private fun startSourceCollection() {
        val src = source ?: return
        if (sourceJob.get() != null) return
        val job = scope.launch {
            src.reader().collect { value ->
                if (value != null) {
                    _state.value = QueryState.Success(value, refreshing = inFlight.get()?.isActive == true)
                }
            }
        }
        // If two observers race here, only one job wins the slot; cancel the loser.
        if (!sourceJob.compareAndSet(null, job)) job.cancel()
    }

    private fun shouldFetch(): Boolean =
        current == null || (now() - lastFetch) > staleTime
}
