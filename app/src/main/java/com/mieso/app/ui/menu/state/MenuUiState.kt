package com.mieso.app.ui.menu.state

import com.mieso.app.data.model.MenuItem

data class MenuUiState(
    val isLoading: Boolean = true,
    val menuItems: List<MenuItem> = emptyList(),
    val categoryTitle: String = "",
    val error: String? = null
)