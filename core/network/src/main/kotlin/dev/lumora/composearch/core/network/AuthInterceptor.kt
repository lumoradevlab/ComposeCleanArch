package dev.lumora.composearch.core.network

import dev.lumora.composearch.core.common.session.SessionStore
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the headers every API call needs:
 *  - `Authorization: Bearer <token>` when a session exists (read from [SessionStore]);
 *  - `Accept: application/json` so the server returns JSON;
 *  - `Accept-Language` from the device locale, for localized messages.
 *
 * The content-negotiation headers are only set when the endpoint hasn't already
 * specified its own (via Retrofit `@Headers`), so a special-case call can still
 * override them. `Content-Type` is intentionally NOT set here — Retrofit's converter
 * sets it on requests that actually have a body.
 *
 * The bearer token is attached ONLY to the API host from [NetworkConfig.baseUrl]. If
 * you add a second backend (a CMS, an image host) on another host, its calls stay
 * unauthenticated automatically — leaking a session token cross-host is exactly what
 * this guard prevents.
 *
 * OkHttp interceptors are synchronous, so we bridge to the suspend store with
 * runBlocking on the network thread — cheap, since the store memoizes the token in
 * memory (only the first read after process start touches DataStore).
 */
class AuthInterceptor @Inject constructor(
    private val sessionStore: SessionStore,
    config: NetworkConfig,
) : Interceptor {

    /** Host of the main app API — the only host the session token is ever sent to. */
    private val apiHost: String? = config.baseUrl.toHttpUrlOrNull()?.host

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val builder = original.newBuilder()

        if (original.header("Accept") == null) {
            builder.header("Accept", "application/json")
        }
        if (original.header("Accept-Language") == null) {
            builder.header("Accept-Language", acceptLanguage())
        }

        // Never attach the bearer token to a different host — see the class docs.
        if (apiHost != null && original.url.host != apiHost) {
            return chain.proceed(builder.build())
        }

        val token = runBlocking { sessionStore.currentAccessToken() }
        if (!token.isNullOrBlank()) {
            builder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(builder.build())
    }

    /** The device language as an IETF tag (e.g. `en-US`), falling back to English. */
    private fun acceptLanguage(): String =
        Locale.getDefault().toLanguageTag().ifBlank { "en-US" }
}
