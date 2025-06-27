package com.mieso.app.data.model

data class CartItem(
    val menuItem: MenuItem = MenuItem(),
    val quantity: Int = 0
)
