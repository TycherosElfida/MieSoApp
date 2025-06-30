package com.mieso.app.data.repository

import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner
import kotlinx.coroutines.flow.Flow

/**
 * Interface for handling home screen and admin-related data operations.
 * This version is cleaned up to prioritize real-time data streams using Flow.
 */
interface HomeRepository {

    /* --- Real-Time Data Streams --- */

    fun getPromoBannersStream(): Flow<List<PromoBanner>>
    fun getCategoriesStream(): Flow<List<FoodCategory>>
    fun getRecommendedItemsStream(): Flow<List<MenuItem>>
    fun getAllMenuItemsStream(): Flow<List<MenuItem>>
    fun getMenuItemsByCategoryStream(categoryId: String): Flow<List<MenuItem>>


    /* --- One-Time Fetch Operations --- */

    suspend fun getMenuItemById(menuItemId: String): MenuItem?
    suspend fun getPromoBannerById(bannerId: String): PromoBanner?
    suspend fun searchMenuItems(query: String): List<MenuItem>


    /* --- Data Mutation Operations (Admin) --- */

    suspend fun addMenuItem(menuItem: MenuItem)
    suspend fun updateMenuItem(menuItem: MenuItem)
    suspend fun deleteMenuItem(itemId: String)

    suspend fun addCategory(category: FoodCategory)
    suspend fun updateCategory(category: FoodCategory)
    suspend fun deleteCategory(categoryId: String)

    suspend fun addPromoBanner(banner: PromoBanner)
    suspend fun updatePromoBanner(banner: PromoBanner)
    suspend fun deletePromoBanner(bannerId: String)
}
