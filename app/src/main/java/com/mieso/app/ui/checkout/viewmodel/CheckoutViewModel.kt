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
import kotlinx.coroutines.flow.*
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
        // Langkah 1: Ambil daftar alamat yang tersimpan satu kali saja.
        loadInitialAddresses()

        // Langkah 2: Pantau perubahan pada keranjang untuk memperbarui harga.
        // Flow ini tidak akan lagi mengganggu daftar alamat.
        viewModelScope.launch {
            cartRepository.getCartItems().collect { cartItems ->
                _uiState.update { currentState ->
                    val subtotal = cartItems.sumOf { it.menuItem.price * it.quantity }
                    val deliveryFee = if (currentState.selectedAddress != null) currentState.deliveryFee else 0L
                    val total = subtotal + deliveryFee
                    currentState.copy(
                        cartItems = cartItems,
                        subtotal = subtotal,
                        deliveryFee = deliveryFee,
                        total = total
                    )
                }
            }
        }
    }

    private fun loadInitialAddresses() {
        viewModelScope.launch {
            val userId = userDataProvider.user.value?.id
            if (userId == null) {
                // Handle jika user tidak ditemukan
                _uiState.update { it.copy(error = "User not logged in.") }
                return@launch
            }
            // Ambil alamat dari database dan set sebagai state awal
            val addresses = userRepository.getUserAddresses(userId).first()
            _uiState.update {
                it.copy(
                    userAddresses = addresses,
                    selectedAddress = addresses.firstOrNull { addr -> addr.isPrimary } ?: addresses.firstOrNull()
                )
            }
        }
    }

    // Fungsi ini tidak berubah dari perbaikan sebelumnya, sudah benar.
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
                    _uiState.update { it.copy(isLoading = false, error = "Gagal mendapatkan detail alamat.") }
                }
            } ?: _uiState.update { it.copy(isLoading = false, error = "Gagal mendapatkan lokasi Anda.") }
        }
    }

    // Fungsi onAddressSelected sekarang juga perlu memperbarui harga
    fun onAddressSelected(address: UserAddress) {
        _uiState.update { currentState ->
            val deliveryFee = if (address.label == "Lokasi Saat Ini" || address.id.isNotBlank()) currentState.deliveryFee else 0L
            val total = currentState.subtotal + deliveryFee
            currentState.copy(
                selectedAddress = address,
                deliveryFee = deliveryFee,
                total = total
            )
        }
    }

    // --- Sisa fungsi lainnya tidak perlu diubah ---

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
                shippingAddress = currentState.selectedAddress,
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

    fun consumeOrderPlacementResult() {
        _uiState.update { it.copy(orderPlacementResult = null) }
    }

    fun onServiceTypeSelected(serviceType: ServiceType) {
        _uiState.update { it.copy(selectedServiceType = serviceType) }
    }

    fun saveNewAddress(
        label: String,
        addressLine1: String,
        city: String,
        postalCode: String
    ) {
        viewModelScope.launch {
            // Tampilkan indikator loading agar pengguna tahu ada proses berjalan
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
                // Jadikan alamat utama jika ini adalah alamat pertama
                isPrimary = _uiState.value.userAddresses.isEmpty()
            )

            // Simpan ke database
            userRepository.addAddress(userId, newAddress)

            // ==========================================================
            // ===             INILAH BAGIAN PERBAIKANNYA             ===
            // ==========================================================
            // Setelah berhasil menyimpan, panggil lagi fungsi untuk memuat
            // daftar alamat agar UI diperbarui dengan data terbaru.
            loadInitialAddresses()

            // Sembunyikan kembali indikator loading
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}