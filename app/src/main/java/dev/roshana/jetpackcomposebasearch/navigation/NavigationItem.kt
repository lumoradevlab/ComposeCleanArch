package dev.roshana.jetpackcomposebasearch.navigation

import dev.roshana.jetpackcomposebasearch.R

sealed class NavigationItem(var title: String, var screen_route: String) {
    object Article : NavigationItem("article", "Article")
}
