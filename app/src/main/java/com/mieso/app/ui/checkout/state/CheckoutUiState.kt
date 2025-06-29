package com.mieso.app.ui.checkout.state

import com.mieso.app.data.model.CartItem // Tambahkan import
import com.mieso.app.data.model.UserAddress

enum class ServiceType { DELIVERY, DINE_IN, TAKE_AWAY, NONE }

data class CheckoutUiState(
    // Properti yang sudah ada
    val selectedServiceType: ServiceType = ServiceType.NONE,
    val userAddresses: List<UserAddress> = emptyList(),
    val selectedAddress: UserAddress? = null,
    val isLoading: Boolean = false,
    val error: String? = null,

    // Properti baru dari PaymentUiState
    val cartItems: List<CartItem> = emptyList(),
    val subtotal: Long = 0,
    val deliveryFee: Long = 8000, // Anda bisa sesuaikan nilai default
    val total: Long = 0,
    val isPlacingOrder: Boolean = false,
    val orderPlacementResult: Result<Unit>? = null
)