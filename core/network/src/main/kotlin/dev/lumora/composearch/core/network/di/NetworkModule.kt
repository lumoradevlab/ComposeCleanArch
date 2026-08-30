/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network.di

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.lumora.composearch.core.network.ApiKeyInterceptor
import dev.lumora.composearch.core.network.AuthInterceptor
import dev.lumora.composearch.core.network.CertificatePin
import dev.lumora.composearch.core.network.NetworkConfig
import dev.lumora.composearch.core.network.RateLimitInterceptor
import dev.lumora.composearch.core.network.ResourceCallAdapterFactory
import dev.lumora.composearch.core.network.ResponseErrorParser
import dev.lumora.composearch.core.network.TokenAuthenticator
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * The shared HTTP stack: one OkHttpClient and one Retrofit for the whole app. Feature
 * modules never build their own — they take this [Retrofit] and call `create(…)` on it
 * (see [ArticleModule]).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        apiKeyInterceptor: ApiKeyInterceptor,
        rateLimitInterceptor: RateLimitInterceptor,
        authenticator: TokenAuthenticator,
        config: NetworkConfig,
    ): OkHttpClient = OkHttpClient.Builder()
        // Per-stage timeouts cap each phase; callTimeout is the hard ceiling on the WHOLE
        // call (incl. the rate-limit interceptor's retry backoff and the 401 refresh), so
        // no request can hang a coroutine/spinner indefinitely.
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(authInterceptor)
        .addInterceptor(apiKeyInterceptor)
        // 429 -> back off and retry a couple of times before surfacing the error.
        .addInterceptor(rateLimitInterceptor)
        // 401 -> refresh token and retry (or log out). Single-flighted internally.
        .authenticator(authenticator)
        .apply {
            certificatePinnerOrNull(config.certificatePins)?.let { certificatePinner(it) }
            if (config.enableLogging) {
                addInterceptor(
                    HttpLoggingInterceptor { message -> Log.i("okhttp.OkHttpClient", message) }
                        .apply { level = HttpLoggingInterceptor.Level.BODY },
                )
            }
        }
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson,
        config: NetworkConfig,
        errorParser: ResponseErrorParser,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(config.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        // Every endpoint returns Resource<T>; this parses success/error/offline into it
        // centrally, so there is no per-call safeApiCall anywhere in the app.
        .addCallAdapterFactory(ResourceCallAdapterFactory(errorParser))
        .build()

    // Single TCP/TLS handshake; fail fast on a dead host.
    private const val CONNECT_TIMEOUT_SECONDS = 15L
    // Gap between bytes while reading the response.
    private const val READ_TIMEOUT_SECONDS = 30L
    // Gap between bytes while writing the request body.
    private const val WRITE_TIMEOUT_SECONDS = 30L
    // Hard ceiling on the entire call, including interceptor retries/backoff.
    private const val CALL_TIMEOUT_SECONDS = 60L

    /** Builds a pinner from config, or null when no pins are configured (pinning off). */
    private fun certificatePinnerOrNull(pins: List<CertificatePin>): CertificatePinner? {
        if (pins.isEmpty()) return null
        val builder = CertificatePinner.Builder()
        pins.forEach { pin ->
            pin.sha256Pins.forEach { hash -> builder.add(pin.hostPattern, hash) }
        }
        return builder.build()
    }
}
