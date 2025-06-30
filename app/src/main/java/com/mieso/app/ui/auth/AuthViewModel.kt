package com.mieso.app.ui.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mieso.app.data.common.Resource
import com.mieso.app.data.model.User
import com.mieso.app.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.mieso.app.domain.usecase.SignInWithGoogleUseCase
import com.mieso.app.domain.usecase.SignUpWithEmailAndPasswordUseCase
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
    val authResult: Resource<User>? = null
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    private val signUpWithEmailAndPasswordUseCase: SignUpWithEmailAndPasswordUseCase
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
            val newMode =
                if (it.authMode == AuthMode.SIGN_IN) AuthMode.SIGN_UP else AuthMode.SIGN_IN
            it.copy(
                authMode = newMode,
                emailError = null,
                passwordError = null
            )
        }
    }

    fun onGoogleSignInClick() {
        viewModelScope.launch {
            _state.update { it.copy(authResult = Resource.Loading) }
            val result = signInWithGoogleUseCase()
            _state.update { it.copy(authResult = result) }
        }
    }

    fun onEmailAuthClick() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _state.update { it.copy(authResult = Resource.Loading) }
            val result = when (_state.value.authMode) {
                AuthMode.SIGN_IN -> signInWithEmailAndPasswordUseCase(
                    _state.value.email,
                    _state.value.password
                )

                AuthMode.SIGN_UP -> signUpWithEmailAndPasswordUseCase(
                    _state.value.email,
                    _state.value.password
                )
            }
            _state.update { it.copy(authResult = result) }
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

    fun resetAuthResult() {
        _state.update { it.copy(authResult = null) }
    }
}