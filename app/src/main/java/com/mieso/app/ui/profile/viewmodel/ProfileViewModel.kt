package com.mieso.app.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.auth.UserDataProvider
import com.mieso.app.data.model.User
import com.mieso.app.data.repository.AuthRepository
import com.mieso.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val userDataProvider: UserDataProvider
) : ViewModel() {

    val user: StateFlow<User?> = userDataProvider.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _updateResult = MutableStateFlow<Result<Unit>?>(null)
    val updateResult = _updateResult.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun updateProfile(username: String, profilePictureUrl: String) {
        viewModelScope.launch {
            val userId = user.value?.id
            if (userId == null) {
                _updateResult.value = Result.failure(Exception("User not found."))
                return@launch
            }
            if (username.isBlank()) {
                _updateResult.value = Result.failure(Exception("Username cannot be empty."))
                return@launch
            }

            val result = userRepository.updateUserProfile(userId, username, profilePictureUrl)
            _updateResult.value = result

            // Jika berhasil, perbarui data user secara lokal
            if (result.isSuccess) {
                val updatedUser = user.value?.copy(
                    username = username,
                    profilePictureUrl = profilePictureUrl
                )
                userDataProvider.setUser(updatedUser)
            }
        }
    }

    fun consumeUpdateResult() {
        _updateResult.value = null
    }
}