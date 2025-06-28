package com.mieso.app.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.auth.UserDataProvider
import com.mieso.app.data.model.User
import com.mieso.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    userDataProvider: UserDataProvider // Inject the provider
) : ViewModel() {

    val user: StateFlow<User?> = userDataProvider.user
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}