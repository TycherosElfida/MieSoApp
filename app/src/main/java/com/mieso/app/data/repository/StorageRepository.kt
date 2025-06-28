package com.mieso.app.data.repository

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

interface StorageRepository {
    suspend fun uploadMenuImage(imageUri: Uri): String?
}

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage
) : StorageRepository {
    override suspend fun uploadMenuImage(imageUri: Uri): String? {
        return try {
            val storageRef = storage.reference
            val imageRef = storageRef.child("menu_images/${UUID.randomUUID()}")
            imageRef.putFile(imageUri).await()
            imageRef.downloadUrl.await()?.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}