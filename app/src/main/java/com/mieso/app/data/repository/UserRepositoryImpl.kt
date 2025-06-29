package com.mieso.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.mieso.app.data.model.UserAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override fun getUserAddresses(userId: String): Flow<List<UserAddress>> {
        return firestore.collection("users").document(userId).collection("addresses")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(UserAddress::class.java)
            }
    }

//    override suspend fun addAddress(userId: String, address: UserAddress) {
//        firestore.collection("users").document(userId).collection("addresses")
//            .add(address).await() // Using .add() is correct for creating a new document with an auto-generated ID
//    }

    override suspend fun addAddress(userId: String, address: UserAddress) {
        try {
            firestore.collection("users").document(userId).collection("addresses")
                .add(address).await()
        } catch (e: Exception) {
            // Cetak error ke Logcat untuk debugging, tapi jangan biarkan aplikasi crash.
            e.printStackTrace()
        }
    }

    override suspend fun updateUserProfile(userId: String, newUsername: String, newProfilePictureUrl: String): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(userId)
            val updates = mapOf(
                "username" to newUsername,
                "profilePictureUrl" to newProfilePictureUrl
            )
            userRef.update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}