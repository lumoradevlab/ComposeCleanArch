/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.query

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.lumora.composearch.core.common.result.AppError
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow

/** Per-call-site knobs for [useQuery], mirroring React Query's options. */
data class QueryOptions(
    /** How long a fetched value stays fresh; a read inside this window serves cache untouched. */
    val staleTime: Long = QueryClient.DEFAULT_STALE_TIME_MS,
    /** How long the cache slot lives after the last screen stops observing. */
    val cacheTime: Long = QueryClient.DEFAULT_CACHE_TIME_MS,
    /** Set false to keep the hook idle (e.g. until a dependency/id is available). */
    val enabled: Boolean = true,
    /** >0 ms refetches on a timer while the screen is STARTED (e.g. a live ticker). */
    val refetchInterval: Long = 0L,
    /**
     * Total fetch attempts before a cold load surfaces an error/retry button. The
     * default retries transient failures (network/timeout/5xx) twice — the user only
     * sees an error once we've genuinely given up. Set to 1 to disable retrying.
     */
    val maxAttempts: Int = QueryClient.DEFAULT_MAX_ATTEMPTS,
    /** Base delay between retries; backs off linearly (1×, 2×, …). */
    val retryDelayMs: Long = QueryClient.DEFAULT_RETRY_DELAY_MS,
) {
    companion object {
        /** Feed-style: serve cache, refetch once it is older than [millis]. */
        fun freshFor(millis: Long): QueryOptions = QueryOptions(staleTime = millis)

        /** Profile-style: never goes stale on its own; refresh only via invalidation. */
        val OfflineForever: QueryOptions = QueryOptions(staleTime = Long.MAX_VALUE)
    }
}

/**
 * The read hook (memory tier). The [fetcher] just calls the API and returns its
 * value (or throws) — exactly like any suspend function. The hook caches the
 * result under [key], de-duplicates in-flight requests, re-emits to every
 * observer, refetches when [key] is invalidated via the [QueryClient], and honours
 * [QueryOptions.staleTime].
 *
 * ```
 * val articles = useQuery(ArticleKeys.HEADLINES, { repo.getHeadlines() })
 * QueryContent(articles) { list -> ArticleList(list) }
 * ```
 */
@Composable
fun <T> useQuery(
    key: String,
    fetcher: suspend () -> T,
    options: QueryOptions = QueryOptions(),
    source: SourceOfTruth<T>? = null,
): QueryResult<T> {
    val client = LocalQueryClient.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val entry = remember(key) {
        client.getOrCreate(
            key, fetcher, options.staleTime, options.cacheTime, source,
            options.maxAttempts, options.retryDelayMs,
        )
    }

    val state by entry.state.collectAsState()
    val isManualRefetch by entry.isManualRefetch.collectAsState()

    // Observer ref-count is owned by a single effect keyed only on (key, enabled), so
    // exactly one observe() is paired with one unobserve(). Declared before the interval
    // effect so observe() runs (synchronously, on enter) before startInterval() checks the
    // count. Keeping refetchInterval OUT of these keys is the fix for the ref-count leak:
    // a changing interval must not re-run observe() without a matching unobserve().
    DisposableEffect(key, options.enabled) {
        if (options.enabled) entry.observe()
        onDispose { if (options.enabled) entry.unobserve() }
    }

    // Interval polling lives in its own effect — it restarts when the interval changes and
    // never touches the observer count. Poll only while STARTED: staying suspended in the
    // block means STOP (backgrounding) cancels it and the `finally` pauses the poll; coming
    // back to STARTED re-enters and resumes.
    LaunchedEffect(key, options.enabled, options.refetchInterval) {
        if (!options.enabled || options.refetchInterval <= 0) return@LaunchedEffect
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            entry.startInterval(options.refetchInterval)
            try {
                awaitCancellation()
            } finally {
                entry.stopInterval()
            }
        }
    }

    return remember(state, isManualRefetch) {
        QueryResult(
            state = state,
            isManualRefetch = isManualRefetch,
            refetch = { entry.fetch(force = true, manual = true) },
        )
    }
}

/**
 * The read hook (persistent tier). Same contract as [useQuery] but backed by Room
 * as the single source of truth: values stream from [reader] and every successful
 * fetch is persisted via [writer], so reads work offline and across process death.
 *
 * ```
 * val profile = useCachedQuery(
 *     key = ProfileKeys.PROFILE,
 *     fetcher = { api.getProfile() },        // network -> ProfileEntity
 *     reader  = { dao.observe() },           // Room -> Flow<ProfileEntity?>
 *     writer  = { dao.upsert(it) },
 * )
 * ```
 */
@Composable
fun <T> useCachedQuery(
    key: String,
    fetcher: suspend () -> T,
    reader: () -> Flow<T?>,
    writer: suspend (T) -> Unit,
    options: QueryOptions = QueryOptions.OfflineForever,
): QueryResult<T> {
    val source = remember(key) { SourceOfTruth(reader, writer) }
    return useQuery(key, fetcher, options, source)
}

/**
 * Immutable snapshot handed to a screen by [useQuery]. Carries the discriminated
 * [state] (for QueryContent) plus the flattened flags React-Query users expect and
 * a [refetch] action for pull-to-refresh.
 */
data class QueryResult<T>(
    val state: QueryState<T>,
    val isManualRefetch: Boolean,
    val refetch: () -> Unit,
) {
    val data: T? get() = (state as? QueryState.Success)?.data
    val error: AppError? get() = (state as? QueryState.Failure)?.error
    val isLoading: Boolean
        get() = state is QueryState.Loading || (state is QueryState.Success && state.refreshing)
    val isSuccess: Boolean get() = state is QueryState.Success
    val isError: Boolean get() = state is QueryState.Failure
}
