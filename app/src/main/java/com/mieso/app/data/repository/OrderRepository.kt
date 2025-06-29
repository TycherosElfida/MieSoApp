// File: app/src/main/java/com/mieso/app/data/repository/OrderRepository.kt

package com.mieso.app.data.repository

import com.mieso.app.data.model.Order
import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling order-related data operations.
 */
interface OrderRepository {
    /**
     * Saves a new order to the remote data source.
     * @param order The order object to be saved.
     * @return A Result indicating success or failure.
     */
    suspend fun placeOrder(order: Order): Result<Unit>

    /**
     * Retrieves a real-time stream of orders for a specific user.
     * @param userId The unique ID of the user whose orders are to be fetched.
     * @return A Flow emitting a list of orders.
     */
    fun getUserOrders(userId: String): Flow<List<Order>>

    // --- TAMBAHKAN FUNGSI BARU DI BAWAH INI ---
    /**
     * Retrieves a real-time stream of ALL orders for the admin.
     * This function fetches all documents from the 'orders' collection.
     * @return A Flow emitting a list of all orders, typically sorted by date.
     */
    fun getAllOrders(): Flow<List<Order>>

    // --- TAMBAHKAN FUNGSI BARU DI BAWAH INI ---
    /**
     * Updates the status of a specific order in Firestore.
     * @param orderId The unique ID of the order document to update.
     * @param newStatus The new status string (e.g., "Completed", "Canceled").
     * @return A Result indicating success or failure of the operation.
     */
    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit>
}
