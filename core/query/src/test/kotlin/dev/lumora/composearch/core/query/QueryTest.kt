package dev.lumora.composearch.core.query

import dev.lumora.composearch.core.common.result.HttpStatusException
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Verifies the hand-rolled engine's contract: a TTL window decides whether a read
 * hits the network, concurrent reads are de-duplicated to one fetch, the
 * persistent (Room) tier serves cached data when the network fails, and
 * invalidation forces a refetch. A [FakeClock] drives the TTL so no real time
 * passes. Everything here is plain Kotlin — no Compose — which is the point.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QueryTest {

    private class FakeClock(var millis: Long = 0L) {
        fun read(): Long = millis
        fun advance(duration: Duration) {
            millis += duration.inWholeMilliseconds
        }
    }

    private fun successData(entry: QueryEntry<*>): Any? =
        (entry.state.value as? QueryState.Success)?.data

    /** Stand-in for a network failure carrying an HTTP status (like ResponseException). */
    private class HttpError(override val httpStatusCode: Int) :
        Exception("http $httpStatusCode"), HttpStatusException

    @Test
    fun `within stale time serves cache, refetches once stale`() =
        runTest(UnconfinedTestDispatcher()) {
            val clock = FakeClock()
            var fetches = 0
            val entry = QueryEntry(
                key = "dashboard",
                fetcher = { "value-${++fetches}" },
                staleTime = 1.minutes.inWholeMilliseconds,
                cacheTime = Long.MAX_VALUE,
                scope = backgroundScope,
                now = clock::read,
            )

            // First observe: nothing cached -> fetch.
            entry.observe()
            assertEquals("value-1", successData(entry))
            assertEquals(1, fetches)

            // 30s later, still inside the 1-minute window -> served from cache, no fetch.
            clock.advance(30.seconds)
            entry.fetch()
            assertEquals("value-1", successData(entry))
            assertEquals(1, fetches)

            // Past the window (total 70s) -> refetch.
            clock.advance(40.seconds)
            entry.fetch()
            assertEquals("value-2", successData(entry))
            assertEquals(2, fetches)
        }

    @Test
    fun `concurrent reads are de-duplicated to a single fetch`() =
        runTest(UnconfinedTestDispatcher()) {
            val gate = CompletableDeferred<Unit>()
            var fetches = 0
            val entry = QueryEntry(
                key = "balances",
                fetcher = { gate.await(); "value-${++fetches}" },
                staleTime = 0L,
                cacheTime = Long.MAX_VALUE,
                scope = backgroundScope,
            )

            // Three reads while the first is still in flight -> only one fetch starts.
            entry.observe()
            entry.fetch()
            entry.fetch()
            assertEquals(0, fetches) // still gated

            gate.complete(Unit)
            assertEquals(1, fetches)
            assertEquals("value-1", successData(entry))
        }

    @Test
    fun `concurrent fetches across real threads run the fetcher exactly once`() = runBlocking {
        // Real multi-threaded dispatcher (not the single-threaded test dispatcher), so
        // the check-then-claim of the in-flight slot is genuinely contended.
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        val calls = AtomicInteger(0)
        val gate = CompletableDeferred<Unit>() // holds the winning fetch mid-flight
        val entry = QueryEntry(
            key = "single-flight",
            fetcher = {
                calls.incrementAndGet()
                gate.await()
                "value"
            },
            staleTime = 0L,
            cacheTime = Long.MAX_VALUE,
            scope = scope,
        )

        // Hammer fetch() from 64 coroutines across the thread pool at once.
        val callers = (1..64).map { launch(Dispatchers.Default) { entry.fetch() } }
        callers.joinAll()

        // Exactly one fetch should have claimed the slot and started its body; the rest
        // saw it in flight (or lost the CAS) and yielded. On the old check-then-set code
        // several could slip through here.
        withTimeout(5_000) { while (calls.get() == 0) yield() }
        assertEquals(1, calls.get())

        gate.complete(Unit)
        scope.cancel()
    }

    @Test
    fun `persistent tier serves the cached value when the fetch fails`() =
        runTest(UnconfinedTestDispatcher()) {
            val room = MutableStateFlow<String?>("cached-profile")
            val entry = QueryEntry(
                key = "profile",
                fetcher = { throw IOException("offline") },
                staleTime = Long.MAX_VALUE,
                cacheTime = Long.MAX_VALUE,
                scope = backgroundScope,
                source = SourceOfTruth(reader = { room }, writer = { room.value = it }),
            )

            // Room holds a value, so the hook serves it even though the fetcher throws.
            entry.observe()
            assertEquals("cached-profile", successData(entry))
        }

    @Test
    fun `retries transient failures before surfacing an error`() =
        runTest(UnconfinedTestDispatcher()) {
            var attempts = 0
            val entry = QueryEntry(
                key = "markets",
                fetcher = {
                    attempts++
                    if (attempts < 3) throw IOException("blip") else "markets-loaded"
                },
                staleTime = Long.MAX_VALUE,
                cacheTime = Long.MAX_VALUE,
                scope = backgroundScope,
                maxAttempts = 3,
                retryDelayMs = 0L, // virtual time skips the backoff
            )

            // Two failures then success: the user never sees a Failure, just the result.
            entry.observe()
            assertEquals("markets-loaded", successData(entry))
            assertEquals(3, attempts)
        }

    @Test
    fun `does not retry a client error`() =
        runTest(UnconfinedTestDispatcher()) {
            var attempts = 0
            val entry = QueryEntry<String>(
                key = "order",
                fetcher = {
                    attempts++
                    throw HttpError(httpStatusCode = 400) // bad request — deterministic
                },
                staleTime = Long.MAX_VALUE,
                cacheTime = Long.MAX_VALUE,
                scope = backgroundScope,
                maxAttempts = 3,
                retryDelayMs = 0L,
            )

            entry.observe()
            assertEquals(1, attempts) // tried once, gave up immediately
            assertEquals(true, entry.state.value is QueryState.Failure)
        }

    @Test
    fun `isRetryable retries only transient failures`() {
        // Transient — worth retrying.
        assertEquals(true, isRetryable(IOException("no connection")))
        assertEquals(true, isRetryable(HttpError(HttpStatusException.NETWORK_ERROR_CODE)))
        assertEquals(true, isRetryable(HttpError(408))) // request timeout
        assertEquals(true, isRetryable(HttpError(500)))
        assertEquals(true, isRetryable(HttpError(503)))

        // Deterministic — must NOT retry. For a write (Buy/Withdraw/Transfer) re-firing
        // one of these risks a duplicate operation, so the mutation path stops here.
        assertEquals(false, isRetryable(HttpError(400)))
        assertEquals(false, isRetryable(HttpError(401)))
        assertEquals(false, isRetryable(HttpError(409)))
        assertEquals(false, isRetryable(HttpError(429))) // handled at the network layer
        assertEquals(false, isRetryable(IllegalStateException("programmer error")))
    }

    @Test
    fun `a tick that lands on a still-running fetch is skipped, not forced`() =
        runTest(UnconfinedTestDispatcher()) {
            // Every request outlasts the poll period — a slow connection against a live
            // screen. Forcing would cancel each attempt just before it landed, so the value
            // would never refresh at all; the tick has to yield to the fetch already running.
            var started = 0
            var completed = 0
            val entry = QueryEntry(
                key = "assets",
                fetcher = {
                    started++
                    delay(2_500L) // > the 1s interval below
                    completed++
                    "total-$completed"
                },
                staleTime = 0L,
                cacheTime = Long.MAX_VALUE,
                scope = backgroundScope,
            )

            entry.observe()
            entry.startInterval(interval = 1_000L)
            advanceTimeBy(6_000L)

            // Ticks at 1s/2s fall inside the first fetch and are skipped, so it survives to
            // commit. Were they forced, `completed` would stay 0 however long we ran.
            assertEquals(true, completed > 0)
            assertEquals(true, started <= 3) // one per completion, not one per tick
            assertEquals("total-$completed", successData(entry))
        }

    @Test
    fun `stopInterval pauses polling, startInterval resumes it`() =
        runTest(UnconfinedTestDispatcher()) {
            var fetches = 0
            val entry = QueryEntry(
                key = "ticker",
                fetcher = { "tick-${++fetches}" },
                staleTime = 0L, // always stale, so every tick really refetches
                cacheTime = Long.MAX_VALUE,
                scope = backgroundScope,
            )

            entry.observe() // initial load
            assertEquals(1, fetches)

            // Polling active (STARTED): ticks fire.
            entry.startInterval(interval = 1_000L)
            advanceTimeBy(3_500L)
            assertEquals(true, fetches > 1)
            val whilePolling = fetches

            // Backgrounded (STOPPED -> stopInterval): polling must pause.
            entry.stopInterval()
            advanceTimeBy(10_000L)
            assertEquals(whilePolling, fetches) // no new fetches while paused

            // Foregrounded again (STARTED -> startInterval): polling resumes.
            entry.startInterval(interval = 1_000L)
            advanceTimeBy(2_500L)
            assertEquals(true, fetches > whilePolling)
        }

    @Test
    fun `unobserved slots are evicted from the client after cacheTime`() =
        runTest(UnconfinedTestDispatcher()) {
            // Share the test's virtual clock so advanceTimeBy drives the client's
            // cacheTime delay (which runs on the client's own scope).
            val client = QueryClient(UnconfinedTestDispatcher(testScheduler))

            // Open 100 distinct (dynamic) keys, as a per-asset screen would.
            repeat(100) { i ->
                client.getOrCreate(
                    key = "asset.$i",
                    fetcher = { "value-$i" },
                    staleTime = Long.MAX_VALUE,
                    cacheTime = 1.minutes.inWholeMilliseconds,
                ).also { entry ->
                    entry.observe()   // a screen opens
                    entry.unobserve() // …and closes, scheduling eviction
                }
            }
            assertEquals(100, client.entryCount())

            // Past cacheTime with nobody observing -> every slot is reclaimed.
            advanceTimeBy(2.minutes.inWholeMilliseconds)
            assertEquals(0, client.entryCount())
        }

    @Test
    fun `a slot re-observed during the eviction window is kept`() =
        runTest(UnconfinedTestDispatcher()) {
            val client = QueryClient(UnconfinedTestDispatcher(testScheduler))
            val entry = client.getOrCreate(
                key = "asset.btc",
                fetcher = { "btc" },
                staleTime = Long.MAX_VALUE,
                cacheTime = 1.minutes.inWholeMilliseconds,
            )

            entry.observe()
            entry.unobserve()                              // eviction scheduled
            advanceTimeBy(30.seconds.inWholeMilliseconds)  // half-way through the window
            entry.observe()                                // a screen comes back
            advanceTimeBy(2.minutes.inWholeMilliseconds)   // original timer would have fired

            assertEquals(1, client.entryCount())           // still cached — observer present
        }

    @Test
    fun `invalidate forces a refetch through the client`() =
        runTest(UnconfinedTestDispatcher()) {
            val client = QueryClient(UnconfinedTestDispatcher())
            var fetches = 0
            // Long stale time so only invalidation (not staleness) can trigger the refetch.
            val entry = client.getOrCreate(
                key = "rates",
                fetcher = { "value-${++fetches}" },
                staleTime = 10.minutes.inWholeMilliseconds,
                cacheTime = Long.MAX_VALUE,
            )

            entry.observe()
            assertEquals("value-1", successData(entry))
            assertEquals(1, fetches)

            client.invalidate("rates")
            assertEquals("value-2", successData(entry))
            assertEquals(2, fetches)
        }
}
