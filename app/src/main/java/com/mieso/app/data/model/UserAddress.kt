package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class UserAddress(
    @DocumentId val id: String = "",
    val label: String = "", // e.g., "Rumah", "Kantor"
    val addressLine1: String = "",
    val addressLine2: String? = null, // e.g., Apartment number, notes
    val city: String = "",
    val postalCode: String = "",
    var isPrimary: Boolean = false
)