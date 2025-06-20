package com.mieso.app.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.repository.HomeRepository
import com.mieso.app.ui.home.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository // Hilt injects our repository here
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHomeScreenData()
    }

    private fun loadHomeScreenData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                // Fetch all data in parallel for better performance
                val banners = homeRepository.getPromoBanners()
                val categories = homeRepository.getCategories()
                val recommended = homeRepository.getRecommendedItems()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        promoBanners = banners,
                        categories = categories,
                        recommendedItems = recommended
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = "Failed to load data.")
                }
            }
        }
    }
}
