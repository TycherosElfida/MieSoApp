package com.mieso.app.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.auth.UserData
import com.mieso.app.data.repository.AuthRepository
import com.mieso.app.ui.profile.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Observe the authentication state from the repository.
        viewModelScope.launch {
            authRepository.getAuthState().collect { firebaseUser ->
                // Map the FirebaseUser to our simpler UserData class for the UI.
                val userData = firebaseUser?.let {
                    UserData(
                        userId = it.uid,
                        username = it.displayName,
                        profilePictureUrl = it.photoUrl?.toString()
                    )
                }
                _uiState.update { it.copy(isLoading = false, userData = userData) }
            }
        }
    }

    /**
     * Executes the sign-out process by calling the repository.
     * The UI will reactively navigate away when the auth state becomes null.
     */
    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}
