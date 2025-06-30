package com.mieso.app.domain.usecase

import com.mieso.app.data.common.Resource
import com.mieso.app.data.model.User
import com.mieso.app.data.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailAndPasswordUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        return repository.signInWithEmail(email, password)
    }
}