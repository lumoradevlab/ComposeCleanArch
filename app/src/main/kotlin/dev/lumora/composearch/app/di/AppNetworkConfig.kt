package dev.lumora.composearch.app.di

import dev.lumora.composearch.app.BuildConfig
import dev.lumora.composearch.core.network.CertificatePin
import dev.lumora.composearch.core.network.NetworkConfig
import javax.inject.Inject

/**
 * App-supplied [NetworkConfig]. The base URL comes from the product flavor via
 * `BuildConfig.BASE_URL`, the credential from `BuildConfig.API_KEY` (populated from
 * `local.properties`), and logging is on only in debug builds — so no environment,
 * URL, or secret is hardcoded in :core:network.
 */
class AppNetworkConfig @Inject constructor() : NetworkConfig {

    override val baseUrl: String = BuildConfig.BASE_URL

    override val apiKey: String = BuildConfig.API_KEY

    override val enableLogging: Boolean = BuildConfig.DEBUG

    /**
     * TODO(security): pin your prod host before shipping. Empty = pinning disabled.
     * Capture the leaf + a backup public-key hash and return them only for the prod
     * flavor, e.g.:
     *   listOf(CertificatePin("api.example.com", listOf("sha256/AAAA…", "sha256/BBBB…")))
     * Pin at least two keys, or a routine cert rotation bricks every installed app.
     */
    override val certificatePins: List<CertificatePin> = emptyList()
}
