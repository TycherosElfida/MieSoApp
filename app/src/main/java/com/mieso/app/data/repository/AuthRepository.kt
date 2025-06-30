package com.mieso.app.data.repository

import com.google.firebase.auth.FirebaseUser
import com.mieso.app.data.common.Resource
import com.mieso.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun getAuthState(): Flow<FirebaseUser?>
    suspend fun firebaseSignInWithGoogle(idToken: String): Resource<User>
    suspend fun signOut()
    suspend fun createUserWithEmail(email: String, password: String): Resource<User>
    suspend fun signInWithEmail(email: String, password: String): Resource<User>
}