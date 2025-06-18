package com.mieso.app.ui.navigation

object NavArguments {
    const val CATEGORY_ID = "categoryId"
    const val MENU_ITEM_ID = "menuItemId"
}

sealed class Screen(val route: String) {

    object Home : Screen("home_screen")

    object Search : Screen("search_screen")

    object Orders : Screen("orders_screen")

    object Promo : Screen("promo_screen")

    object Profile : Screen("profile_screen")

    object Cart : Screen("cart_screen")

    object Menu : Screen("menu_screen/{${NavArguments.CATEGORY_ID}}") {
        fun createRoute(categoryId: String) = "menu_screen/$categoryId"
    }

    object MenuItemDetail : Screen("menu_item_detail_screen/{${NavArguments.MENU_ITEM_ID}}") {
        fun createRoute(menuItemId: String) = "menu_item_detail_screen/$menuItemId"
    }

    object Checkout : Screen("checkout_screen")

    object AddAddress : Screen("add_address_screen")
}