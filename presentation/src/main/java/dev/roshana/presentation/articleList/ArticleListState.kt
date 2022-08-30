package dev.roshana.presentation.articleList

import androidx.paging.PagingData
import dev.roshana.domain.models.article.Article
import kotlinx.coroutines.flow.Flow

data class ArticleListState(
    var isLoading: Boolean = false,
    val dataList: Flow<PagingData<Article>>? = null,
    val errorMessage: String = ""
)
