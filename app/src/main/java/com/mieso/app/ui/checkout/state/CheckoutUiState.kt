package com.mieso.app.ui.checkout.state

enum class ServiceType {
    DELIVERY, DINE_IN, TAKE_AWAY, NONE
}

data class CheckoutUiState(
    val selectedServiceType: ServiceType = ServiceType.NONE
)