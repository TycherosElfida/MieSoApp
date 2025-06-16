package com.mieso.app.ui.menu.detail.state

import com.mieso.app.data.model.MenuItem

data class MenuItemDetailUiState(
    val isLoading: Boolean = true,
    val menuItem: MenuItem? = null,
    val quantity: Int = 1,
    val error: String? = null
) {
    // A helper computed property to calculate the total price
    val totalPrice: Long
        get() = (menuItem?.price ?: 0) * quantity
}