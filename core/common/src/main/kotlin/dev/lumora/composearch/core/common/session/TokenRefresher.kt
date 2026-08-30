/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.common.session

/**
 * Exchanges a refresh token for a fresh [AuthTokens] pair when the API returns 401.
 *
 * The implementation belongs to the auth feature (it owns the endpoint shape), so
 * `:core:network` depends only on this contract — exactly the same inversion as
 * `NetworkConfig`. The implementation MUST call a client that does NOT install the
 * auth `Authenticator`, otherwise a failed refresh would itself 401 and recurse.
 */
interface TokenRefresher {
    /** @return new tokens, or null when the refresh token is rejected (forces logout). */
    suspend fun refresh(refreshToken: String): AuthTokens?
}
