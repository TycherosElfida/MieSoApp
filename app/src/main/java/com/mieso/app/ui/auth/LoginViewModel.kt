package com.mieso.app.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.auth.GoogleAuthHandler
import com.mieso.app.data.auth.SignInResult
import com.mieso.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthMode {
    SIGN_IN, SIGN_UP
}

data class AuthScreenState(
    val authMode: AuthMode = AuthMode.SIGN_IN,
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isSigningIn: Boolean = false,
    val signInResult: SignInResult? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authHandler: GoogleAuthHandler,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthScreenState())
    val state = _state.asStateFlow()

    fun onEmailChanged(email: String) {
        _state.update { it.copy(email = email, emailError = null) }
    }

    fun onPasswordChanged(password: String) {
        _state.update { it.copy(password = password, passwordError = null) }
    }

    fun onAuthModeToggled() {
        _state.update {
            val newMode = if (it.authMode == AuthMode.SIGN_IN) AuthMode.SIGN_UP else AuthMode.SIGN_IN
            it.copy(
                authMode = newMode,
                emailError = null,
                passwordError = null
            )
        }
    }

    fun onGoogleSignInClick() {
        viewModelScope.launch {
            _state.update { it.copy(isSigningIn = true) }
            val idToken = authHandler.signIn()
            val result = if (idToken != null) {
                authRepository.firebaseSignInWithGoogle(idToken)
            } else {
                SignInResult.Error("Sign-in was cancelled or failed.")
            }
            _state.update { it.copy(signInResult = result, isSigningIn = false) }
        }
    }

    fun onEmailAuthClick() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _state.update { it.copy(isSigningIn = true) }
            val result = when (_state.value.authMode) {
                AuthMode.SIGN_IN -> authRepository.signInWithEmail(
                    _state.value.email,
                    _state.value.password
                )
                AuthMode.SIGN_UP -> authRepository.createUserWithEmail(
                    _state.value.email,
                    _state.value.password
                )
            }
            _state.update { it.copy(signInResult = result, isSigningIn = false) }
        }
    }

    private fun validateInputs(): Boolean {
        val email = _state.value.email
        val password = _state.value.password
        var isValid = true

        val emailError = if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false
            "Enter a valid email address."
        } else null

        val passwordError = if (password.length < 6) {
            isValid = false
            "Password must be at least 6 characters."
        } else null

        _state.update {
            it.copy(emailError = emailError, passwordError = passwordError)
        }
        return isValid
    }

    fun resetSignInResult() {
        _state.update { it.copy(signInResult = null) }
    }
}