/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.app.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.lumora.composearch.core.common.session.TokenRefresher
import dev.lumora.composearch.core.network.NetworkConfig
import javax.inject.Singleton

/**
 * Binds the app-level implementations of the interfaces that :core modules declare but
 * intentionally don't implement (so they stay environment-agnostic). This is the last
 * mile that makes the network graph fully constructable.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindNetworkConfig(impl: AppNetworkConfig): NetworkConfig

    @Binds
    @Singleton
    abstract fun bindTokenRefresher(impl: SessionExpiryTokenRefresher): TokenRefresher
}
