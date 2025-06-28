package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val username: String? = null,
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val role: String = "user" // Default role is "user"
)