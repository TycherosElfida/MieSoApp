package com.mieso.app.ui.admin.viewmodel

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner
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

data class AddEditBannerUiState(
    val id: String = "",
    val order: String = "",
    val targetScreen: String = "",
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
    private val bannerId: String? = savedStateHandle["bannerId"]

    // --- States ---
    private val _addEditScreenUiState = MutableStateFlow(AddEditScreenUiState())
    val addEditScreenUiState = _addEditScreenUiState.asStateFlow()

    private val _addEditBannerUiState = MutableStateFlow(AddEditBannerUiState())
    val addEditBannerUiState = _addEditBannerUiState.asStateFlow()

    private val _categories = MutableStateFlow<List<FoodCategory>>(emptyList())
    val categories: StateFlow<List<FoodCategory>> = _categories.asStateFlow()

    val menuItems: StateFlow<List<MenuItem>> =
        homeRepository.getAllMenuItemsStream()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val promoBanners: StateFlow<List<PromoBanner>> =
        homeRepository.getPromoBannersStream()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            _categories.value = homeRepository.getCategories()
        }
        if (menuItemId != null) {
            loadMenuItem(menuItemId)
        }
        if (bannerId != null) {
            loadPromoBanner(bannerId)
        }
    }

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

    fun saveCategory(categoryToEdit: FoodCategory?, name: String, order: Int) {
        viewModelScope.launch {
            if (categoryToEdit != null) {
                val updatedCategory = categoryToEdit.copy(name = name, order = order)
                homeRepository.updateCategory(updatedCategory)
            } else {
                val newCategory = FoodCategory(name = name, order = order)
                homeRepository.addCategory(newCategory)
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            homeRepository.deleteCategory(categoryId)
        }
    }

    private fun loadPromoBanner(id: String) {
        // You'll need a way to get a single banner by ID.
        // For simplicity, we'll filter the flow here.
        viewModelScope.launch {
            val banner = homeRepository.getPromoBannersStream().first()
                .find { it.id == id }

            if (banner != null) {
                _addEditBannerUiState.value = AddEditBannerUiState(
                    id = banner.id,
                    order = banner.order.toString(),
                    targetScreen = banner.targetScreen,
                    existingImageUrl = banner.imageUrl,
                    isEditing = true
                )
            }
        }
    }


    fun onBannerOrderChanged(order: String) {
        _addEditBannerUiState.update { it.copy(order = order) }
    }

    fun onBannerTargetChanged(target: String) {
        _addEditBannerUiState.update { it.copy(targetScreen = target) }
    }

    fun onBannerImageSelected(uri: Uri) {
        _addEditBannerUiState.update { it.copy(selectedImageUri = uri) }
    }

    fun savePromoBanner() {
        viewModelScope.launch {
            _addEditBannerUiState.update { it.copy(isSaving = true) }
            val state = _addEditBannerUiState.value

            val imageUrl = if (state.selectedImageUri != null) {
                storageRepository.uploadMenuImage(state.selectedImageUri) // Re-using the same upload logic
            } else {
                state.existingImageUrl
            }

            if (imageUrl != null) {
                val banner = PromoBanner(
                    id = if (state.isEditing) state.id else "",
                    order = state.order.toIntOrNull() ?: 0,
                    targetScreen = state.targetScreen,
                    imageUrl = imageUrl
                )

                if (state.isEditing) {
                    homeRepository.updatePromoBanner(banner)
                } else {
                    homeRepository.addPromoBanner(banner)
                }
            }
            _addEditBannerUiState.update { it.copy(isSaving = false) }
        }
    }

    fun deletePromoBanner(bannerId: String) {
        viewModelScope.launch {
            homeRepository.deletePromoBanner(bannerId)
        }
    }
}