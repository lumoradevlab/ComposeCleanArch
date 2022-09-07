package dev.roshana.jetpackcomposebasearch.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import dev.roshana.presentation.articleUi.articleGraph
import dev.roshana.presentation.locationUi.locationGraph
import dev.roshana.presentation.navigation.Articles
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun MainNavigation(navHostController: NavHostController, modifier: Modifier = Modifier) {

    NavHost(
        navController = navHostController,
        startDestination = Articles.ARTICLEGRAPH, modifier = modifier
    ) {
        articleGraph(navHostController = navHostController)
        locationGraph(navHostController = navHostController)
    }
}