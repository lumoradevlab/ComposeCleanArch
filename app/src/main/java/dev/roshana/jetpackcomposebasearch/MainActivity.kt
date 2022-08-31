package dev.roshana.jetpackcomposebasearch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.roshana.jetpackcomposebasearch.ui.theme.JetpackComposeBaseArchTheme
import dev.roshana.presentation.articleList.ArticleListViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposeBaseArchTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    val viewModel: ArticleListViewModel = hiltViewModel()
    val state = viewModel.characterListState.value
    Text(text = "Hello ${state.errorMessage}!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetpackComposeBaseArchTheme {
        Greeting("Android")
    }
}