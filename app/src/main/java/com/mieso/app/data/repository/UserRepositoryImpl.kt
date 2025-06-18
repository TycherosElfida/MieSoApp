package com.mieso.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.mieso.app.data.model.UserAddress
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    // For this prototype, we'll use a hardcoded user ID.
    // In a real app, you would get this from FirebaseAuth.
    private val prototypeUserId = "Eric_lil_KT"

    override fun getUserAddresses(userId: String): Flow<List<UserAddress>> {
        return firestore.collection("users").document(userId).collection("addresses")
            .snapshots() // Use snapshots() for real-time updates
            .map { snapshot ->
                snapshot.toObjects(UserAddress::class.java)
            }
    }

    override suspend fun addAddress(userId: String, address: UserAddress) {
        firestore.collection("users").document(userId).collection("addresses")
            .add(address)
    }
}