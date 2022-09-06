package dev.roshana.jetpackcomposebasearch.navigation

import androidx.annotation.DrawableRes
import dev.roshana.jetpackcomposebasearch.R
import dev.roshana.presentation.navigation.Articles
import dev.roshana.presentation.navigation.Locations

sealed class NavigationItem(
    var title: String,
    var screen_route: String,
    @DrawableRes var icon: Int
) {
    object ArticlesScreen : NavigationItem(
        title = "Article",
        screen_route = Articles.ARTICLESLIST,
        icon = R.drawable.ic_baseline_settings
    )

    object LocationsScreen : NavigationItem(
        title = "Locations",
        screen_route = Locations.LOCATIONLIST,
        R.drawable.ic_baseline_add_location
    )
}
