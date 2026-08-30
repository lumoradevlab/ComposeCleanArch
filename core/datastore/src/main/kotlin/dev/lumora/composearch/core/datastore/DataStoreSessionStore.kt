/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.lumora.composearch.core.common.session.AuthTokens
import dev.lumora.composearch.core.common.session.SessionStore
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * DataStore-backed [SessionStore] — the implementation side of the contract declared in
 * :core:common, so :core:network depends only on the interface.
 *
 * ## Encryption
 * Tokens are stored as plain preferences here. That is fine for a template and for apps
 * whose token is short-lived and low-value; it is NOT fine for a banking/finance app.
 * To harden it, encrypt the values before writing (Tink AEAD with the keyset wrapped by
 * a master key in the Android Keystore) and decrypt on read — introduce a `TokenCipher`
 * interface and wrap the two accessors below. Nothing outside this class changes.
 */
@Singleton
class DataStoreSessionStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : SessionStore {

    override val accessToken: Flow<String?> =
        dataStore.data.map { it[ACCESS_TOKEN]?.takeIf(String::isNotBlank) }

    override val isLoggedIn: Flow<Boolean> = accessToken.map { !it.isNullOrBlank() }

    override suspend fun currentAccessToken(): String? = accessToken.first()

    override suspend fun currentRefreshToken(): String? =
        dataStore.data.first()[REFRESH_TOKEN]?.takeIf(String::isNotBlank)

    override suspend fun save(tokens: AuthTokens) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = tokens.accessToken
            tokens.refreshToken?.let { prefs[REFRESH_TOKEN] = it }
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
        }
    }

    private companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    }
}
