package com.mieso.app.ui.checkout.state

import com.mieso.app.data.model.UserAddress

enum class ServiceType { DELIVERY, DINE_IN, TAKE_AWAY, NONE }

data class CheckoutUiState(
    val selectedServiceType: ServiceType = ServiceType.NONE,
    val userAddresses: List<UserAddress> = emptyList(),
    val selectedAddress: UserAddress? = null
)