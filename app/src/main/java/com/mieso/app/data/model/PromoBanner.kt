package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class PromoBanner(
    @DocumentId val id: String = "",
    val imageUrl: String = "",
    val targetScreen: String = "", // e.g., "menu/itemId" or "promo/promoId"
    val order: Int = 0
)
