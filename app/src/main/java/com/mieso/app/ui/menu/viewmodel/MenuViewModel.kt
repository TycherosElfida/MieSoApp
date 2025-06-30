package com.mieso.app.ui.menu.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.repository.HomeRepository
import com.mieso.app.ui.menu.state.MenuUiState
import com.mieso.app.ui.navigation.NavArguments
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState = _uiState.asStateFlow()

    private val categoryId: String = savedStateHandle.get<String>(NavArguments.CATEGORY_ID) ?: ""
    private val categoryName: String = savedStateHandle.get<String>(NavArguments.CATEGORY_NAME) ?: "Menu"

    init {
        if (categoryId.isNotEmpty()) {
            _uiState.update { it.copy(categoryTitle = categoryName) }
            loadMenuItems(categoryId)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Category not found.") }
        }
    }

    private fun loadMenuItems(categoryId: String) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val items = homeRepository.getMenuItemsByCategory(categoryId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        menuItems = items,
                        error = null
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = "Failed to load menu.") }
            }
        }
    }
}