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
            // Adds the order object to the "orders" collection in Firestore
            firestore.collection("orders").add(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // In case of an error (e.g., network issue), return a failure result
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getUserOrders(userId: String): Flow<List<Order>> {
        // Query the 'orders' collection...
        return firestore.collection("orders")
            // ...for documents where the 'userId' field matches the current user's ID...
            .whereEqualTo("userId", userId)
            // ...and sort them by creation date, with the newest orders first.
            .orderBy("createdAt", Query.Direction.DESCENDING)
            // .snapshots() provides a Flow that emits updates in real-time.
            .snapshots()
            .map { snapshot ->
                // Convert the query snapshot into a list of Order objects.
                snapshot.toObjects(Order::class.java)
            }
    }
}