package com.mieso.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.mieso.app.data.model.Order
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : OrderRepository {
    override suspend fun placeOrder(order: Order): Result<Unit> {
        return try {
            // The `order` object now reliably contains the correct `userId`.
            // The security rule `allow create: if request.auth.uid != null;` permits this.
            // Firestore will automatically handle the server-side timestamp for `createdAt`.
            firestore.collection("orders").add(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getUserOrders(userId: String): Flow<List<Order>> {
        // This query correctly filters orders based on the `userId` field.
        // The security rule `allow read: if request.auth.uid == resource.data.userId` ensures
        // users can only read their own orders.
        return firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Order::class.java)
            }
    }
}