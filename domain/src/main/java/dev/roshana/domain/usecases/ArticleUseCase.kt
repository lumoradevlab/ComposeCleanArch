package dev.roshana.domain.usecases

import androidx.paging.PagingData
import dev.roshana.domain.models.article.Article
import dev.roshana.domain.repositories.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** PariSa;
coding and smoking ;)
 **/

class ArticleUseCase @Inject constructor(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(source: String? = null): Flow<PagingData<Article>> {
        return articleRepository.getTechCrunchNews(source)
    }
}


