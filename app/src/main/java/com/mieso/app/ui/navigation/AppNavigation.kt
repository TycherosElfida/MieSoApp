package com.mieso.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mieso.app.ui.cart.CartScreen
import com.mieso.app.ui.checkout.CheckoutScreen
import com.mieso.app.ui.home.HomeScreen
import com.mieso.app.ui.menu.MenuScreen
import com.mieso.app.ui.menu.detail.MenuItemDetailScreen
import com.mieso.app.ui.orders.OrdersScreen
import com.mieso.app.ui.profile.ProfileScreen
import com.mieso.app.ui.promo.PromoScreen
import com.mieso.app.ui.search.SearchScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Search.route) { SearchScreen() }

        composable(Screen.Orders.route) { OrdersScreen() }

        composable(Screen.Promo.route) { PromoScreen() }

        composable(Screen.Profile.route) { ProfileScreen() }

        composable(
            route = Screen.Menu.route,
            arguments = listOf(navArgument(NavArguments.CATEGORY_ID) { type = NavType.StringType })
        ) {
            MenuScreen(navController = navController)
        }

        composable(
            route = Screen.MenuItemDetail.route,
            arguments = listOf(navArgument(NavArguments.MENU_ITEM_ID) { type = NavType.StringType })
        ) {
            MenuItemDetailScreen(navController = navController)
        }

        composable(Screen.Cart.route) {
            CartScreen()
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(navController = navController)
        }
    }
}