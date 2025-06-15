package com.mieso.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mieso.app.ui.home.HomeScreen
import com.mieso.app.ui.orders.OrdersScreen
import com.mieso.app.ui.profile.ProfileScreen
import com.mieso.app.ui.promo.PromoScreen
import com.mieso.app.ui.search.SearchScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route // Our default starting screen
    ) {
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Search.route) {
            SearchScreen()
        }
        composable(Screen.Orders.route) {
            OrdersScreen()
        }
        composable(Screen.Promo.route) {
            PromoScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}