package com.mieso.app.ui.checkout.viewmodel

import androidx.lifecycle.ViewModel
import com.mieso.app.ui.checkout.state.CheckoutUiState
import com.mieso.app.ui.checkout.state.ServiceType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState = _uiState.asStateFlow()

    fun onServiceTypeSelected(serviceType: ServiceType) {
        _uiState.update { it.copy(selectedServiceType = serviceType) }
        // In the next step, this function will also trigger navigation
        // to the appropriate details screen (e.g., address for delivery).
    }
}