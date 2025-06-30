// File: app/src/main/java/com/mieso/app/ui/admin/viewmodel/AdminViewModel.kt

package com.mieso.app.ui.admin.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.Order
import com.mieso.app.data.model.PromoBanner
import com.mieso.app.data.repository.HomeRepository
import com.mieso.app.data.repository.OrderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ... data class tidak berubah ...
data class AddEditScreenUiState(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val categoryId: String = "",
    val categoryName: String = "",
    val isRecommended: Boolean = false,
    val imageUrl: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)

data class AddEditBannerUiState(
    val id: String = "",
    val order: String = "",
    val targetScreen: String = "",
    val imageUrl: String = "",
    val isEditing: Boolean = false,
    val isSaving: Boolean = false
)


@HiltViewModel
class AdminViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val orderRepository: OrderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // ... StateFlows dan init block yang sudah ada tidak berubah ...
    private val menuItemId: String? = savedStateHandle["menuItemId"]
    private val bannerId: String? = savedStateHandle["bannerId"]

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

    val allOrders: StateFlow<List<Order>> =
        orderRepository.getAllOrders()
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


    // --- TAMBAHKAN FUNGSI BARU DI BAWAH INI ---
    /**
     * Triggers an update for an order's status.
     * This function will be called from the UI when an admin confirms a status change.
     * @param orderId The ID of the order to be updated.
     * @param newStatus The new status to be set for the order.
     */
    fun updateOrderStatus(orderId: String, newStatus: String) {
        // Memastikan orderId tidak kosong untuk mencegah error.
        if (orderId.isBlank()) return

        viewModelScope.launch {
            // Memanggil fungsi dari repository.
            // Kita tidak perlu menangani hasilnya di sini karena UI akan otomatis
            // diperbarui oleh StateFlow `allOrders` saat data di Firestore berubah.
            orderRepository.updateOrderStatus(orderId, newStatus)
        }
    }


    // --- Sisa fungsi-fungsi ViewModel tidak berubah ---
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
                    imageUrl = item.imageUrl,
                    isEditing = true
                )
            }
        }
    }
    fun onNameChanged(name: String) = _addEditScreenUiState.update { it.copy(name = name) }
    fun onDescriptionChanged(description: String) = _addEditScreenUiState.update { it.copy(description = description) }
    fun onPriceChanged(price: String) = _addEditScreenUiState.update { it.copy(price = price) }
    fun onCategoryChanged(categoryId: String, categoryName: String) = _addEditScreenUiState.update { it.copy(categoryId = categoryId, categoryName = categoryName) }
    fun onIsRecommendedChanged(isRecommended: Boolean) = _addEditScreenUiState.update { it.copy(isRecommended = isRecommended) }
    fun onImageUrlChanged(url: String) = _addEditScreenUiState.update { it.copy(imageUrl = url) }

    fun saveMenuItem() {
        viewModelScope.launch {
            _addEditScreenUiState.update { it.copy(isSaving = true) }
            val state = _addEditScreenUiState.value
            val menuItem = MenuItem(
                id = state.id,
                name = state.name,
                description = state.description,
                price = state.price.toLongOrNull() ?: 0L,
                categoryId = state.categoryId,
                categoryName = state.categoryName,
                isRecommended = state.isRecommended,
                imageUrl = state.imageUrl
            )
            if (state.isEditing) {
                homeRepository.updateMenuItem(menuItem)
            } else {
                homeRepository.addMenuItem(menuItem)
            }
            _addEditScreenUiState.update { it.copy(isSaving = false) }
        }
    }

    fun deleteMenuItem(itemId: String) {
        viewModelScope.launch { homeRepository.deleteMenuItem(itemId) }
    }

    fun saveCategory(categoryToEdit: FoodCategory?, name: String, order: Int) {
        viewModelScope.launch {
            val category = categoryToEdit?.copy(name = name, order = order) ?: FoodCategory(name = name, order = order)
            if (categoryToEdit != null) {
                homeRepository.updateCategory(category)
            } else {
                homeRepository.addCategory(category)
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch { homeRepository.deleteCategory(categoryId) }
    }

    private fun loadPromoBanner(id: String) {
        viewModelScope.launch {
            val banner = homeRepository.getPromoBannerById(id)
            if (banner != null) {
                _addEditBannerUiState.value = AddEditBannerUiState(
                    id = banner.id,
                    order = banner.order.toString(),
                    targetScreen = banner.targetScreen,
                    imageUrl = banner.imageUrl,
                    isEditing = true
                )
            }
        }
    }
    fun onBannerOrderChanged(order: String) = _addEditBannerUiState.update { it.copy(order = order) }
    fun onBannerTargetChanged(target: String) = _addEditBannerUiState.update { it.copy(targetScreen = target) }
    fun onBannerImageUrlChanged(url: String) = _addEditBannerUiState.update { it.copy(imageUrl = url) }

    fun savePromoBanner() {
        viewModelScope.launch {
            _addEditBannerUiState.update { it.copy(isSaving = true) }
            val state = _addEditBannerUiState.value
            val banner = PromoBanner(
                id = state.id,
                order = state.order.toIntOrNull() ?: 0,
                targetScreen = state.targetScreen,
                imageUrl = state.imageUrl
            )
            if (state.isEditing) {
                homeRepository.updatePromoBanner(banner)
            } else {
                homeRepository.addPromoBanner(banner)
            }
            _addEditBannerUiState.update { it.copy(isSaving = false) }
        }
    }

    fun deletePromoBanner(bannerId: String) {
        viewModelScope.launch { homeRepository.deletePromoBanner(bannerId) }
    }
}
