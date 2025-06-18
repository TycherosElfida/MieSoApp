package com.mieso.app.data.repository

import com.mieso.app.data.model.UserAddress
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    // We assume a fixed userId for this prototype. In a real app,
    // this would be the currently authenticated user's ID.
    fun getUserAddresses(userId: String): Flow<List<UserAddress>>
    suspend fun addAddress(userId: String, address: UserAddress)
    // Add other functions like updateUser, etc. later
}