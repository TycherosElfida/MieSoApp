package com.mieso.app.data.repository

import com.mieso.app.data.model.CartItem
import com.mieso.app.data.model.MenuItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addToCart(item: MenuItem, quantity: Int)
    suspend fun updateQuantity(itemId: String, newQuantity: Int)
    suspend fun removeFromCart(itemId: String)
    suspend fun clearCart()
}