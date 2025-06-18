package com.mieso.app.ui.payment.state

import com.mieso.app.data.model.CartItem
import com.mieso.app.data.model.UserAddress

/**
 * Represents the UI state for the PaymentScreen.
 *
 * @param cartItems The list of items to be purchased.
 * @param selectedAddress The delivery address for the order.
 * @param subtotal The total price of all items before fees.
 * @param deliveryFee The calculated fee for delivery.
 * @param total The final price including all fees.
 * @param isPlacingOrder True if the order placement is in progress, false otherwise.
 * @param orderPlacementResult Holds the result of the order placement attempt. Null if not yet attempted.
 */
data class PaymentUiState(
    val cartItems: List<CartItem> = emptyList(),
    val selectedAddress: UserAddress? = null,
    val subtotal: Long = 0,
    val deliveryFee: Long = 8000, // Default/example delivery fee
    val total: Long = 0,
    val isPlacingOrder: Boolean = false,
    val orderPlacementResult: Result<Unit>? = null
)