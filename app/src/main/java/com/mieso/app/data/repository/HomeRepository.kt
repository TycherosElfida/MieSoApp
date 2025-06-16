package com.mieso.app.data.repository

import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner

interface HomeRepository {
    suspend fun getPromoBanners(): List<PromoBanner>
    suspend fun getCategories(): List<FoodCategory>
    suspend fun getRecommendedItems(): List<MenuItem>

    // --- NEW FUNCTION ---
    suspend fun getMenuItemsByCategory(categoryId: String): List<MenuItem>
}