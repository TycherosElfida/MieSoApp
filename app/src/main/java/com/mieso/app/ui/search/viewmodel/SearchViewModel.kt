package com.mieso.app.ui.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.repository.HomeRepository
import com.mieso.app.ui.search.state.SearchUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query, isSearching = true) }
        searchJob?.cancel() // Batalkan pencarian sebelumnya
        searchJob = viewModelScope.launch {
            delay(300L) // Debounce untuk menunda pencarian
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        if (query.isBlank()) {
            _uiState.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        _uiState.update { it.copy(isLoading = true) }
        try {
            val results = repository.searchMenuItems(query)
            _uiState.update {
                it.copy(
                    searchResults = results,
                    isLoading = false,
                    isSearching = false
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    error = "Failed to perform search.",
                    isSearching = false
                )
            }
        }
    }
}