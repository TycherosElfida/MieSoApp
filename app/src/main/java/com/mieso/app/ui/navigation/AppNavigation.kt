// File: app/src/main/java/com/mieso/app/ui/navigation/AppNavigation.kt

package com.mieso.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mieso.app.ui.admin.AddEditMenuItemScreen
import com.mieso.app.ui.admin.AddEditPromoBannerScreen
import com.mieso.app.ui.admin.AdminCategoriesScreen
import com.mieso.app.ui.admin.AdminDashboardScreen // <-- TAMBAHKAN IMPORT INI
import com.mieso.app.ui.admin.AdminMenuScreen
import com.mieso.app.ui.admin.AdminPromoBannersScreen
import com.mieso.app.ui.cart.CartScreen
import com.mieso.app.ui.checkout.AddAddressScreen
import com.mieso.app.ui.checkout.CheckoutScreen
import com.mieso.app.ui.checkout.DeliveryDetailsScreen
import com.mieso.app.ui.home.HomeScreen
import com.mieso.app.ui.info.HelpCenterScreen
import com.mieso.app.ui.info.PrivacyPolicyScreen
import com.mieso.app.ui.info.TermsAndConditionsScreen
import com.mieso.app.ui.menu.MenuScreen
import com.mieso.app.ui.menu.detail.MenuItemDetailScreen
import com.mieso.app.ui.orders.OrdersScreen
import com.mieso.app.ui.payment.PaymentScreen
import com.mieso.app.ui.profile.ManageAddressesScreen
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

        composable(Screen.Search.route) {
            SearchScreen(navController = navController)
        }

        composable(Screen.Orders.route) { OrdersScreen() }

        composable(Screen.Promo.route) { PromoScreen() }

        composable(Screen.Profile.route) { ProfileScreen(navController = navController) }

        composable(
            route = Screen.Menu.route,
            // This line is essential. It defines the expected argument and its type.
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
            CartScreen(navController = navController)
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(navController = navController)
        }

        composable(Screen.AddAddressScreen.route) {
            AddAddressScreen(navController = navController)
        }

        composable(Screen.DeliveryDetails.route) {
            DeliveryDetailsScreen(navController = navController)
        }

        composable(Screen.Payment.route) {
            PaymentScreen(navController = navController)
        }

        composable(Screen.ManageAddresses.route) {
            ManageAddressesScreen(navController = navController)
        }

        composable(Screen.HelpCenter.route) {
            HelpCenterScreen(navController = navController)
        }
        composable(Screen.TermsAndConditions.route) {
            TermsAndConditionsScreen(navController = navController)
        }
        composable(Screen.PrivacyPolicy.route) {
            PrivacyPolicyScreen(navController = navController)
        }

        // --- Rute Admin ---
        composable(Screen.AdminDashboard.route) { // <-- TAMBAHKAN BLOK INI
            AdminDashboardScreen(navController = navController)
        }

        composable(Screen.AdminMenu.route) {
            AdminMenuScreen(navController)
        }

        composable(
            route = Screen.AddEditMenuItem.route,
            arguments = listOf(navArgument("menuItemId") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            AddEditMenuItemScreen(navController = navController)
        }

        composable(Screen.AdminCategories.route) {
            AdminCategoriesScreen(navController = navController)
        }

        composable(Screen.AdminPromoBanners.route) {
            AdminPromoBannersScreen(navController = navController)
        }

        composable(
            route = Screen.AddEditPromoBanner.route,
            arguments = listOf(navArgument("bannerId") {
                type = NavType.StringType
                nullable = true
            })
        ) {
            AddEditPromoBannerScreen(navController = navController)
        }
    }
}
