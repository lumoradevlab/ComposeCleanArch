package dev.lumora.composearch.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Whether the one-time onboarding flow has been completed. Client state, like [ThemeStore]. */
@Singleton
class OnboardingStore @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {
    val isCompleted: Flow<Boolean> = dataStore.data.map { it[COMPLETED] ?: false }

    suspend fun setCompleted(completed: Boolean) {
        dataStore.edit { it[COMPLETED] = completed }
    }

    private companion object {
        val COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
