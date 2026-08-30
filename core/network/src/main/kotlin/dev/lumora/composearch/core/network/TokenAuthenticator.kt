package dev.lumora.composearch.core.network

import dev.lumora.composearch.core.common.session.SessionStore
import dev.lumora.composearch.core.common.session.TokenRefresher
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Refreshes the session when the API answers 401, then retries the request with the
 * new bearer token. If refresh fails (no refresh token, or the server rejects it) the
 * session is cleared — which flips [SessionStore.isLoggedIn] to false, and the app,
 * observing that flow, routes back to login. That is the single, global place a hard
 * 401 turns into a logout.
 *
 * Concurrent 401s are single-flighted through [mutex]: ten requests that all fail with
 * a stale token trigger exactly one refresh. Callers that wake after the rotation see
 * a token different from the one they sent and simply retry with it.
 *
 * Pairs with [AuthInterceptor], which attaches the token on the way out; this handles
 * the token going stale.
 */
class TokenAuthenticator @Inject constructor(
    private val sessionStore: SessionStore,
    private val refresher: TokenRefresher,
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        // We already retried once with a fresh token and still got 401 — stop, don't loop.
        if (response.priorResponseCount() >= MAX_RETRIES) return null

        val staleToken = response.request.header(AUTH_HEADER)?.removePrefix(BEARER_PREFIX)

        // OkHttp calls this off the dispatcher thread; bridge to the suspend store.
        return runBlocking {
            mutex.withLock {
                val current = sessionStore.currentAccessToken()
                // Another request already refreshed while we waited for the lock.
                if (!current.isNullOrBlank() && current != staleToken) {
                    return@withLock response.request.withBearer(current)
                }

                val refreshToken = sessionStore.currentRefreshToken()
                if (refreshToken.isNullOrBlank()) {
                    sessionStore.clear()
                    return@withLock null
                }

                val refreshed = runCatching { refresher.refresh(refreshToken) }.getOrNull()
                if (refreshed == null) {
                    sessionStore.clear()
                    null
                } else {
                    sessionStore.save(refreshed)
                    response.request.withBearer(refreshed.accessToken)
                }
            }
        }
    }

    private fun Request.withBearer(token: String): Request =
        newBuilder().header(AUTH_HEADER, "$BEARER_PREFIX$token").build()

    /** How many times OkHttp has already retried this chain (0 on the first 401). */
    private fun Response.priorResponseCount(): Int {
        var count = 0
        var prior = priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private companion object {
        const val AUTH_HEADER = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val MAX_RETRIES = 1
    }
}
