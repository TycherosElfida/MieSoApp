package com.mieso.app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mieso.app.ui.home.HomeScreen
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
            // Pass the NavController to HomeScreen so it can navigate
            HomeScreen(navController = navController)
        }
        composable(Screen.Search.route) { SearchScreen() }
        composable(Screen.Orders.route) { OrdersScreen() }
        composable(Screen.Promo.route) { PromoScreen() }
        composable(Screen.Profile.route) { ProfileScreen() }

        // --- NEW DESTINATIONS ---
        composable(
            route = Screen.Menu.route,
            arguments = listOf(navArgument(NavArguments.CATEGORY_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            // The MenuScreen will extract the categoryId from the backStackEntry
            val categoryId = backStackEntry.arguments?.getString(NavArguments.CATEGORY_ID)
            // Placeholder UI for now
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Menu Screen for Category: $categoryId")
            }
        }

        composable(
            route = Screen.MenuItemDetail.route,
            arguments = listOf(navArgument(NavArguments.MENU_ITEM_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val menuItemId = backStackEntry.arguments?.getString(NavArguments.MENU_ITEM_ID)
            // Placeholder UI for now
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Detail Screen for Item: $menuItemId")
            }
        }
    }
}