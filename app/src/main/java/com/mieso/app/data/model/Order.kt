package com.mieso.app.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude // <-- TAMBAHKAN IMPORT INI
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Order(
    // --- PERUBAHAN UTAMA ADA DI SINI ---
    /**
     * @DocumentId: Otomatis diisi dengan ID dokumen dari Firestore saat dibaca.
     * @get:Exclude: Mencegah properti ini ditulis sebagai field di dalam dokumen Firestore.
     * var: Properti yang dianotasi dengan @DocumentId harus bisa diubah (mutable).
     */
    @DocumentId @get:Exclude var id: String = "",

    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val shippingAddress: UserAddress? = null,
    val subtotal: Long = 0,
    val deliveryFee: Long = 0,
    val total: Long = 0,
    val paymentMethod: String = "COD",
    val status: String = "Pending", // e.g., Pending, Confirmed, Delivered, Canceled
    @ServerTimestamp val createdAt: Date? = null
)
