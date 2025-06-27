package com.mieso.app.ui.orders.state

import com.mieso.app.data.model.Order

data class OrdersUiState(
    val isLoading: Boolean = true,
    val orders: List<Order> = emptyList(),
    val error: String? = null
)
