package com.mieso.app.data.repository

import com.google.firebase.auth.FirebaseUser
import com.mieso.app.data.auth.SignInResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getAuthState(): Flow<FirebaseUser?>
    suspend fun firebaseSignInWithGoogle(idToken: String): SignInResult
    suspend fun signOut()

    suspend fun createUserWithEmail(email: String, password: String): SignInResult

    suspend fun signInWithEmail(email: String, password: String): SignInResult
}