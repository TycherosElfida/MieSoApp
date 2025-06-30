package com.mieso.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import com.mieso.app.data.model.FoodCategory
import com.mieso.app.data.model.MenuItem
import com.mieso.app.data.model.PromoBanner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implementation of HomeRepository using Firestore as the data source.
 * This version is refactored to use real-time snapshot listeners for data streams.
 */
class HomeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : HomeRepository {

    /* --- Real-Time Data Stream Implementations --- */

    override fun getPromoBannersStream(): Flow<List<PromoBanner>> {
        return firestore.collection("promoBanners")
            .orderBy("order", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(PromoBanner::class.java) }
    }

    override fun getCategoriesStream(): Flow<List<FoodCategory>> {
        return firestore.collection("categories")
            .orderBy("order", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(FoodCategory::class.java) }
    }

    override fun getRecommendedItemsStream(): Flow<List<MenuItem>> {
        return firestore.collection("menuItems")
            .whereEqualTo("isRecommended", true)
            .limit(10)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(MenuItem::class.java) }
    }

    override fun getAllMenuItemsStream(): Flow<List<MenuItem>> {
        return firestore.collection("menuItems")
            .orderBy("name")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(MenuItem::class.java) }
    }

    override fun getMenuItemsByCategoryStream(categoryId: String): Flow<List<MenuItem>> {
        return firestore.collection("menuItems")
            .whereEqualTo("categoryId", categoryId)
            .snapshots()
            .map { snapshot -> snapshot.toObjects(MenuItem::class.java) }
    }


    /* --- One-Time Fetch Implementations --- */

    override suspend fun getMenuItemById(menuItemId: String): MenuItem? {
        return try {
            firestore.collection("menuItems").document(menuItemId).get().await()
                .toObject(MenuItem::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getPromoBannerById(bannerId: String): PromoBanner? {
        return try {
            firestore.collection("promoBanners").document(bannerId).get().await()
                .toObject(PromoBanner::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun searchMenuItems(query: String): List<MenuItem> {
        if (query.isBlank()) {
            return emptyList()
        }
        return try {
            val allItems =
                firestore.collection("menuItems").get().await().toObjects(MenuItem::class.java)
            allItems.filter { menuItem ->
                menuItem.name.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    /* --- Data Mutation Implementations (Admin) --- */

    override suspend fun addMenuItem(menuItem: MenuItem) {
        try {
            val newDocRef = firestore.collection("menuItems").document()
            newDocRef.set(menuItem.copy(id = newDocRef.id)).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateMenuItem(menuItem: MenuItem) {
        try {
            if (menuItem.id.isBlank()) return
            firestore.collection("menuItems").document(menuItem.id).set(menuItem).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteMenuItem(itemId: String) {
        try {
            firestore.collection("menuItems").document(itemId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addCategory(category: FoodCategory) {
        try {
            val newDocRef = firestore.collection("categories").document()
            newDocRef.set(category.copy(id = newDocRef.id)).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateCategory(category: FoodCategory) {
        try {
            if (category.id.isBlank()) return
            firestore.collection("categories").document(category.id).set(category).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteCategory(categoryId: String) {
        try {
            firestore.collection("categories").document(categoryId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun addPromoBanner(banner: PromoBanner) {
        try {
            val newDocRef = firestore.collection("promoBanners").document()
            newDocRef.set(banner.copy(id = newDocRef.id)).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updatePromoBanner(banner: PromoBanner) {
        try {
            if (banner.id.isBlank()) return
            firestore.collection("promoBanners").document(banner.id).set(banner).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deletePromoBanner(bannerId: String) {
        try {
            firestore.collection("promoBanners").document(bannerId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
