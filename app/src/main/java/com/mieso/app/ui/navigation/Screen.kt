package com.mieso.app.ui.navigation

object NavArguments {
    const val CATEGORY_ID = "categoryId"
    const val MENU_ITEM_ID = "menuItemId"
}

sealed class Screen(val route: String) {

    object Login : Screen("login_screen")

    object Main : Screen("main_screen")

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

    object AddAddressScreen : Screen("add_address_screen")

    object DeliveryDetails : Screen("delivery_details_screen")

    object Payment : Screen("payment_screen")

    object ManageAddresses : Screen("manage_addresses_screen")

    object HelpCenter : Screen("help_center_screen")

    object TermsAndConditions : Screen("terms_and_conditions_screen")

    object PrivacyPolicy : Screen("privacy_policy_screen")

    object AdminMenu : Screen("admin_menu_screen")

    object AddEditMenuItem : Screen("add_edit_menu_item_screen?menuItemId={menuItemId}") {
        fun createRoute(menuItemId: String? = null): String {
            return if (menuItemId != null) {
                "add_edit_menu_item_screen?menuItemId=$menuItemId"
            } else {
                "add_edit_menu_item_screen"
            }
        }
    }
    object AdminCategories : Screen("admin_categories_screen")

    object AdminPromoBanners : Screen("admin_promo_banners_screen")

    object AddEditPromoBanner : Screen("add_edit_promo_banner_screen?bannerId={bannerId}") {
        fun createRoute(bannerId: String? = null): String {
            return if (bannerId != null) {
                "add_edit_promo_banner_screen?bannerId=$bannerId"
            } else {
                "add_edit_promo_banner_screen"
            }
        }
    }
}