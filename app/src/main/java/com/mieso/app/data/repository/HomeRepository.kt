package com.mieso.app.data.repository

import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner

interface HomeRepository {
    suspend fun getPromoBanners(): List<PromoBanner>
    suspend fun getCategories(): List<FoodCategory>
    suspend fun getRecommendedItems(): List<MenuItem>
    suspend fun getMenuItemsByCategory(categoryId: String): List<MenuItem>
    suspend fun getMenuItemById(menuItemId: String): MenuItem?
    suspend fun searchMenuItems(query: String): List<MenuItem>
}