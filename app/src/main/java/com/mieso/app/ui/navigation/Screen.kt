package com.mieso.app.ui.navigation

sealed class Screen(val route: String) {
    // Routes for the main bottom navigation bar
    object Home : Screen("home_screen")
    object Search : Screen("search_screen")
    object Orders : Screen("orders_screen")
    object Promo : Screen("promo_screen")
    object Profile : Screen("profile_screen")

    // We can add other routes here as we build them, for example:
    // object Onboarding : Screen("onboarding_screen")
    // object Login : Screen("login_screen")
    // object MenuItemDetail : Screen("menu_item_detail_screen/{itemId}") {
    //     fun createRoute(itemId: String) = "menu_item_detail_screen/$itemId"
    // }
}
