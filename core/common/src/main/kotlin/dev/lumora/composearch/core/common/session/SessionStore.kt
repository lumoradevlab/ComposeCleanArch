/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.common.session

import kotlinx.coroutines.flow.Flow

/** Auth tokens for the current session. */
data class AuthTokens(
    val accessToken: String,
    val refreshToken: String? = null,
)

/**
 * Contract for reading/writing the session. Declared here (in :core:common) so
 * that :core:network depends on the interface, while :core:datastore provides
 * the implementation — neither depends on the other.
 */
interface SessionStore {
    val accessToken: Flow<String?>
    val isLoggedIn: Flow<Boolean>
    suspend fun currentAccessToken(): String?

    /** The refresh token, read on demand by the network layer's 401 retry. */
    suspend fun currentRefreshToken(): String?
    suspend fun save(tokens: AuthTokens)
    suspend fun clear()
}
