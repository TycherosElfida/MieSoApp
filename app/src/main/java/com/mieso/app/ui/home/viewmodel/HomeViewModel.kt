package com.mieso.app.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.repository.HomeRepository
import com.mieso.app.ui.home.state.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository
) : ViewModel() {

    // A trigger that we can use to manually re-initiate the data streams.
    private val _retryTrigger = MutableStateFlow(0)

    /**
     * A StateFlow that represents the entire state of the Home screen.
     * It is constructed by combining multiple real-time data streams from the repository
     * along with our manual retry trigger.
     */
    val uiState: StateFlow<HomeUiState> = combine(
        homeRepository.getPromoBannersStream(),
        homeRepository.getCategoriesStream(),
        homeRepository.getRecommendedItemsStream(),
        homeRepository.getAllMenuItemsStream(),
        _retryTrigger // We combine the trigger here
    ) { banners, categories, recommended, allItems, _ ->
        // This transformation block is re-executed whenever any of the source streams emit a new value.
        HomeUiState(
            isLoading = false, // As soon as we get data, loading is complete.
            promoBanners = banners,
            categories = categories,
            recommendedItems = recommended,
            allMenuItems = allItems
        )
    }.catch { throwable ->
        // If any of the combined flows encounter an error, it's caught here.
        emit(
            HomeUiState(
                isLoading = false,
                error = throwable.message ?: "An unknown error occurred"
            )
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    /**
     * A public function that the UI can call to trigger a data refresh.
     * Incrementing the value of the trigger causes the `combine` block to re-execute.
     */
    fun onRetry() {
        _retryTrigger.value++
    }
}
