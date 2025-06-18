package com.mieso.app.data.repository

import com.mieso.app.data.model.Order

// NEW: Interface for order-related data operations
interface OrderRepository {
    /**
     * Saves a new order to the remote data source.
     * @param order The order object to be saved.
     * @return A Result indicating success or failure.
     */
    suspend fun placeOrder(order: Order): Result<Unit>
}