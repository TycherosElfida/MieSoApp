package com.mieso.app.ui.search.state

import com.mieso.app.data.model.MenuItem

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<MenuItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSearching: Boolean = false
)