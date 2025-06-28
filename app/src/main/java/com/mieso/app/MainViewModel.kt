package com.mieso.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mieso.app.data.auth.UserDataProvider
import com.mieso.app.data.model.User
import com.mieso.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
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
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore,
    private val userDataProvider: UserDataProvider
) : ViewModel() {

    private val _authStatus = MutableStateFlow(AuthStatus.LOADING)
    val authStatus = _authStatus.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.getAuthState().collectLatest { firebaseUser ->
                if (firebaseUser != null) {
                    // User is logged in to Firebase Auth, now get their data from Firestore
                    val userDoc = firestore.collection("users").document(firebaseUser.uid).get().await()
                    val user = userDoc.toObject<User>()
                    userDataProvider.setUser(user)
                    _authStatus.value = AuthStatus.AUTHENTICATED
                } else {
                    // User is logged out
                    userDataProvider.clear()
                    _authStatus.value = AuthStatus.UNAUTHENTICATED
                }
            }
        }
    }
}