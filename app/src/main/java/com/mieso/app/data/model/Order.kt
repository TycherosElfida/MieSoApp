package com.mieso.app.data.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Order(
    val id: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val shippingAddress: UserAddress? = null,
    val subtotal: Long = 0,
    val deliveryFee: Long = 0,
    val total: Long = 0,
    val paymentMethod: String = "COD", // Default or selected method
    val status: String = "Pending", // e.g., Pending, Confirmed, Delivered
    @ServerTimestamp val createdAt: Date? = null
)