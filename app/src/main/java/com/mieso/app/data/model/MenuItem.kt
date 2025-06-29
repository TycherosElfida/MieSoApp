package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class MenuItem(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Long = 0,
    val imageUrl: String = "",
    val categoryId: String = "", // This will be our primary link for queries
    val categoryName: String = "", // This is for display purposes
    val isRecommended: Boolean = false
)
