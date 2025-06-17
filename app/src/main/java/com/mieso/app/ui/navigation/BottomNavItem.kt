package com.mieso.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

// List of items to be displayed in the bottom navigation bar
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Home",
        icon = Icons.Filled.Home,
        screen = Screen.Home
    ),
    BottomNavItem(
        label = "Search",
        icon = Icons.Filled.Search,
        screen = Screen.Search
    ),
    BottomNavItem(
        label = "Cart",
        icon = Icons.Outlined.ShoppingCart,
        screen = Screen.Cart
    ),
    BottomNavItem(
        label = "Promo",
        icon = Icons.Outlined.Discount,
        screen = Screen.Promo
    ),
    BottomNavItem(
        label = "Profile",
        icon = Icons.Filled.Person,
        screen = Screen.Profile
    )
)