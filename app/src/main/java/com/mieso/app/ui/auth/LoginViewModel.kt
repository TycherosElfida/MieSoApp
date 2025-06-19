package com.mieso.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// NEW: ViewModel for the LoginScreen
data class LoginState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)

@Inject
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onSignInResult(idToken: String) {
        viewModelScope.launch {
            val signInResult = authRepository.googleSignIn(idToken)
            _state.update {
                it.copy(
                    isSignInSuccessful = signInResult.data != null,
                    signInError = signInResult.errorMessage
                )
            }
        }
    }

    fun resetState() {
        _state.update { LoginState() }
    }
}