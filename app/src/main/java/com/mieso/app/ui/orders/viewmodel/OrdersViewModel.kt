package com.mieso.app.ui.orders.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.repository.AuthRepository
import com.mieso.app.data.repository.OrderRepository
import com.mieso.app.ui.orders.state.OrdersUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Listen to authentication state changes.
            authRepository.getAuthState().flatMapLatest { user ->
                if (user != null) {
                    // If a user is logged in, fetch their orders.
                    orderRepository.getUserOrders(user.uid)
                } else {
                    // If no user, return a flow with an empty list.
                    flowOf(emptyList())
                }
            }.catch { e ->
                // Handle any errors from the upstream flows.
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }.collect { orders ->
                // Update the state with the new list of orders.
                _uiState.update { it.copy(isLoading = false, orders = orders) }
            }
        }
    }
}
