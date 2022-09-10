package dev.roshana.presentation.articleUi

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun ArticlesListScreen() {

    val context = LocalContext.current
    val viewModel: ArticleListViewModel = hiltViewModel()
    val state = viewModel.articleListState.value
    val lazyListState = rememberLazyListState()
    //val lazyGridState = rememberLazyGridState()

    val showColumn by remember {
        mutableStateOf(true)
    }

    val scope = rememberCoroutineScope()

    val articles = state.dataList?.collectAsLazyPagingItems()
    Scaffold {
        Box(modifier = Modifier.padding(it)) {

            if (state.errorMessage.isNotEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.errorMessage)
                }
            } else {
                articles?.let { items ->

                    if (showColumn) {
                    ArticlesListColumn(items = items, listState = lazyListState) { articleId ->

                    }
                }
                }
            }
        }
    }
}
