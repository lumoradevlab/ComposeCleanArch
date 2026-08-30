package dev.lumora.composearch.core.common.result

/**
 * Implemented by exceptions that carry an HTTP status, so [AppError.from] can map a
 * failure by status code without :core:common depending on :core:network. The network
 * layer's `ResponseException` implements this; nothing else needs to.
 *
 * Two negative [httpStatusCode] values are synthetic (no real HTTP response arrived) —
 * see [NETWORK_ERROR_CODE] and [UNKNOWN_ERROR_CODE] — so a transport failure can still
 * be mapped to a typed error.
 */
interface HttpStatusException {
    val httpStatusCode: Int

    /** Seconds the client should wait before retrying (from a `Retry-After` header), if known. */
    val retryAfterSeconds: Long? get() = null

    companion object {
        /** No HTTP response at all — offline, DNS, timeout. Maps to [AppError.Network]. */
        const val NETWORK_ERROR_CODE = -1

        /** A non-transport failure with no HTTP status. Maps to [AppError.Unknown]. */
        const val UNKNOWN_ERROR_CODE = -2
    }
}
