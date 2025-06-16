package com.mieso.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HomeRepository {

    // ... (getPromoBanners, getCategories, getRecommendedItems functions remain the same)
    override suspend fun getPromoBanners(): List<PromoBanner> {
        return try {
            firestore.collection("promoBanners")
                .orderBy("order", Query.Direction.ASCENDING)
                .get().await().toObjects(PromoBanner::class.java)
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getCategories(): List<FoodCategory> {
        return try {
            firestore.collection("categories")
                .orderBy("order", Query.Direction.ASCENDING)
                .get().await().toObjects(FoodCategory::class.java)
        } catch (e: Exception) { emptyList() }
    }

    override suspend fun getRecommendedItems(): List<MenuItem> {
        return try {
            firestore.collection("menuItems")
                .whereEqualTo("isRecommended", true)
                .limit(10).get().await().toObjects(MenuItem::class.java)
        } catch (e: Exception) { emptyList() }
    }


    // --- NEW FUNCTION IMPLEMENTATION ---
    override suspend fun getMenuItemsByCategory(categoryId: String): List<MenuItem> {
        return try {
            // Note: In a production app, you might want to fetch the category name
            // from the categoryId to display in the header. For now, we assume
            // the ID is sufficient for the query. To do this, we query the 'categories'
            // collection first, find the document with the matching ID, and then use its 'name'
            // field in the 'whereEqualTo' clause for 'menuItems'.
            // For this implementation, we will assume the categoryId passed is the name itself.
            val categoryDocument = firestore.collection("categories").document(categoryId).get().await()
            val categoryName = categoryDocument.getString("name") ?: ""

            if (categoryName.isNotEmpty()) {
                firestore.collection("menuItems")
                    .whereEqualTo("category", categoryName)
                    .get()
                    .await()
                    .toObjects(MenuItem::class.java)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            // Log the error in a real app
            e.printStackTrace()
            emptyList()
        }
    }
}