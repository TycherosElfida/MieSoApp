package com.mieso.app.ui.checkout.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.model.UserAddress
import com.mieso.app.data.repository.UserRepository
import com.mieso.app.ui.checkout.state.CheckoutUiState
import com.mieso.app.ui.checkout.state.ServiceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    // For this prototype, we'll use a hardcoded user ID.
    private val userId = "Eric_lil_KT"

    init {
        // Start observing user addresses as soon as the ViewModel is created.
        userRepository.getUserAddresses(userId)
            .onEach { addresses ->
                _uiState.update {
                    it.copy(
                        userAddresses = addresses,
                        // Automatically select the primary address if one exists and none is selected yet
                        selectedAddress = it.selectedAddress ?: addresses.firstOrNull { addr -> addr.isPrimary }
                    )
                }
            }
            .launchIn(viewModelScope)
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
            val newAddress = UserAddress(
                label = label,
                addressLine1 = addressLine1,
                city = city,
                postalCode = postalCode,
                // If this is the first address, make it the primary one.
                isPrimary = _uiState.value.userAddresses.isEmpty()
            )
            userRepository.addAddress(userId, newAddress)
            // After saving, the Firestore listener in our init block will
            // automatically update the userAddresses list in the UI state.
        }
    }
}