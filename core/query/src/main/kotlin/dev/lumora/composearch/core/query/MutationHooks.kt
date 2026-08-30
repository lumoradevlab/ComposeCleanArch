/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.query

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import dev.lumora.composearch.core.common.result.AppError
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Per-call-site options for [useMutation]. */
data class MutationOptions<R>(
    /** Keys to invalidate on success, so affected queries refetch (e.g. a list after an edit). */
    val invalidateKeys: List<String> = emptyList(),
    val onSuccess: (R) -> Unit = {},
    val onError: (AppError) -> Unit = {},
    /** Retries on failure (0 = no retry). Delay grows linearly: retryDelay * attempt. */
    val retryCount: Int = 0,
    val retryDelay: Long = 1000L,
)

/** Internal state the mutation hook tracks across attempts. */
data class MutationState<R>(
    val isLoading: Boolean = false,
    val data: R? = null,
    val error: AppError? = null,
)

/**
 * Handle returned by [useMutation] — drives a write (buy, withdraw, …) and exposes
 * its progress. Call it like a function: `buy(order)`.
 */
data class MutationResult<P, R>(
    val mutate: (P) -> Unit,
    val data: R?,
    val error: AppError?,
    val isLoading: Boolean,
    val isSuccess: Boolean,
    val isError: Boolean,
) {
    operator fun invoke(params: P) = mutate(params)
}

/**
 * The write hook. [mutationFn] just calls the API and returns its result (or
 * throws) — exactly like a fetcher. On success it invalidates
 * [MutationOptions.invalidateKeys] through the [QueryClient] from [LocalQueryClient],
 * so affected queries refetch (and re-cache to Room) automatically; on failure it
 * maps the exception to [AppError] and optionally retries.
 *
 * ```
 * val bookmark = useMutation<String, Unit>(
 *     mutationFn = { id -> repo.bookmark(id) },
 *     options = MutationOptions(invalidateKeys = listOf(ArticleKeys.BOOKMARKS)),
 * )
 * Button(onClick = { bookmark(id) }, enabled = !bookmark.isLoading) { Text("Save") }
 * ```
 */
@Composable
fun <P, R> useMutation(
    mutationFn: suspend (P) -> R,
    options: MutationOptions<R> = MutationOptions(),
    scope: CoroutineScope = rememberCoroutineScope(),
): MutationResult<P, R> {
    val client = LocalQueryClient.current
    var state by remember { mutableStateOf(MutationState<R>()) }

    // Keep `mutate` a stable reference across recompositions (callers pass a fresh
    // `mutationFn`/`options` lambda each time, which would otherwise rebuild it every
    // frame). It reads the latest fn/options at call time via rememberUpdatedState.
    val currentFn = rememberUpdatedState(mutationFn)
    val currentOptions = rememberUpdatedState(options)

    // Single-flight guard: while a mutation is in flight, further mutate() calls are dropped,
    // so double-tapping a submit button can't run the write (and its onSuccess /
    // invalidations) more than once. Cleared when the job completes.
    val inFlight = remember { AtomicReference<Job?>(null) }

    val mutate: (P) -> Unit = remember {
        { params: P ->
            val running = inFlight.get()
            if (running == null || !running.isActive) {
                val job = scope.launch(start = CoroutineStart.LAZY) {
                    val fn = currentFn.value
                    val opts = currentOptions.value
                    state = state.copy(isLoading = true, error = null)

                    var attempt = 0
                    var lastError: AppError? = null
                    while (attempt <= opts.retryCount) {
                        try {
                            val result = fn(params)
                            state = MutationState(isLoading = false, data = result, error = null)
                            opts.invalidateKeys.forEach(client::invalidate)
                            opts.onSuccess(result)
                            return@launch
                        } catch (cancellation: CancellationException) {
                            throw cancellation
                        } catch (throwable: Throwable) {
                            lastError = AppError.from(throwable)
                            // Only retry transient failures. Re-firing a write on a 4xx
                            // (validation, conflict, auth) is pointless and risks a
                            // duplicate submit (a payment, an order, a transfer) — so a deterministic error
                            // stops here immediately. See [isRetryable].
                            if (attempt < opts.retryCount && isRetryable(throwable)) {
                                delay(opts.retryDelay * (attempt + 1))
                            } else {
                                break
                            }
                        }
                        attempt++
                    }

                    state = MutationState(isLoading = false, data = null, error = lastError)
                    lastError?.let(opts.onError)
                }
                job.invokeOnCompletion { inFlight.compareAndSet(job, null) }
                // Install as THE in-flight mutation; if a concurrent call already claimed the
                // slot, drop this one (start only the winner).
                if (inFlight.compareAndSet(running, job)) job.start() else job.cancel()
            }
            Unit
        }
    }

    return MutationResult(
        mutate = mutate,
        data = state.data,
        error = state.error,
        isLoading = state.isLoading,
        isSuccess = state.data != null && state.error == null,
        isError = state.error != null,
    )
}
