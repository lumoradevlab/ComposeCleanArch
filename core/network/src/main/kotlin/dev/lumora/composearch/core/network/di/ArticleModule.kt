/*
 * ComposeCleanArch — Copyright (c) 2026 Parisa Hazhirghader
 *
 * Licensed under the GNU Affero General Public License v3.0 (see LICENSE).
 * A commercial license is available for proprietary use — see NOTICE.
 */
package dev.lumora.composearch.core.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.lumora.composearch.core.network.apis.ArticleApi
import javax.inject.Singleton
import retrofit2.Retrofit

/**
 * Binds the article area onto the DI graph — one small module per area, created from
 * the shared [Retrofit] that [NetworkModule] provides. Copy this file (four lines of
 * real content) for each new area rather than growing one giant module.
 */
@Module
@InstallIn(SingletonComponent::class)
object ArticleModule {

    @Provides
    @Singleton
    fun provideArticleApi(retrofit: Retrofit): ArticleApi =
        retrofit.create(ArticleApi::class.java)
}
