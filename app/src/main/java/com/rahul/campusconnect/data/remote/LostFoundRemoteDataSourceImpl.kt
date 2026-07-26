package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.LostFoundItem
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class LostFoundRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider,
    private val storageManager: StorageManager
) : LostFoundRemoteDataSource {

    override suspend fun getItems(collegeId: String): Result<List<LostFoundItem>> = try {
        val snapshot = pathProvider.lostFound(collegeId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val items = snapshot.documents.mapNotNull { it.toObject(LostFoundItem::class.java)?.copy(id = it.id) }
        Result.success(items)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getItemById(collegeId: String, itemId: String): Result<LostFoundItem?> = try {
        val document = pathProvider.lostFound(collegeId).document(itemId).get().await()
        val item = document.toObject(LostFoundItem::class.java)?.copy(id = document.id)
        Result.success(item)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createItem(collegeId: String, item: LostFoundItem): Result<String> = try {
        val document = pathProvider.lostFound(collegeId).document(item.id.ifBlank { generateItemId() })
        val finalItem = item.copy(id = document.id, collegeId = collegeId)
        document.set(finalItem).await()
        Result.success(document.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateItem(collegeId: String, item: LostFoundItem): Result<Unit> = try {
        pathProvider.lostFound(collegeId).document(item.id).set(item.copy(updatedAt = System.currentTimeMillis())).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteItem(collegeId: String, itemId: String, imagePath: String?): Result<Unit> = try {
        imagePath?.let { storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, it) }
        pathProvider.lostFound(collegeId).document(itemId).delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun markAsResolved(collegeId: String, itemId: String): Result<Unit> = try {
        pathProvider.lostFound(collegeId).document(itemId).update(
            mapOf("status" to "RESOLVED", "resolvedAt" to System.currentTimeMillis(), "updatedAt" to System.currentTimeMillis())
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadImage(collegeId: String, itemId: String, imageUri: Uri): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.lostFoundImage(collegeId, itemId)
        storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = imageUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteFile(path: String): Result<Unit> = storageManager.deleteFile(
        bucket = StorageConstants.MEDIA_BUCKET,
        path = path
    )

    override fun generateItemId(): String = UUID.randomUUID().toString()
}
