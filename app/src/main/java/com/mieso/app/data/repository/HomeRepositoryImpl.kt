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

    override suspend fun getPromoBanners(): List<PromoBanner> {
        return try {
            val snapshot = firestore.collection("promoBanners")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()
            snapshot.toObjects(PromoBanner::class.java)
        } catch (e: Exception) {
            // In a real app, you would log this error.
            emptyList()
        }
    }

    override suspend fun getCategories(): List<FoodCategory> {
        return try {
            val snapshot = firestore.collection("categories")
                .orderBy("order", Query.Direction.ASCENDING)
                .get()
                .await()
            snapshot.toObjects(FoodCategory::class.java)
        } catch (e: Exception) {
            // for logging the errors
            emptyList()
        }
    }

    override suspend fun getRecommendedItems(): List<MenuItem> {
        return try {
            val snapshot = firestore.collection("menuItems")
                .whereEqualTo("isRecommended", true)
                .limit(10) // Get up to 10 recommended items
                .get()
                .await()
            snapshot.toObjects(MenuItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

