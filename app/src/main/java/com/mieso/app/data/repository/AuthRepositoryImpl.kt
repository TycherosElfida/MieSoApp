package com.mieso.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.mieso.app.data.auth.SignInResult
import com.mieso.app.data.auth.UserData
import com.mieso.app.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun getAuthState(): Flow<FirebaseUser?> {
        val authState = MutableStateFlow(auth.currentUser)
        auth.addAuthStateListener { firebaseAuth ->
            authState.value = firebaseAuth.currentUser
        }
        return authState
    }

    override suspend fun firebaseSignInWithGoogle(idToken: String): SignInResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val user = authResult.user!!

            // Check if this is a new user
            if (authResult.additionalUserInfo?.isNewUser == true) {
                createUserDocument(user)
            }

            SignInResult.Success(
                UserData(
                    userId = user.uid,
                    username = user.displayName,
                    profilePictureUrl = user.photoUrl?.toString(),
                    email = user.email
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun createUserWithEmail(email: String, password: String): SignInResult {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user!!
            createUserDocument(user) // Create user document in Firestore
            SignInResult.Success(
                UserData(
                    userId = user.uid,
                    username = user.displayName,
                    profilePictureUrl = user.photoUrl?.toString(),
                    email = user.email
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): SignInResult {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val user = authResult.user!!
            SignInResult.Success(
                UserData(
                    userId = user.uid,
                    username = user.displayName,
                    profilePictureUrl = user.photoUrl?.toString(),
                    email = user.email
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun signOut() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun createUserDocument(firebaseUser: FirebaseUser) {
        val user = User(
            id = firebaseUser.uid,
            username = firebaseUser.displayName,
            email = firebaseUser.email,
            profilePictureUrl = firebaseUser.photoUrl?.toString(),
            role = "user" // Default role
        )
        firestore.collection("users").document(firebaseUser.uid).set(user).await()
    }
}