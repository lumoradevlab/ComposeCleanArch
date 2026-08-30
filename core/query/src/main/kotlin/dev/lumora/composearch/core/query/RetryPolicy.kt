package dev.lumora.composearch.core.query

import dev.lumora.composearch.core.common.result.HttpStatusException
import java.io.IOException

/**
 * Shared retry classifier for both reads ([QueryEntry]) and writes ([useMutation]).
 *
 * Only transient failures are worth retrying: no connection, a request timeout, or a
 * 5xx. A 4xx (bad request, unauthorized, conflict, validation) is deterministic —
 * retrying just delays the same error — and for a write it is actively dangerous:
 * re-firing a submit that the server already rejected (or already
 * accepted before the response was lost) risks a duplicate operation. 429 is handled
 * at the network layer, so it is not retried here.
 */
internal fun isRetryable(throwable: Throwable): Boolean = when (throwable) {
    is IOException -> true
    is HttpStatusException -> when (throwable.httpStatusCode) {
        HttpStatusException.NETWORK_ERROR_CODE -> true
        408 -> true
        in 500..599 -> true
        else -> false
    }
    else -> false
}
