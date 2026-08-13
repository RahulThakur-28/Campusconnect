package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.LostFoundItem

interface LostFoundRepository {
    suspend fun getItems(): Result<List<LostFoundItem>>
    suspend fun getItemById(itemId: String): Result<LostFoundItem?>
    suspend fun createItem(item: LostFoundItem): Result<String>
    suspend fun updateItem(item: LostFoundItem): Result<Unit>
    suspend fun deleteItem(itemId: String, imagePath: String?): Result<Unit>
    suspend fun markAsResolved(itemId: String): Result<Unit>
    suspend fun getMyItems(userId: String): Result<List<LostFoundItem>>
    suspend fun uploadImage(itemId: String, imageUri: Uri): Result<Pair<String, String>>
    suspend fun uploadImage(collegeId: String, itemId: String, imageUri: Uri): Result<Pair<String, String>>
    suspend fun deleteFile(path: String): Result<Unit>
}
