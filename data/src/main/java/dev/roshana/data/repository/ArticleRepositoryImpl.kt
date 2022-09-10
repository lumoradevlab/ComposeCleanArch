package  dev.roshana.data.repository

import android.app.Application
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.roshana.data.network.api.ApiService
import dev.roshana.data.repository.pagingSource.ArticlePagingSource
import dev.roshana.domain.models.article.Article
import dev.roshana.domain.repositories.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepositoryImpl @Inject constructor(
    val apiService: ApiService
) : ArticleRepository {
    override suspend fun getTechCrunchNews(source: String?): Flow<PagingData<Article>> {

        return Pager(PagingConfig(pageSize = 50)) {
            ArticlePagingSource(apiService = apiService)
        }.flow
    }

}
