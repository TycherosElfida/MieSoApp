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
            firestore.collection("orders").add(order).await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    override fun getUserOrders(userId: String): Flow<List<Order>> {
        return firestore.collection("orders")
            .whereEqualTo("userId", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(Order::class.java)
            }
    }


    override fun getAllOrders(): Flow<List<Order>> {
        return firestore.collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { snapshot ->
                // Alih-alih langsung ke .toObjects(), kita akan memproses setiap dokumen
                // untuk memastikan ID-nya terpasang dengan benar.
                snapshot.documents.mapNotNull { document ->
                    // Konversi dokumen ke data class Order
                    val order = document.toObject(Order::class.java)
                    // Secara manual menetapkan ID dokumen ke properti 'id' di data class.
                    // Ini memastikan bahwa 'id' selalu berisi ID dokumen yang sebenarnya.
                    order?.apply {
                        id = document.id
                    }
                }
            }
    }

    override suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        return try {
            firestore.collection("orders").document(orderId)
                .update("status", newStatus)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
