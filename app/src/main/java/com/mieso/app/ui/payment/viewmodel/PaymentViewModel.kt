package com.mieso.app.ui.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.auth.UserDataProvider
import com.mieso.app.data.model.Order
import com.mieso.app.data.repository.CartRepository
import com.mieso.app.data.repository.OrderRepository
import com.mieso.app.data.repository.UserRepository
import com.mieso.app.ui.payment.state.PaymentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val userDataProvider: UserDataProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                userDataProvider.user,
                cartRepository.getCartItems()
            ) { user, cartItems ->
                if (user == null) {
                    return@combine PaymentUiState()
                }

                val addresses = userRepository.getUserAddresses(user.id).first()
                val selectedAddress = addresses.firstOrNull { it.isPrimary } ?: addresses.firstOrNull()
                val subtotal = cartItems.sumOf { it.menuItem.price * it.quantity }
                val deliveryFee = if (selectedAddress != null) _uiState.value.deliveryFee else 0L
                val total = subtotal + deliveryFee

                PaymentUiState(
                    cartItems = cartItems,
                    selectedAddress = selectedAddress,
                    subtotal = subtotal,
                    deliveryFee = deliveryFee,
                    total = total
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun placeOrder() {
        viewModelScope.launch {
            val user = userDataProvider.user.value
            if (_uiState.value.isPlacingOrder || _uiState.value.cartItems.isEmpty() || user == null) {
                _uiState.update { it.copy(orderPlacementResult = Result.failure(Exception("User not logged in or cart is empty."))) }
                return@launch
            }

            _uiState.update { it.copy(isPlacingOrder = true) }

            val order = Order(
                userId = user.id,
                items = _uiState.value.cartItems,
                shippingAddress = _uiState.value.selectedAddress,
                subtotal = _uiState.value.subtotal,
                deliveryFee = _uiState.value.deliveryFee,
                total = _uiState.value.total
            )

            val result = orderRepository.placeOrder(order)
            if (result.isSuccess) {
                cartRepository.clearCart()
            }

            _uiState.update {
                it.copy(isPlacingOrder = false, orderPlacementResult = result)
            }
        }
    }

    fun consumeOrderPlacementResult() {
        _uiState.update { it.copy(orderPlacementResult = null) }
    }
}