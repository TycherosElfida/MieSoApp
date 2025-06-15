package com.mieso.app.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.ui.home.state.FoodCategory
import com.mieso.app.ui.home.state.HomeUiState
import com.mieso.app.ui.home.state.MenuItem
import com.mieso.app.ui.home.state.PromoBanner
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    // _uiState is the private, mutable state that only the ViewModel can change.
    private val _uiState = MutableStateFlow(HomeUiState())
    // uiState is the public, read-only state that the UI will observe.
    val uiState = _uiState.asStateFlow()

    init {
        // When the ViewModel is created, we launch a coroutine to load our data.
        loadHomeScreenData()
    }

    private fun loadHomeScreenData() {
        viewModelScope.launch {
            // Simulate a network delay for a realistic loading experience.
            delay(1500)

            // Hardcoded data for UI development. We'll replace this with real
            // data from Firebase later.
            val promos = listOf(
                PromoBanner(1, "https://placehold.co/600x300/E65100/FFFFFF?text=Promo+1"),
                PromoBanner(2, "https://placehold.co/600x300/FFCA28/000000?text=Promo+2"),
                PromoBanner(3, "https://placehold.co/600x300/4CAF50/FFFFFF?text=Promo+3")
            )
            val categories = listOf(
                FoodCategory(1, "Mie Ayam"),
                FoodCategory(2, "Bakso"),
                FoodCategory(3, "Minuman"),
                FoodCategory(4, "Cemilan"),
                FoodCategory(5, "Paket Hemat")
            )
            val menuItems = listOf(
                MenuItem(1, "Mie Ayam Spesial", "Rp 25.000", "https://placehold.co/400x400/E65100/FFFFFF?text=Mie+Ayam"),
                MenuItem(2, "Bakso Urat Jumbo", "Rp 28.000", "https://placehold.co/400x400/BF360C/FFFFFF?text=Bakso"),
                MenuItem(3, "Es Teh Manis", "Rp 8.000", "https://placehold.co/400x400/FFCA28/000000?text=Es+Teh"),
                MenuItem(4, "Mie Ayam Original", "Rp 20.000", "https://placehold.co/400x400/E65100/FFFFFF?text=Mie+Ayam"),
                MenuItem(5, "Bakso Halus", "Rp 22.000", "https://placehold.co/400x400/BF360C/FFFFFF?text=Bakso")
            )

            // Update the state with the loaded data. The UI will automatically react to this change.
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    promoBanners = promos,
                    categories = categories,
                    recommendedItems = menuItems
                )
            }
        }
    }
}
