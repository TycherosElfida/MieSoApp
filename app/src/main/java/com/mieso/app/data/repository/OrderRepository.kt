package com.mieso.app.data.repository

import com.mieso.app.data.model.Order
import kotlinx.coroutines.flow.Flow

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
}