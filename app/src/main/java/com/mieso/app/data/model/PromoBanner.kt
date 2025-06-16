package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class PromoBanner(
    @DocumentId val id: String = "", // This annotation maps the document ID
    val imageUrl: String = "",
    val targetScreen: String = "",
    val order: Int = 0 // Add the missing 'order' field
)
