package com.mieso.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.mieso.app.data.auth.SignInResult
import com.mieso.app.data.auth.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// NEW: Implementation of the AuthRepository
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override fun getAuthState(): Flow<FirebaseUser?> {
        // Use a state flow to emit the current user state on subscription
        val authState = MutableStateFlow(auth.currentUser)
        auth.addAuthStateListener { firebaseAuth ->
            authState.value = firebaseAuth.currentUser
        }
        return authState
    }

    override suspend fun googleSignIn(idToken: String): SignInResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user!!
            SignInResult(
                data = UserData(
                    userId = user.uid,
                    username = user.displayName,
                    profilePictureUrl = user.photoUrl?.toString()
                ),
                errorMessage = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
