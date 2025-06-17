package com.mieso.app.data.repository

import com.mieso.app.data.model.CartItem
import com.mieso.app.data.model.MenuItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor() : CartRepository {

    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())

    override fun getCartItems(): Flow<List<CartItem>> {
        // We expose the cart items as a Flow of a List, sorted alphabetically for a consistent display.
        return _cartItems.map { it.values.toList().sortedBy { item -> item.menuItem.name } }
    }

    override suspend fun addToCart(item: MenuItem, quantity: Int) {
        val currentItems = _cartItems.value.toMutableMap()
        val existingItem = currentItems[item.id]

        if (existingItem != null) {
            // If the item already exists in the cart, we just increase its quantity.
            currentItems[item.id] = existingItem.copy(quantity = existingItem.quantity + quantity)
        } else {
            // Otherwise, we add the new item to the cart.
            currentItems[item.id] = CartItem(menuItem = item, quantity = quantity)
        }
        _cartItems.value = currentItems
    }

    override suspend fun updateQuantity(itemId: String, newQuantity: Int) {
        val currentItems = _cartItems.value.toMutableMap()
        val item = currentItems[itemId]

        if (item != null) {
            if (newQuantity > 0) {
                currentItems[itemId] = item.copy(quantity = newQuantity)
            } else {
                // If the new quantity is 0 or less, we remove the item from the cart.
                currentItems.remove(itemId)
            }
            _cartItems.value = currentItems
        }
    }

    override suspend fun removeFromCart(itemId: String) {
        val currentItems = _cartItems.value.toMutableMap()
        currentItems.remove(itemId)
        _cartItems.value = currentItems
    }

    override suspend fun clearCart() {
        _cartItems.value = emptyMap()
    }
}