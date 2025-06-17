package com.mieso.app.ui.cart.state

import com.mieso.app.data.model.CartItem

data class CartUiState(
    val cartItems: List<CartItem> = emptyList(),
    val totalItemCount: Int = 0,
    val totalPrice: Long = 0L
)