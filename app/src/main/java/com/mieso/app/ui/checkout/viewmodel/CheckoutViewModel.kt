package com.mieso.app.ui.checkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.auth.UserDataProvider
import com.mieso.app.data.model.Order
import com.mieso.app.data.model.UserAddress
import com.mieso.app.data.repository.CartRepository
import com.mieso.app.data.repository.LocationRepository
import com.mieso.app.data.repository.OrderRepository
import com.mieso.app.data.repository.UserRepository
import com.mieso.app.ui.checkout.state.CheckoutUiState
import com.mieso.app.ui.checkout.state.ServiceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val locationRepository: LocationRepository,
    private val userDataProvider: UserDataProvider,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialAddresses()
        viewModelScope.launch {
            cartRepository.getCartItems().collect { cartItems ->
                _uiState.update { currentState ->
                    val subtotal = cartItems.sumOf { it.menuItem.price * it.quantity }
                    // Recalculate total whenever cart changes
                    val newTotal = subtotal + currentState.deliveryFee
                    currentState.copy(
                        cartItems = cartItems,
                        subtotal = subtotal,
                        total = newTotal
                    )
                }
            }
        }
    }

    private fun loadInitialAddresses() {
        viewModelScope.launch {
            val userId = userDataProvider.user.value?.id
            if (userId == null) {
                _uiState.update { it.copy(error = "User not logged in.") }
                return@launch
            }
            val addresses = userRepository.getUserAddresses(userId).first()
            _uiState.update {
                it.copy(
                    userAddresses = addresses,
                    selectedAddress = addresses.firstOrNull { addr -> addr.isPrimary }
                        ?: addresses.firstOrNull()
                )
            }
        }
    }

    // --- PERBAIKAN LOGIKA UTAMA ADA DI FUNGSI INI ---
    fun onServiceTypeSelected(serviceType: ServiceType) {
        _uiState.update { currentState ->
            // Tentukan ongkos kirim berdasarkan tipe layanan
            val deliveryFee = when (serviceType) {
                ServiceType.DELIVERY -> currentState.deliveryFee // Gunakan nilai default dari state
                else -> 0L // Gratis untuk Dine-In atau Take Away
            }
            // Hitung ulang total harga
            val total = currentState.subtotal + deliveryFee

            currentState.copy(
                selectedServiceType = serviceType,
                deliveryFee = deliveryFee,
                total = total
            )
        }
    }

    fun onAddressSelected(address: UserAddress) {
        _uiState.update { it.copy(selectedAddress = address) }
    }


    fun placeOrder() {
        viewModelScope.launch {
            val user = userDataProvider.user.value
            val currentState = _uiState.value
            if (currentState.isPlacingOrder || currentState.cartItems.isEmpty() || user == null) {
                _uiState.update { it.copy(orderPlacementResult = Result.failure(Exception("User not logged in or cart is empty."))) }
                return@launch
            }

            _uiState.update { it.copy(isPlacingOrder = true) }

            val order = Order(
                userId = user.id,
                items = currentState.cartItems,
                shippingAddress = if (currentState.selectedServiceType == ServiceType.DELIVERY) currentState.selectedAddress else null,
                subtotal = currentState.subtotal,
                deliveryFee = currentState.deliveryFee,
                total = currentState.total
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

    fun fetchCurrentLocationAsAddress() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            locationRepository.getCurrentLocation().firstOrNull()?.let { location ->
                val address = locationRepository.reverseGeocode(location)
                if (address != null) {
                    _uiState.update { currentState ->
                        val updatedAddresses = currentState.userAddresses.toMutableList()
                        updatedAddresses.removeAll { it.label == "Lokasi Saat Ini" }
                        updatedAddresses.add(0, address)
                        currentState.copy(
                            isLoading = false,
                            userAddresses = updatedAddresses,
                            selectedAddress = address
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Gagal mendapatkan detail alamat."
                        )
                    }
                }
            } ?: _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Gagal mendapatkan lokasi Anda."
                )
            }
        }
    }

    fun saveNewAddress(
        label: String,
        addressLine1: String,
        city: String,
        postalCode: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = userDataProvider.user.value?.id
            if (userId == null) {
                _uiState.update { it.copy(isLoading = false, error = "User not found.") }
                return@launch
            }
            val newAddress = UserAddress(
                label = label,
                addressLine1 = addressLine1,
                city = city,
                postalCode = postalCode,
                isPrimary = _uiState.value.userAddresses.isEmpty()
            )
            userRepository.addAddress(userId, newAddress)
            loadInitialAddresses()
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun consumeOrderPlacementResult() {
        _uiState.update { it.copy(orderPlacementResult = null) }
    }
}