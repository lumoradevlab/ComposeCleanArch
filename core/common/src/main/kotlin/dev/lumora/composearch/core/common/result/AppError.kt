/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.common.result

import java.io.IOException

/**
 * The payload of a failed hook (QueryState.Failure / mutation error). It is NOT
 * something you return from data-layer functions — fetchers just throw, and the
 * hook layer maps the exception to this so the UI can react.
 */
sealed class AppError {
    abstract val message: String?

    /** No connection / network I/O failure — UI can show "you're offline". */
    data class Network(override val message: String? = null) : AppError()

    /** The request was rejected because the session is invalid or expired (HTTP 401/403). */
    data class Unauthorized(override val message: String? = null) : AppError()

    /** The server rejected the input (HTTP 422/400), with per-field messages when supplied. */
    data class Validation(
        override val message: String? = null,
        val fieldErrors: Map<String, List<String>> = emptyMap(),
    ) : AppError()

    /**
     * The API rate-limited us (HTTP 429). The network layer already retries with
     * backoff; this is surfaced only when retries are exhausted, so the UI can show a
     * "slow down" state and optionally count down [retryAfterSeconds].
     */
    data class RateLimited(
        override val message: String? = null,
        val retryAfterSeconds: Long? = null,
    ) : AppError()

    /** The server failed (HTTP 5xx) — retrying later may work. */
    data class Server(override val message: String? = null, val code: Int = 0) : AppError()

    /** Anything else. */
    data class Unknown(override val message: String? = null, val cause: Throwable? = null) : AppError()

    companion object {
        fun from(throwable: Throwable): AppError = when {
            // A raw IOException (fetcher that didn't go through the Resource adapter).
            throwable is IOException -> Network(throwable.message)
            throwable is HttpStatusException -> when (throwable.httpStatusCode) {
                // The Resource adapter already collapsed a transport failure to this.
                HttpStatusException.NETWORK_ERROR_CODE -> Network(throwable.message)
                401, 403 -> Unauthorized(throwable.message)
                400, 422 -> Validation(throwable.message)
                429 -> RateLimited(throwable.message, throwable.retryAfterSeconds)
                in 500..599 -> Server(throwable.message, throwable.httpStatusCode)
                else -> Unknown(throwable.message, throwable)
            }
            else -> Unknown(throwable.message, throwable)
        }
    }
}
