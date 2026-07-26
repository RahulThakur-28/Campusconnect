package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.LostFoundItem

interface LostFoundRemoteDataSource {
    suspend fun getItems(collegeId: String): Result<List<LostFoundItem>>
    suspend fun getItemById(collegeId: String, itemId: String): Result<LostFoundItem?>
    suspend fun createItem(collegeId: String, item: LostFoundItem): Result<String>
    suspend fun updateItem(collegeId: String, item: LostFoundItem): Result<Unit>
    suspend fun deleteItem(collegeId: String, itemId: String, imagePath: String?): Result<Unit>
    suspend fun markAsResolved(collegeId: String, itemId: String): Result<Unit>
    suspend fun uploadImage(collegeId: String, itemId: String, imageUri: Uri): Result<Pair<String, String>>
    suspend fun deleteFile(path: String): Result<Unit>
    fun generateItemId(): String
}
