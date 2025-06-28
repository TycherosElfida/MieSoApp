package com.mieso.app.ui.home.state

import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner

data class HomeUiState(
    val isLoading: Boolean = true,
    val promoBanners: List<PromoBanner> = emptyList(),
    val categories: List<FoodCategory> = emptyList(),
    val recommendedItems: List<MenuItem> = emptyList(),
    val allMenuItems: List<MenuItem> = emptyList(),
    val error: String? = null // To handle potential errors
)