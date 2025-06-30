package com.mieso.app.domain.usecase

import com.google.firebase.auth.FirebaseUser
import com.mieso.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    operator fun invoke(): Flow<FirebaseUser?> {
        return repository.getAuthState()
    }
}