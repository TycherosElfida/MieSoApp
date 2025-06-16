package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class MenuItem(
    @DocumentId val id: String = "", // Map the document ID
    val name: String = "",
    val price: Long = 0,
    val imageUrl: String = "",
    val category: String = "",
    val isRecommended: Boolean = false // Add the missing 'isRecommended' field
)
