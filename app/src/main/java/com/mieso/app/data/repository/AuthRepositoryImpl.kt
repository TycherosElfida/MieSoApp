package com.mieso.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.mieso.app.data.auth.SignInResult
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
            val authResult = auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()
            val firebaseUser = authResult.user!!

            // If the user is new, create their document. Otherwise, fetch it.
            val user = if (authResult.additionalUserInfo?.isNewUser == true) {
                createUserDocument(firebaseUser)
            } else {
                getUserDocument(firebaseUser.uid)!!
            }
            SignInResult.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun createUserWithEmail(email: String, password: String): SignInResult {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user!!
            val user = createUserDocument(firebaseUser)
            SignInResult.Success(user)
        } catch (e: Exception) {
            e.printStackTrace()
            SignInResult.Error(e.message ?: "An unknown error occurred.")
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): SignInResult {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            val user = getUserDocument(auth.currentUser!!.uid)!!
            SignInResult.Success(user)
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

    private suspend fun createUserDocument(firebaseUser: FirebaseUser): User {
        val user = User(
            id = firebaseUser.uid,
            username = firebaseUser.displayName,
            email = firebaseUser.email,
            profilePictureUrl = firebaseUser.photoUrl?.toString(),
            role = "user" // Default role
        )
        firestore.collection("users").document(firebaseUser.uid).set(user).await()
        return user
    }

    private suspend fun getUserDocument(uid: String): User? {
        return try {
            firestore.collection("users").document(uid).get().await().toObject<User>()
        } catch (e: Exception) {
            null
        }
    }
}