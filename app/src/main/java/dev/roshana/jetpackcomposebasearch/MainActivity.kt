package dev.roshana.jetpackcomposebasearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import dev.roshana.jetpackcomposebasearch.ui.theme.JetpackComposeBaseArchTheme
import dev.roshana.presentation.articleList.ArticleListViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    @OptIn(
        ExperimentalMaterialApi::class,
        ExperimentalAnimationApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeBaseArchTheme {
                BaseContent()
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val viewModel: ArticleListViewModel = hiltViewModel()
    val state = viewModel.articleListState.value
    val list = state.dataList?.collectAsLazyPagingItems()
    val size = list?.itemSnapshotList?.items
    Text(text = "Hello ${size}!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackComposeBaseArchTheme {
        Greeting("Android")
    }
}