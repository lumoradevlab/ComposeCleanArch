package dev.roshana.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.roshana.domain.repositories.ArticleRepository
import dev.roshana.domain.repositories.DataStoreRepository
import dev.roshana.domain.usecases.ArticleUseCase
import dev.roshana.domain.usecases.DataStoreUseCase
import javax.inject.Singleton

/** PariSa;
coding and smoking ;)
 **/

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
    fun providesArticlesUseCase(articleRepository: ArticleRepository) =
        ArticleUseCase(articleRepository)

    @Provides
    @Singleton
    fun providesDataStoreUseCase(dataStoreRepository: DataStoreRepository) =
        DataStoreUseCase(dataStoreRepository)

}