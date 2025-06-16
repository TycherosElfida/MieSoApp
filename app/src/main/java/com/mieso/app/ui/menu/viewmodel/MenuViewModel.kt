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
    savedStateHandle: SavedStateHandle // Injected by Hilt to access navigation arguments.
) : ViewModel() {

    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState = _uiState.asStateFlow()

    private val categoryId: String = savedStateHandle.get<String>(NavArguments.CATEGORY_ID) ?: ""

    init {
        if (categoryId.isNotEmpty()) {
            loadMenuData(categoryId)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Category not found.") }
        }
    }

    private fun loadMenuData(categoryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val items = homeRepository.getMenuItemsByCategory(categoryId)
                // Set the category title from the first item found.
                // A more robust solution might fetch the category name separately.
                val title = items.firstOrNull()?.category ?: "Menu"

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        menuItems = items,
                        categoryTitle = title
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = "Failed to load menu.") }
            }
        }
    }
}