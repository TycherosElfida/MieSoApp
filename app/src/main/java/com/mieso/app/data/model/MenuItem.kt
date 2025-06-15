package com.mieso.app.data.model

data class MenuItem(
    val id: String = "",
    val name: String = "",
    val price: Long = 0,
    val imageUrl: String = "",
    val category: String = "",
    val isRecommended: Boolean = false
)