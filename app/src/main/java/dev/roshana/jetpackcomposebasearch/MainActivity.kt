package dev.roshana.jetpackcomposebasearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import dagger.hilt.android.AndroidEntryPoint
import dev.roshana.presentation.articleUi.ArticleListViewModel
import dev.roshana.presentation.uiComponent.ui.theme.JetpackComposeBaseArchTheme
import dev.roshana.presentation.welcomeUi.WelcomeViewModel
import javax.inject.Inject

/** PariSa;
coding and smoking ;)
 **/

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