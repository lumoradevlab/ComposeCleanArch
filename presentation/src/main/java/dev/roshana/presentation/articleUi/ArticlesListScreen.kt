package dev.roshana.presentation.articleUi

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.ExperimentalCoroutinesApi

/** PariSa;
coding and smoking ;)
 **/

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
    val toolBarHeight by animateDpAsState(
        targetValue = min(56.dp, 100.dp),
    )


    val articles = state.dataList?.collectAsLazyPagingItems()
    Scaffold(topBar = {
        TopAppBar(
            modifier = Modifier
                .height(toolBarHeight),
            title = {
                Text(
                    text = "Articles",
                    color = Color.Black
                )
            },
            backgroundColor = Color.White,
        )
    }) {

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
