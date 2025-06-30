package com.mieso.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mieso.app.data.auth.UserDataProvider
import com.mieso.app.data.model.User
import com.mieso.app.domain.usecase.GetAuthStateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

enum class AuthStatus {
    LOADING,
    AUTHENTICATED,
    UNAUTHENTICATED
}

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAuthStateUseCase: GetAuthStateUseCase,
    private val firestore: FirebaseFirestore,
    private val userDataProvider: UserDataProvider
) : ViewModel() {

    private val _authStatus = MutableStateFlow(AuthStatus.LOADING)
    val authStatus = _authStatus.asStateFlow()

    init {
        checkAuthenticationState()
    }

    fun refreshAuthenticationState() {
        _authStatus.value = AuthStatus.LOADING
        checkAuthenticationState()
    }

    private fun checkAuthenticationState() {
        viewModelScope.launch {
            val firebaseUser = getAuthStateUseCase().first()

            if (firebaseUser != null) {
                val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                val user = userDoc.toObject<User>()

                if (user != null) {
                    userDataProvider.setUser(user)
                    _authStatus.value = AuthStatus.AUTHENTICATED
                } else {
                    _authStatus.value = AuthStatus.UNAUTHENTICATED
                }
            } else {
                userDataProvider.clear()
                _authStatus.value = AuthStatus.UNAUTHENTICATED
            }
        }
    }
}