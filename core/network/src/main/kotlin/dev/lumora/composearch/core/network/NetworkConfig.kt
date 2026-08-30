/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network

/**
 * Per-build network configuration. The :app module provides the implementation
 * (from BuildConfig / the product flavor), so :core:network stays free of any
 * hardcoded environment or URL.
 *
 * Adding a second backend (a CMS, an auth service)? Add its base URL here, expose it
 * as a `buildConfigField` on the flavor, and create a qualified Retrofit for it —
 * never hardcode a host inside a repository.
 */
interface NetworkConfig {
    val baseUrl: String

    /**
     * Credential for the backend, attached by [ApiKeyInterceptor]. :app reads it from
     * `BuildConfig`, which the build populates from `local.properties` — so the key is
     * never committed. Blank means "no key configured"; requests then go out unsigned.
     *
     * An API you authenticate with a user session instead of a static key won't need
     * this — delete it and the interceptor, and let [AuthInterceptor] do the work.
     */
    val apiKey: String

    /** HTTP bodies are logged only when this is on — keep it to debug builds. */
    val enableLogging: Boolean

    /**
     * SHA-256 public-key pins per host. An empty list disables pinning — appropriate
     * for dev/staging against rotating certs, but prod should always pin.
     */
    val certificatePins: List<CertificatePin>
}

/**
 * Certificate pins for a single host. [hostPattern] is an OkHttp host pattern
 * (`"api.example.com"` or `"*.example.com"`); [sha256Pins] are the pinned public-key
 * hashes in `"sha256/BASE64"` form.
 *
 * Always pin at least two keys (the live cert plus a backup/next key), or a routine
 * certificate rotation will brick every installed app until they update.
 */
data class CertificatePin(
    val hostPattern: String,
    val sha256Pins: List<String>,
)
