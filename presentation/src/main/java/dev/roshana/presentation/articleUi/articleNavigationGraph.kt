package dev.roshana.presentation.articleUi

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.roshana.presentation.navigation.Articles
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
            route = Articles.ARTICLESLIST,
            enterTransition = { _, _ ->
                slideInVertically(initialOffsetY = { +1000 }, animationSpec = spring())
            },
        ) {

        }


    }
}
