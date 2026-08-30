/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.datastore.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.lumora.composearch.core.common.dispatchers.IoDispatcher
import dev.lumora.composearch.core.common.session.SessionStore
import dev.lumora.composearch.core.datastore.DataStoreSessionStore
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    /**
     * One preferences DataStore for the whole app. DataStore permits exactly one active
     * instance per file, so this must stay a singleton — creating a second one for the
     * same file throws at runtime.
     */
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context,
        @IoDispatcher dispatcher: CoroutineDispatcher,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        scope = CoroutineScope(SupervisorJob() + dispatcher),
        produceFile = { context.preferencesDataStoreFile(DATASTORE_NAME) },
    )

    private const val DATASTORE_NAME = "app_preferences"
}

/** Binds the DataStore implementation to the :core:common contract. */
@Module
@InstallIn(SingletonComponent::class)
abstract class SessionStoreModule {

    @Binds
    @Singleton
    abstract fun bindSessionStore(impl: DataStoreSessionStore): SessionStore
}
