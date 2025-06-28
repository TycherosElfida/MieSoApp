package com.mieso.app.ui.admin.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.repository.HomeRepository
import com.mieso.app.data.repository.StorageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditScreenUiState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val isRecommended: Boolean = false,
    val selectedImageUri: Uri? = null,
    val existingImageUrl: String? = null,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val storageRepository: StorageRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val menuItemId: String? = savedStateHandle["menuItemId"]

    private val _addEditScreenUiState = MutableStateFlow(AddEditScreenUiState())
    val addEditScreenUiState = _addEditScreenUiState.asStateFlow()

    private val _categories = MutableStateFlow<List<FoodCategory>>(emptyList())
    val categories: StateFlow<List<FoodCategory>> = _categories.asStateFlow()

    init {
        viewModelScope.launch {
            _categories.value = homeRepository.getCategories()
        }

        if (menuItemId != null) {
            loadMenuItem(menuItemId)
        }
    }

    val menuItems: StateFlow<List<MenuItem>> =
        homeRepository.getAllMenuItemsStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch {
            homeRepository.deleteMenuItem(itemId)
        }
    }

    private fun loadMenuItem(id: String) {
        viewModelScope.launch {
            val item = homeRepository.getMenuItemById(id)
            if (item != null) {
                _addEditScreenUiState.value = AddEditScreenUiState(
                    id = item.id,
                    name = item.name,
                    description = item.description,
                    price = item.price.toString(),
                    categoryId = item.categoryId,
                    categoryName = item.categoryName,
                    isRecommended = item.isRecommended,
                    existingImageUrl = item.imageUrl,
                    isEditing = true
                )
            }
        }
    }

    fun onNameChanged(name: String) {
        _addEditScreenUiState.update { it.copy(name = name) }
    }

    fun onDescriptionChanged(description: String) {
        _addEditScreenUiState.update { it.copy(description = description) }
    }

    fun onPriceChanged(price: String) {
        _addEditScreenUiState.update { it.copy(price = price) }
    }

    fun onCategoryChanged(categoryId: String, categoryName: String) {
        _addEditScreenUiState.update { it.copy(categoryId = categoryId, categoryName = categoryName) }
    }

    fun onIsRecommendedChanged(isRecommended: Boolean) {
        _addEditScreenUiState.update { it.copy(isRecommended = isRecommended) }
    }

    fun onImageSelected(uri: Uri) {
        _addEditScreenUiState.update { it.copy(selectedImageUri = uri) }
    }

    fun saveMenuItem() {
        viewModelScope.launch {
            _addEditScreenUiState.update { it.copy(isSaving = true) }
            val state = _addEditScreenUiState.value

            val imageUrl = if (state.selectedImageUri != null) {
                storageRepository.uploadMenuImage(state.selectedImageUri)
            } else {
                state.existingImageUrl
            }

            if (imageUrl != null) {
                val menuItem = MenuItem(
                    id = if (state.isEditing) state.id else "", // Keep ID for updates
                    name = state.name,
                    description = state.description,
                    price = state.price.toLongOrNull() ?: 0L,
                    categoryId = state.categoryId,
                    categoryName = state.categoryName,
                    isRecommended = state.isRecommended,
                    imageUrl = imageUrl
                )

                if (state.isEditing) {
                    homeRepository.updateMenuItem(menuItem)
                } else {
                    homeRepository.addMenuItem(menuItem)
                }
            }
            _addEditScreenUiState.update { it.copy(isSaving = false) }
        }
    }
}