package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class PromoBanner(
    @DocumentId val id: String = "",
    val imageUrl: String = "",
    val targetScreen: String = "",
    val order: Int = 0
)
