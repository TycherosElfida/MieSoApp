package com.mieso.app.ui.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.mieso.app.data.auth.UserData
import com.mieso.app.data.model.User
import com.mieso.app.data.repository.AuthRepository
import com.mieso.app.ui.profile.state.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val user: StateFlow<User?> = authRepository.getAuthState().flatMapLatest { firebaseUser ->
        if (firebaseUser != null) {
            firestore.collection("users").document(firebaseUser.uid).snapshots().map { snapshot ->
                snapshot.toObject(User::class.java)
            }
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Observe the authentication state from the repository.
        viewModelScope.launch {
            authRepository.getAuthState().collect { firebaseUser ->
                // Map the FirebaseUser to our simpler UserData class for the UI.
                val userData = firebaseUser?.let {
                    UserData(
                        userId = it.uid,
                        username = it.displayName,
                        profilePictureUrl = it.photoUrl?.toString(),
                        email = it.email
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