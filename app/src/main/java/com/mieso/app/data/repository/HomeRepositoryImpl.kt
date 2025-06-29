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

    override suspend fun getMenuItemsByCategory(categoryId: String): List<MenuItem> {
        return try {
            firestore.collection("menuItems")
                .whereEqualTo("categoryId", categoryId) // Query by the correct field: categoryId
                .get()
                .await()
                .toObjects(MenuItem::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getMenuItemById(menuItemId: String): MenuItem? {
        return try {
            firestore.collection("menuItems")
                .document(menuItemId)
                .get()
                .await()
                .toObject(MenuItem::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getAllMenuItems(): List<MenuItem> {
        return try {
            firestore.collection("menuItems")
                .orderBy("name") // Urutkan berdasarkan nama untuk konsistensi
                .get()
                .await()
                .toObjects(MenuItem::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun searchMenuItems(query: String): List<MenuItem> {
        if (query.isBlank()) {
            return emptyList()
        }
        return try {
            // Ambil semua item menu. Untuk aplikasi skala besar, pertimbangkan
            // layanan pencarian pihak ketiga seperti Algolia atau Elasticsearch.
            val allItems = firestore.collection("menuItems")
                .get()
                .await()
                .toObjects(MenuItem::class.java)

            // Filter di sisi klien dengan case-insensitive
            allItems.filter { menuItem ->
                menuItem.name.contains(query, ignoreCase = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getAllMenuItemsStream(): Flow<List<MenuItem>> {
        return firestore.collection("menuItems")
            .orderBy("name")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(MenuItem::class.java)
            }
    }

    override suspend fun deleteMenuItem(itemId: String) {
        try {
            firestore.collection("menuItems").document(itemId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // THIS IS THE CRITICAL FIX for CREATING new menu items.
    override suspend fun addMenuItem(menuItem: MenuItem) {
        try {
            // 1. Create a reference to a new, empty document in the 'menuItems' collection.
            val newMenuItemRef = firestore.collection("menuItems").document()
            // 2. Explicitly set the ID in our object from the new document reference.
            val menuItemWithId = menuItem.copy(id = newMenuItemRef.id)
            // 3. Use .set() to save the complete object to the new document reference.
            newMenuItemRef.set(menuItemWithId).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // THIS IS THE CRITICAL FIX for UPDATING existing menu items.
    override suspend fun updateMenuItem(menuItem: MenuItem) {
        try {
            // Guard against trying to update an item without an ID.
            if (menuItem.id.isBlank()) {
                throw IllegalArgumentException("MenuItem ID cannot be blank for update.")
            }
            // Use .set() on a document reference that includes the specific ID.
            firestore.collection("menuItems").document(menuItem.id).set(menuItem).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // We will apply the same robust logic to categories and banners.
    override suspend fun addCategory(category: FoodCategory) {
        try {
            val newCategoryRef = firestore.collection("categories").document()
            val categoryWithId = category.copy(id = newCategoryRef.id)
            newCategoryRef.set(categoryWithId).await()
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

    override fun getPromoBannersStream(): Flow<List<PromoBanner>> {
        return firestore.collection("promoBanners")
            .orderBy("order")
            .snapshots()
            .map { snapshot -> snapshot.toObjects(PromoBanner::class.java) }
    }

    // v-- ADD THIS FUNCTION IMPLEMENTATION --v
    override suspend fun getPromoBannerById(bannerId: String): PromoBanner? {
        return try {
            firestore.collection("promoBanners").document(bannerId).get().await()
                .toObject(PromoBanner::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun addPromoBanner(banner: PromoBanner) {
        try {
            val newBannerRef = firestore.collection("promoBanners").document()
            val bannerWithId = banner.copy(id = newBannerRef.id)
            newBannerRef.set(bannerWithId).await()
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