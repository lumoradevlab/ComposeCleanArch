package dev.roshana.presentation.articleUi

import androidx.paging.PagingData
import dev.roshana.domain.models.DataState
import dev.roshana.domain.models.article.Article
import kotlinx.coroutines.flow.Flow

/** PariSa;
coding and smoking ;)
 **/

class ArticleListState(
    var loading: Boolean? = null,
    var dataList: Flow<PagingData<Article>>? = null,
    var errorMsg: String? = ""
) : DataState<Flow<PagingData<Article>>>(
    isLoading = loading == false,
    data = dataList,
    errorMessage = errorMsg!!
)


/* var isLoading: Boolean = false,
 val dataList: Flow<PagingData<Article>>? = null,
 val errorMessage: String = ""*/

