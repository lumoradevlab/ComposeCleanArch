package dev.roshana.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.roshana.domain.repositories.ArticleRepository
import dev.roshana.domain.usecases.ArticleUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    /*@Provides
    @Singleton
    internal fun providesArticleUseCase(
        articleRepository: ArticleRepository
    ) = ArticleUseCase(articleRepository)*/

    @Provides
    @Singleton
    fun providesCharactersUseCase(articleRepository: ArticleRepository) =
        ArticleUseCase(articleRepository)
}