package dev.roshana.presentation.locationUi

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.material.ExperimentalMaterialApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import dev.roshana.presentation.navigation.Articles
import dev.roshana.presentation.navigation.Locations
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@ExperimentalAnimationApi
@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.locationGraph(
    navHostController: NavHostController
) {
    navigation(
        startDestination = Locations.LOCATIONLIST,
        route = Locations.LOCATIONGRAPH
    ) {

        composable(route = Locations.LOCATIONLIST){
            LocationScreen()
        }


    }
}
