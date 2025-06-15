package com.mieso.app.ui.home.state

// We define simple data classes for the items we'll display.
// In a real app, these would come from our `data/model` package and be more complex.
data class PromoBanner(val id: Int, val imageUrl: String)
data class FoodCategory(val id: Int, val name: String)
data class MenuItem(val id: Int, val name:String, val price: String, val imageUrl: String)

// This is the main state class for the Home Screen.
data class HomeUiState(
    val isLoading: Boolean = true,
    val promoBanners: List<PromoBanner> = emptyList(),
    val categories: List<FoodCategory> = emptyList(),
    val recommendedItems: List<MenuItem> = emptyList()
)