package dev.roshana.domain.repositories

import androidx.paging.PagingData
import dev.roshana.domain.models.article.Article
import kotlinx.coroutines.flow.Flow

/** PariSa;
coding and smoking ;)
 **/

interface ArticleRepository {
    suspend fun getTechCrunchNews(source: String? = null): Flow<PagingData<Article>>
}