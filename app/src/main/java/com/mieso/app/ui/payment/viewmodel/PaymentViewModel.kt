package com.mieso.app.ui.payment.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.model.Order
import com.mieso.app.data.repository.AuthRepository
import com.mieso.app.data.repository.CartRepository
import com.mieso.app.data.repository.OrderRepository
import com.mieso.app.data.repository.UserRepository
import com.mieso.app.ui.payment.state.PaymentUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getAuthState().flatMapLatest { user ->
                if (user == null) {
                    flowOf(PaymentUiState())
                } else {
                    combine(
                        userRepository.getUserAddresses(user.uid),
                        cartRepository.getCartItems()
                    ) { addresses, cartItems ->
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
                    }
                }
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    /**
     * Constructs an Order object and requests the repository to save it.
     */
    fun placeOrder() {
        viewModelScope.launch {
            if (_uiState.value.isPlacingOrder || _uiState.value.cartItems.isEmpty()) return@launch

            val userId = authRepository.getAuthState().first()?.uid ?: run {
                _uiState.update { it.copy(orderPlacementResult = Result.failure(Exception("User not logged in."))) }
                return@launch
            }

            _uiState.update { it.copy(isPlacingOrder = true) }

            val order = Order(
                userId = userId,
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

    /**
     * Resets the order placement result, allowing the user to retry or dismiss a message.
     */
    fun consumeOrderPlacementResult() {
        _uiState.update { it.copy(orderPlacementResult = null) }
    }
}