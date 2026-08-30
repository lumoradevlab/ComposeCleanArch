/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.di

import dev.lumora.composearch.core.common.session.AuthTokens
import dev.lumora.composearch.core.common.session.TokenRefresher
import javax.inject.Inject

/**
 * Default [TokenRefresher]: treats a 401 as a hard session expiry.
 *
 * Returning null makes `TokenAuthenticator` clear the session, which flips
 * `SessionStore.isLoggedIn` to false — and the app shell, observing that flow, returns
 * to login on its own. Screens never navigate to login themselves.
 *
 * When your backend has a refresh endpoint, implement it here: call it with a client
 * that does NOT install the authenticator (otherwise a failed refresh 401s and
 * recurses) and return the new tokens.
 */
class SessionExpiryTokenRefresher @Inject constructor() : TokenRefresher {
    override suspend fun refresh(refreshToken: String): AuthTokens? = null
}
