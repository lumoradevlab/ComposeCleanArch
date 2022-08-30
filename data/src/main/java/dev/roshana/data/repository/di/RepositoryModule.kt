package  dev.roshana.data.repository.di

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.roshana.data.network.api.ApiService
import dev.roshana.data.repository.ArticleRepositoryImpl
import dev.roshana.domain.repositories.ArticleRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Provides
    @Singleton
    internal open fun providesArticleRepository(
        application: Application,
        apiService: ApiService
    ) = ArticleRepositoryImpl(application, apiService) as ArticleRepository

}
