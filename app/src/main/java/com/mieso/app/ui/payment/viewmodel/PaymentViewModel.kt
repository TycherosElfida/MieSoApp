package com.mieso.app.ui.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.model.Order
import com.mieso.app.data.repository.CartRepository
import com.mieso.app.data.repository.OrderRepository
import com.mieso.app.data.repository.UserRepository
import com.mieso.app.ui.payment.state.PaymentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()

    // A hardcoded user ID for the prototype. In a real app, this would come from an auth service.
    private val userId = "Eric_lil_KT"

    init {
        // This `combine` block reactively builds the UI state whenever the cart or user addresses change.
        viewModelScope.launch {
            combine(
                cartRepository.getCartItems(),
                userRepository.getUserAddresses(userId)
            ) { cartItems, addresses ->
                // Determine the selected address (primary or the first one available).
                val selectedAddress = addresses.firstOrNull { it.isPrimary } ?: addresses.firstOrNull()
                val subtotal = cartItems.sumOf { it.menuItem.price * it.quantity }
                // Delivery fee is only applied if there is a shipping address.
                val deliveryFee = if (selectedAddress != null) _uiState.value.deliveryFee else 0L
                val total = subtotal + deliveryFee

                // Return a fully constructed state object.
                PaymentUiState(
                    cartItems = cartItems,
                    selectedAddress = selectedAddress,
                    subtotal = subtotal,
                    deliveryFee = deliveryFee,
                    total = total
                )
            }.collect { newState ->
                // Update the state flow with the latest combined data.
                _uiState.value = newState
            }
        }
    }

    /**
     * Constructs an Order object and requests the repository to save it.
     */
    fun placeOrder() {
        viewModelScope.launch {
            // Guard clause: Do not proceed if the order is already being placed or the cart is empty.
            if (_uiState.value.isPlacingOrder || _uiState.value.cartItems.isEmpty()) return@launch

            // Set loading state
            _uiState.update { it.copy(isPlacingOrder = true) }

            // Construct the Order object from the current state.
            val order = Order(
                userId = userId,
                items = _uiState.value.cartItems,
                shippingAddress = _uiState.value.selectedAddress,
                subtotal = _uiState.value.subtotal,
                deliveryFee = _uiState.value.deliveryFee,
                total = _uiState.value.total,
                paymentMethod = "Cash on Delivery", // Hardcoded for now
                status = "Pending"
            )

            // Place the order and get the result.
            val result = orderRepository.placeOrder(order)

            // If successful, clear the local cart.
            if (result.isSuccess) {
                cartRepository.clearCart()
            }

            // Update the UI state with the final result and turn off the loading indicator.
            _uiState.update {
                it.copy(
                    isPlacingOrder = false,
                    orderPlacementResult = result
                )
            }
        }
    }

    /**
     * Resets the order placement result, allowing the user to retry or dismiss a message.
     */
    fun consumeOrderPlacementResult() {
        _uiState.update { it.copy(orderPlacementResult = null) }
    }
}