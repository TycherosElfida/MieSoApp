package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class FoodCategory(
    @DocumentId val id: String = "",
    val name: String = "",
    val order: Int = 0
)