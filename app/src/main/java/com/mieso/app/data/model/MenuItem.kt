package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class MenuItem(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Long = 0,
    val imageUrl: String = "",
    val categoryId: String = "",
    val categoryName: String = "",

    @get:PropertyName("isRecommended")
    val isRecommended: Boolean = false
)
