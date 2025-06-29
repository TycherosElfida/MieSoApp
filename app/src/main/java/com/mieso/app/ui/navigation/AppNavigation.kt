package com.mieso.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember // <-- TAMBAHAN: Import yang diperlukan
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.mieso.app.ui.admin.AddEditMenuItemScreen
import com.mieso.app.ui.admin.AddEditPromoBannerScreen
import com.mieso.app.ui.admin.AdminCategoriesScreen
import com.mieso.app.ui.admin.AdminDashboardScreen
import com.mieso.app.ui.admin.AdminMenuScreen
import com.mieso.app.ui.admin.AdminPromoBannersScreen
import com.mieso.app.ui.cart.CartScreen
import com.mieso.app.ui.checkout.AddAddressScreen
import com.mieso.app.ui.checkout.CheckoutScreen
import com.mieso.app.ui.checkout.DeliveryDetailsScreen
import com.mieso.app.ui.checkout.viewmodel.CheckoutViewModel
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

// Definisikan rute untuk grafik navigasi checkout
object CheckoutGraph {
    const val route = "checkout_graph"
}

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

        // ==========================================================
        // ===        GRAFIK NAVIGASI CHECKOUT (PERBAIKAN)        ===
        // ==========================================================
        navigation(
            startDestination = Screen.Checkout.route,
            route = CheckoutGraph.route
        ) {
            composable(Screen.Checkout.route) { backStackEntry ->
                // Dapatkan NavBackStackEntry dari grafik induk
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(CheckoutGraph.route)
                }
                // Buat ViewModel dengan scope ke grafik induk
                val checkoutViewModel: CheckoutViewModel = hiltViewModel(parentEntry)

                CheckoutScreen(
                    navController = navController,
                    viewModel = checkoutViewModel // Berikan ViewModel yang sudah dibuat
                )
            }
            composable(Screen.DeliveryDetails.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(CheckoutGraph.route)
                }
                val checkoutViewModel: CheckoutViewModel = hiltViewModel(parentEntry)

                DeliveryDetailsScreen(
                    navController = navController,
                    viewModel = checkoutViewModel // Berikan ViewModel yang sama
                )
            }
            composable(Screen.Payment.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(CheckoutGraph.route)
                }
                val checkoutViewModel: CheckoutViewModel = hiltViewModel(parentEntry)

                PaymentScreen(
                    navController = navController,
                    viewModel = checkoutViewModel // Berikan ViewModel yang sama
                )
            }
        }

        composable(Screen.AddAddressScreen.route) {
            AddAddressScreen(navController = navController)
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
        composable(Screen.AdminDashboard.route) {
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