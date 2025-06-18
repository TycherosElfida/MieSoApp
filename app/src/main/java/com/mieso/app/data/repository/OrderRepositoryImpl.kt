package com.mieso.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.mieso.app.data.model.Order
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// NEW: Implementation of the OrderRepository
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
}