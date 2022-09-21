package dev.roshana.presentation.articleUi

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.roshana.presentation.navigation.Articles
import kotlinx.coroutines.ExperimentalCoroutinesApi

/** PariSa;
coding and smoking ;)
 **/

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
fun NavGraphBuilder.articleGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = Articles.ARTICLESLIST,
        route = Articles.ARTICLEGRAPH
    ) {

        composable(
            route = Articles.ARTICLESLIST
        ) {
            ArticlesListScreen()
        }


    }
}
