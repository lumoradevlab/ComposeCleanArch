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
 * Appends the NewsAPI key as an `apiKey` query parameter on every request to the API
 * host. It lives in one interceptor so no endpoint signature carries the key and no
 * call site can forget it.
 *
 * The key itself comes from [NetworkConfig.apiKey], which :app reads out of
 * `BuildConfig` (populated from `local.properties`) — so a credential is never
 * committed to source. When [NetworkConfig.apiKey] is blank the request passes through
 * untouched and the server answers 401, which surfaces as `AppError.Unauthorized`.
 *
 * Many APIs authenticate with a header instead; if yours does, set it here with
 * `header("X-Api-Key", key)` rather than adding a query parameter.
 */
class ApiKeyInterceptor @Inject constructor(
    private val config: NetworkConfig,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val key = config.apiKey
        val original = chain.request()
        if (key.isBlank() || original.url.queryParameter(API_KEY_PARAM) != null) {
            return chain.proceed(original)
        }
        val url = original.url.newBuilder().addQueryParameter(API_KEY_PARAM, key).build()
        return chain.proceed(original.newBuilder().url(url).build())
    }

    private companion object {
        const val API_KEY_PARAM = "apiKey"
    }
}
