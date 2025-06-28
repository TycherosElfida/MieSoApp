package com.mieso.app.ui.checkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.auth.UserDataProvider
import com.mieso.app.data.model.UserAddress
import com.mieso.app.data.repository.LocationRepository
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
    private val userDataProvider: UserDataProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userDataProvider.user.collectLatest { user ->
                if (user != null) {
                    userRepository.getUserAddresses(user.id).collect { addresses ->
                        _uiState.update {
                            it.copy(
                                userAddresses = addresses,
                                selectedAddress = it.selectedAddress ?: addresses.firstOrNull { addr -> addr.isPrimary } ?: addresses.firstOrNull()
                            )
                        }
                    }
                }
            }
        }
    }

    fun onServiceTypeSelected(serviceType: ServiceType) {
        _uiState.update { it.copy(selectedServiceType = serviceType) }
    }

    fun onAddressSelected(address: UserAddress) {
        _uiState.update { it.copy(selectedAddress = address) }
    }

    fun saveNewAddress(
        label: String,
        addressLine1: String,
        city: String,
        postalCode: String
    ) {
        viewModelScope.launch {
            val userId = userDataProvider.user.value?.id ?: return@launch
            val newAddress = UserAddress(
                label = label,
                addressLine1 = addressLine1,
                city = city,
                postalCode = postalCode,
                isPrimary = _uiState.value.userAddresses.isEmpty()
            )
            userRepository.addAddress(userId, newAddress)
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
                    _uiState.update { it.copy(isLoading = false, error = "Gagal mendapatkan detail alamat.") }
                }
            } ?: _uiState.update { it.copy(isLoading = false, error = "Gagal mendapatkan lokasi Anda.") }
        }
    }
}