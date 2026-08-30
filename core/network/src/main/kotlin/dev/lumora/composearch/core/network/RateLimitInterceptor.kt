/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network

import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Retries HTTP 429 (rate limited) a few times before giving up. The prod API enforces
 * per-endpoint limits (`x-ratelimit-limit`), so a brief burst can trip it; a short
 * backoff usually clears it without bothering the user.
 *
 * Honours a `Retry-After` header (seconds) when present, otherwise uses exponential
 * backoff. Waits are capped at [MAX_WAIT_SECONDS] so we never block a network thread
 * for long; if 429 persists past [MAX_RETRIES] the response is returned as-is and the
 * call adapter maps it to a [ResponseError] (which `AppError.from` turns into
 * [dev.lumora.composearch.core.common.result.AppError.RateLimited]).
 *
 * Runs on OkHttp's worker thread, so a blocking sleep here is fine.
 */
class RateLimitInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response = chain.proceed(chain.request())

        while (response.code == HTTP_TOO_MANY_REQUESTS && attempt < MAX_RETRIES) {
            val waitMs = response.retryAfterMillis() ?: backoffMillis(attempt)
            response.close() // free the connection before re-issuing the request
            try {
                Thread.sleep(waitMs)
            } catch (interrupted: InterruptedException) {
                Thread.currentThread().interrupt()
                return chain.proceed(chain.request())
            }
            attempt++
            response = chain.proceed(chain.request())
        }
        return response
    }

    /** `Retry-After` as whole seconds, clamped to a sane ceiling. Null if absent/unparseable. */
    private fun Response.retryAfterMillis(): Long? =
        header("Retry-After")?.toLongOrNull()
            ?.coerceIn(0, MAX_WAIT_SECONDS)
            ?.let { it * 1_000 }

    private fun backoffMillis(attempt: Int): Long =
        (BASE_BACKOFF_MS shl attempt).coerceAtMost(MAX_WAIT_SECONDS * 1_000)

    private companion object {
        const val HTTP_TOO_MANY_REQUESTS = 429
        const val MAX_RETRIES = 2
        const val BASE_BACKOFF_MS = 500L
        const val MAX_WAIT_SECONDS = 5L
    }
}
