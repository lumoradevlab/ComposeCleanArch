package dev.roshana.jetpackcomposebasearch.navigation

import dev.roshana.jetpackcomposebasearch.R

sealed class NavigationItem(var title: String, var icon: Int, var screen_route: String) {
    object Article : NavigationItem("article", R.drawable.ic_baseline_face,"Article")
}
