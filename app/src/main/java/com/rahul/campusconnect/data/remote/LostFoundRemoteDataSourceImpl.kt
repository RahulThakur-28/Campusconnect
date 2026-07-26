package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.LostFoundItem
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class LostFoundRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageManager: StorageManager
) : LostFoundRemoteDataSource {

    private suspend fun getCollegeId(): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in.")

        val snapshot = firestore
            .collection(Constants.USERS)
            .document(uid)
            .get()
            .await()

        return snapshot.getString("collegeId")
            ?: throw IllegalStateException("College ID not found.")
    }

    private suspend fun lostFoundCollection(): CollectionReference {
        return firestore.collection(Constants.COLLEGES)
            .document(getCollegeId())
            .collection(Constants.LOST_FOUND)
    }

    override suspend fun getItems(): Result<List<LostFoundItem>> = try {
        Log.d("LOST_FOUND_QUERY", "Fetching lost & found items...")
        val snapshot = lostFoundCollection()
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val items = snapshot.documents.mapNotNull {
            it.toObject(LostFoundItem::class.java)?.copy(id = it.id)
        }
        Result.success(items)
    } catch (e: Exception) {
        Log.e("LOST_FOUND_QUERY", "Error fetching items", e)
        Result.failure(e)
    }

    override suspend fun getItemById(itemId: String): Result<LostFoundItem?> = try {
        val document = lostFoundCollection().document(itemId).get().await()
        val item = document.toObject(LostFoundItem::class.java)?.copy(id = document.id)
        Result.success(item)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createItem(item: LostFoundItem): Result<String> = try {
        val document = lostFoundCollection().document(item.id.ifBlank { generateItemId() })
        val currentTime = System.currentTimeMillis()
        val collegeId = getCollegeId()
        
        val finalItem = item.copy(
            id = document.id,
            collegeId = collegeId,
            createdAt = currentTime,
            updatedAt = currentTime
        )
        document.set(finalItem).await()
        Log.d("LOST_FOUND_CREATE", "Item created: ${document.id}")
        Result.success(document.id)
    } catch (e: Exception) {
        Log.e("LOST_FOUND_CREATE", "Error creating item", e)
        Result.failure(e)
    }

    override suspend fun updateItem(item: LostFoundItem): Result<Unit> = try {
        val updatedItem = item.copy(updatedAt = System.currentTimeMillis())
        lostFoundCollection().document(item.id).set(updatedItem).await()
        Log.d("LOST_FOUND_UPDATE", "Item updated: ${item.id}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("LOST_FOUND_UPDATE", "Error updating item", e)
        Result.failure(e)
    }

    override suspend fun deleteItem(itemId: String, imagePath: String?): Result<Unit> = try {
        // Delete image if exists
        imagePath?.let { path ->
            storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
            Log.d("LOST_FOUND_DELETE", "Deleted image: $path")
        }
        // Permanent delete Firestore document
        lostFoundCollection().document(itemId).delete().await()
        Log.d("LOST_FOUND_DELETE", "Deleted document: $itemId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("LOST_FOUND_DELETE", "Error deleting item", e)
        Result.failure(e)
    }

    override suspend fun markAsResolved(itemId: String): Result<Unit> = try {
        lostFoundCollection().document(itemId).update(
            mapOf(
                "status" to "RESOLVED",
                "resolvedAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
        Log.d("LOST_FOUND_RESOLVE", "Item marked as resolved: $itemId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("LOST_FOUND_RESOLVE", "Error resolving item", e)
        Result.failure(e)
    }

    override suspend fun uploadImage(itemId: String, imageUri: Uri): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.lostFoundImage(getCollegeId(), itemId)
        storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = imageUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Log.e("LOST_FOUND_UPLOAD", "Error uploading image", e)
        Result.failure(e)
    }

    override suspend fun deleteFile(path: String): Result<Unit> = try {
        storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun generateItemId(): String = UUID.randomUUID().toString()
}
