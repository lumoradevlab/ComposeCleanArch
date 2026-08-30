/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.query

import dev.lumora.composearch.core.common.dispatchers.IoDispatcher
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * The cache + invalidation handle — the equivalent of React Query's `QueryClient`,
 * hand-rolled (no Store5). It owns one [QueryEntry] per key in a [ConcurrentHashMap]
 * and a long-lived background [scope] on the injected IO dispatcher.
 *
 * It is a Hilt `@Singleton`, so the DI graph is the real owner of the cache; it is
 * also exposed to Compose via [LocalQueryClient]/[ProvideQueryClient] so hooks can
 * reach it without threading it through every call.
 *
 * Use it for cross-feature invalidation — after a buy mutation,
 * `queryClient.invalidate(ArticleKeys.HEADLINES)` makes the headlines query refetch.
 *
 * The dispatcher is injected (never `Dispatchers.IO` inline) so a
 * test can drive the engine with a `TestDispatcher`: `QueryClient(testDispatcher)`.
 */
@Singleton
class QueryClient @Inject constructor(
    @IoDispatcher dispatcher: CoroutineDispatcher,
) {
    private val scope = CoroutineScope(SupervisorJob() + dispatcher)
    private val entries = ConcurrentHashMap<String, QueryEntry<*>>()
    private val metrics = ConcurrentHashMap<String, QueryMetrics>()

    /** Get the shared cache slot for [key], creating it on first use. Hooks call this. */
    @Suppress("UNCHECKED_CAST")
    internal fun <T> getOrCreate(
        key: String,
        fetcher: suspend () -> T,
        staleTime: Long,
        cacheTime: Long,
        source: SourceOfTruth<T>? = null,
        maxAttempts: Int = DEFAULT_MAX_ATTEMPTS,
        retryDelayMs: Long = DEFAULT_RETRY_DELAY_MS,
    ): QueryEntry<T> = entries.computeIfAbsent(key) {
        QueryEntry(
            key, fetcher, staleTime, cacheTime, scope, source, maxAttempts, retryDelayMs,
            recordMetric = ::record, onEvict = ::evict,
        )
    } as QueryEntry<T>

    /** Force the query registered under [key] to refetch from the network. */
    fun invalidate(key: String) {
        entries[key]?.invalidate()
    }

    /** Invalidate every key matching [pattern] (a regex) — e.g. `"articles\\..*"`. */
    fun invalidateMatching(pattern: String) {
        val regex = pattern.toRegex()
        entries.keys.asSequence()
            .filter { regex.containsMatchIn(it) }
            .forEach { invalidate(it) }
    }

    /** Invalidate a known set of keys in one call. */
    fun invalidateAll(keys: List<String>) {
        keys.forEach { invalidate(it) }
    }

    /** Optimistically write [data] into an existing cache slot (no-op if not created yet). */
    @Suppress("UNCHECKED_CAST")
    fun <T> setData(key: String, data: T) {
        (entries[key] as? QueryEntry<T>)?.setData(data)
    }

    /** Warm the cache ahead of navigation. Fetches only if the slot is missing/stale. */
    fun <T> prefetch(
        key: String,
        fetcher: suspend () -> T,
        staleTime: Long = DEFAULT_STALE_TIME_MS,
        cacheTime: Long = DEFAULT_CACHE_TIME_MS,
    ) {
        scope.launch {
            getOrCreate(key, fetcher, staleTime, cacheTime).fetch(force = false)
        }
    }

    /** Drop a single query slot and its metrics. */
    fun remove(key: String) {
        entries[key]?.cleanup()
        entries.remove(key)
        metrics.remove(key)
    }

    /** How many live cache slots exist — for tests and the metrics/debug surface. */
    fun entryCount(): Int = entries.size

    /**
     * Reclaim a slot that has been unobserved for its `cacheTime`. Routed here from the
     * entry so the slot actually leaves [entries] (and [metrics]) instead of being emptied
     * in place — otherwise dynamic keys (`asset.{id}`, searches) would accumulate forever.
     *
     * The check + removal run inside [ConcurrentHashMap.computeIfPresent] so they are
     * atomic against another thread re-creating the key; a slot that picked up a new
     * observer in the eviction window (`hasNoObservers() == false`) is kept.
     */
    private fun evict(key: String) {
        entries.computeIfPresent(key) { _, entry ->
            if (entry.hasNoObservers()) {
                entry.cleanup()
                metrics.remove(key)
                null // returning null removes the mapping
            } else {
                entry // re-observed since eviction was scheduled — keep it
            }
        }
    }

    /** Drop every query (e.g. on logout). */
    fun clear() {
        entries.values.forEach { it.cleanup() }
        entries.clear()
        metrics.clear()
    }

    /** Snapshot of per-key fetch metrics (count, timing, cache-hit/error rates). */
    fun metricsSnapshot(): Map<String, QueryMetrics> = metrics.toMap()

    private fun record(key: String, durationMs: Long, fromCache: Boolean, isError: Boolean) {
        metrics.compute(key) { _, existing ->
            (existing ?: QueryMetrics()).run {
                copy(
                    fetchCount = fetchCount + 1,
                    totalTimeMs = totalTimeMs + durationMs,
                    cacheHits = cacheHits + if (fromCache) 1 else 0,
                    errors = errors + if (isError) 1 else 0,
                )
            }
        }
    }

    companion object {
        const val DEFAULT_STALE_TIME_MS = 5 * 60 * 1000L
        const val DEFAULT_CACHE_TIME_MS = 10 * 60 * 1000L

        /** Total fetch attempts (incl. the first) before a cold load surfaces an error. */
        const val DEFAULT_MAX_ATTEMPTS = 3

        /** Base delay between retries; backs off linearly (1×, 2×, …). */
        const val DEFAULT_RETRY_DELAY_MS = 1000L
    }
}

/** Lightweight per-query telemetry, handy for spotting a chatty or failing screen. */
data class QueryMetrics(
    val fetchCount: Int = 0,
    val totalTimeMs: Long = 0L,
    val cacheHits: Int = 0,
    val errors: Int = 0,
) {
    val averageTimeMs: Long get() = if (fetchCount > 0) totalTimeMs / fetchCount else 0L
    val cacheHitRate: Float get() = if (fetchCount > 0) cacheHits.toFloat() / fetchCount else 0f
    val errorRate: Float get() = if (fetchCount > 0) errors.toFloat() / fetchCount else 0f
}
