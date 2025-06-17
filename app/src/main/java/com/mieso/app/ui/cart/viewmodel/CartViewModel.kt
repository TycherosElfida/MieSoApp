package com.mieso.app.ui.cart.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.repository.CartRepository
import com.mieso.app.ui.cart.state.CartUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Observe the cart items from the repository and update the UI state accordingly.
        cartRepository.getCartItems()
            .onEach { items ->
                _uiState.update {
                    it.copy(
                        cartItems = items,
                        totalItemCount = items.sumOf { item -> item.quantity },
                        totalPrice = items.sumOf { item -> item.menuItem.price * item.quantity }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun addToCart(menuItem: MenuItem, quantity: Int) {
        viewModelScope.launch {
            cartRepository.addToCart(menuItem, quantity)
        }
    }

    fun updateQuantity(itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(itemId, newQuantity)
        }
    }

    fun removeFromCart(itemId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(itemId)
        }
    }
}