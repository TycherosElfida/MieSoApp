package com.mieso.app.data.repository

import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    suspend fun getPromoBanners(): List<PromoBanner>
    // fun getCategories(): Flow<List<FoodCategory>>
    suspend fun getCategories(): List<FoodCategory>
    suspend fun getRecommendedItems(): List<MenuItem>
    suspend fun getMenuItemsByCategory(categoryId: String): List<MenuItem>
    suspend fun getMenuItemById(menuItemId: String): MenuItem?
    suspend fun searchMenuItems(query: String): List<MenuItem>
    fun getAllMenuItemsStream(): Flow<List<MenuItem>>
    suspend fun deleteMenuItem(itemId: String)
    suspend fun addMenuItem(menuItem: MenuItem)
    suspend fun updateMenuItem(menuItem: MenuItem)
    suspend fun addCategory(category: FoodCategory)
    suspend fun updateCategory(category: FoodCategory)
    suspend fun deleteCategory(categoryId: String)
    fun getPromoBannersStream(): Flow<List<PromoBanner>>
    suspend fun getPromoBannerById(bannerId: String): PromoBanner?
    suspend fun addPromoBanner(banner: PromoBanner)
    suspend fun updatePromoBanner(banner: PromoBanner)
    suspend fun deletePromoBanner(bannerId: String)
    suspend fun getAllMenuItems(): List<MenuItem>
}