package com.mieso.app.domain.usecase

import com.mieso.app.data.auth.GoogleAuthHandler
import com.mieso.app.data.common.Resource
import com.mieso.app.data.model.User
import com.mieso.app.data.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val googleAuthHandler: GoogleAuthHandler,
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Resource<User> {
        val idToken = googleAuthHandler.signIn()
        return if (idToken != null) {
            repository.firebaseSignInWithGoogle(idToken)
        } else {
            Resource.Error("Sign-in was cancelled or failed.")
        }
    }
}