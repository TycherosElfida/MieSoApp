package com.mieso.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.mieso.app.ui.admin.AdminManageOrdersScreen
import com.mieso.app.ui.admin.AdminMenuScreen
import com.mieso.app.ui.admin.AdminPromoBannersScreen
import com.mieso.app.ui.admin.viewmodel.AdminViewModel
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

// Rute untuk grafik navigasi
object CheckoutGraph {
    const val route = "checkout_graph"
}
object AdminGraph {
    const val route = "admin_graph"
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // --- Rute Utama ---
        composable(Screen.Home.route) { HomeScreen(navController = navController) }
        composable(Screen.Search.route) { SearchScreen(navController = navController) }
        composable(Screen.Orders.route) { OrdersScreen() }
        composable(Screen.Profile.route) { ProfileScreen(navController = navController) }
        composable(Screen.Promo.route) { PromoScreen() }
        composable(route = Screen.Menu.route, arguments = listOf(navArgument(NavArguments.CATEGORY_ID) { type = NavType.StringType })) { MenuScreen(navController = navController) }
        composable(route = Screen.MenuItemDetail.route, arguments = listOf(navArgument(NavArguments.MENU_ITEM_ID) { type = NavType.StringType })) { MenuItemDetailScreen(navController = navController) }
        composable(Screen.Cart.route) { CartScreen(navController = navController) }

        // --- Grafik Navigasi Checkout ---
        navigation(
            startDestination = Screen.Checkout.route,
            route = CheckoutGraph.route
        ) {
            composable(Screen.Checkout.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(CheckoutGraph.route) }
                val checkoutViewModel: CheckoutViewModel = hiltViewModel(parentEntry)
                CheckoutScreen(navController = navController, viewModel = checkoutViewModel)
            }
            composable(Screen.DeliveryDetails.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(CheckoutGraph.route) }
                val checkoutViewModel: CheckoutViewModel = hiltViewModel(parentEntry)
                DeliveryDetailsScreen(navController = navController, viewModel = checkoutViewModel)
            }
            composable(Screen.Payment.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(CheckoutGraph.route) }
                val checkoutViewModel: CheckoutViewModel = hiltViewModel(parentEntry)
                PaymentScreen(navController = navController, viewModel = checkoutViewModel)
            }
        }

        // --- Grafik Navigasi Admin ---
        navigation(
            startDestination = Screen.AdminDashboard.route,
            route = AdminGraph.route
        ) {
            composable(Screen.AdminDashboard.route) { AdminDashboardScreen(navController = navController)
            }
            composable(Screen.AdminManageOrders.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(AdminGraph.route) }
                val adminViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AdminManageOrdersScreen(navController = navController, viewModel = adminViewModel)
            }
            composable(Screen.AdminMenu.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(AdminGraph.route) }
                val adminViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AdminMenuScreen(navController, viewModel = adminViewModel)
            }
            composable(Screen.AdminCategories.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(AdminGraph.route) }
                val adminViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AdminCategoriesScreen(navController = navController, viewModel = adminViewModel)
            }
            composable(Screen.AdminPromoBanners.route) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(AdminGraph.route) }
                val adminViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AdminPromoBannersScreen(navController = navController, viewModel = adminViewModel)
            }
            composable(
                route = Screen.AddEditMenuItem.route,
                arguments = listOf(navArgument("menuItemId") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(AdminGraph.route) }
                val adminViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AddEditMenuItemScreen(navController = navController, viewModel = adminViewModel)
            }
            composable(
                route = Screen.AddEditPromoBanner.route,
                arguments = listOf(navArgument("bannerId") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val parentEntry = remember(backStackEntry) { navController.getBackStackEntry(AdminGraph.route) }
                val adminViewModel: AdminViewModel = hiltViewModel(parentEntry)
                AddEditPromoBannerScreen(navController = navController, viewModel = adminViewModel)
            }
        }

        // --- Rute lain-lain ---
        composable(Screen.AddAddressScreen.route) { AddAddressScreen(navController = navController) }
        composable(Screen.ManageAddresses.route) { ManageAddressesScreen(navController = navController) }
        composable(Screen.HelpCenter.route) { HelpCenterScreen(navController = navController) }
        composable(Screen.TermsAndConditions.route) { TermsAndConditionsScreen(navController = navController) }
        composable(Screen.PrivacyPolicy.route) { PrivacyPolicyScreen(navController = navController) }
    }
}