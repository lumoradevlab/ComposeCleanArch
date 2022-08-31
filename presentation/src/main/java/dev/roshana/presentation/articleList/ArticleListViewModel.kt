package dev.roshana.presentation.articleList

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.roshana.domain.usecases.ArticleUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val articleUseCase: ArticleUseCase
) : ViewModel() {

    private val _articleListState: MutableState<ArticleListState> = mutableStateOf(
        ArticleListState()
    )
    val characterListState: State<ArticleListState>
        get() = _articleListState

    private val handler = CoroutineExceptionHandler { _, exception ->
        _articleListState.value.isLoading = false
        _articleListState.value = ArticleListState(
            errorMessage = exception.message!!
        )
    }

    init {
        getArticles()
    }


    private fun getArticles() = viewModelScope.launch(handler) {
        val response = articleUseCase().cachedIn(viewModelScope)
        _articleListState.value = ArticleListState(
            dataList = response
        )

    }
}