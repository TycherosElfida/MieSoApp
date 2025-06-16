package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class FoodCategory(
    @DocumentId val id: String = "", // Map the document ID
    val name: String = "",
    val order: Int = 0 // Add the missing 'order' field
)