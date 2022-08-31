package  dev.roshana.data.repository.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.roshana.data.repository.ArticleRepositoryImpl
import dev.roshana.domain.repositories.ArticleRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /*@Provides
    @Singleton
    internal open fun providesArticleRepository(
        application: Application,
        apiService: ApiService
    ) = ArticleRepositoryImpl(application, apiService) as ArticleRepository*/

    @Binds
    abstract fun providesArticleRepository(impl: ArticleRepositoryImpl): ArticleRepository

}
