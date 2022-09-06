package dev.roshana.jetpackcomposebasearch

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.roshana.jetpackcomposebasearch.navigation.Articles
import dev.roshana.jetpackcomposebasearch.navigation.BottomNavigationBar
import dev.roshana.jetpackcomposebasearch.navigation.Locations

@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun BaseContent() {
    val navController = rememberNavController()

    val bottomScreens = listOf(
        Articles.ARTICLESLIST,
        Locations.LOCATIONLIST
    )

    val showNavBar = navController
        .currentBackStackEntryAsState().value?.destination?.route in bottomScreens

    Scaffold(
        bottomBar = {
            if (showNavBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { padding ->

        //MainNavigation(navhostController = navController, modifier = Modifier.padding(padding))
    }
}
