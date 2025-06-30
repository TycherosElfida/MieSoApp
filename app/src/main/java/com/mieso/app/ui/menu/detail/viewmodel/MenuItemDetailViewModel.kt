package com.mieso.app.ui.menu.detail.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.repository.HomeRepository
import com.mieso.app.ui.menu.detail.state.MenuItemDetailUiState
import com.mieso.app.ui.navigation.NavArguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuItemDetailViewModel @Inject constructor(
    private val repository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuItemDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val menuItemId: String = savedStateHandle.get<String>(NavArguments.MENU_ITEM_ID) ?: ""

    init {
        if (menuItemId.isNotEmpty()) {
            loadItemDetails()
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Item not found.") }
        }
    }

    private fun loadItemDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val item = repository.getMenuItemById(menuItemId)
                _uiState.update {
                    it.copy(isLoading = false, menuItem = item)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Failed to load item details.") }
            }
        }
    }

    fun onQuantityChanged(newQuantity: Int) {
        if (newQuantity >= 1) { // Quantity must be at least 1
            _uiState.update { it.copy(quantity = newQuantity) }
        }
    }

}