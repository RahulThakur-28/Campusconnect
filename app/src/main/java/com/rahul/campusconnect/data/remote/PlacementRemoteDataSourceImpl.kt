package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Placement
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlacementRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider,
    private val storageManager: StorageManager
) : PlacementRemoteDataSource {

    override suspend fun getPlacements(collegeId: String): Result<List<Placement>> = try {
        val snapshot = pathProvider.placements(collegeId)
            .whereEqualTo(Constants.DELETED, false)
            .orderBy("postedAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val placements = snapshot.documents.mapNotNull { it.toObject(Placement::class.java)?.copy(id = it.id) }
        Result.success(placements)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPlacementById(collegeId: String, placementId: String): Result<Placement?> = try {
        val document = pathProvider.placements(collegeId).document(placementId).get().await()
        val placement = document.toObject(Placement::class.java)?.copy(id = document.id)
        if (placement?.deleted == true) Result.success(null) else Result.success(placement)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createPlacement(collegeId: String, placement: Placement): Result<String> = try {
        val collection = pathProvider.placements(collegeId)
        val document = if (placement.id.isBlank()) collection.document() else collection.document(placement.id)
        val currentTime = System.currentTimeMillis()
        val finalPlacement = placement.copy(
            id = document.id,
            collegeId = collegeId,
            postedAt = currentTime,
            updatedAt = currentTime,
            deleted = false
        )
        document.set(finalPlacement).await()
        Result.success(document.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updatePlacement(collegeId: String, placement: Placement): Result<Unit> = try {
        val updatedPlacement = placement.copy(updatedAt = System.currentTimeMillis())
        pathProvider.placements(collegeId).document(updatedPlacement.id).set(updatedPlacement).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun generatePlacementId(): String = java.util.UUID.randomUUID().toString()

    override suspend fun deletePlacement(collegeId: String, placementId: String): Result<Unit> = try {
        pathProvider.placements(collegeId).document(placementId).update(
            mapOf(
                Constants.DELETED to true,
                Constants.UPDATED_AT to System.currentTimeMillis()
            )
        ).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getPlacementsByCategory(collegeId: String, category: String): Result<List<Placement>> = try {
        val snapshot = pathProvider.placements(collegeId)
            .whereEqualTo(Constants.DELETED, false)
            .whereEqualTo("category", category)
            .orderBy("postedAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val placements = snapshot.documents.mapNotNull { it.toObject(Placement::class.java)?.copy(id = it.id) }
        Result.success(placements)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun searchPlacements(collegeId: String, query: String): Result<List<Placement>> = try {
        val snapshot = pathProvider.placements(collegeId)
            .whereEqualTo(Constants.DELETED, false)
            .orderBy("companyName")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
        val placements = snapshot.documents.mapNotNull { it.toObject(Placement::class.java)?.copy(id = it.id) }
        Result.success(placements)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getMyPlacements(collegeId: String, userId: String): Result<List<Placement>> = try {
        val snapshot = pathProvider.placements(collegeId)
            .whereEqualTo(Constants.DELETED, false)
            .whereEqualTo("createdBy", userId)
            .orderBy("postedAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val placements = snapshot.documents.mapNotNull { it.toObject(Placement::class.java)?.copy(id = it.id) }
        Result.success(placements)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadPlacementLogo(collegeId: String, placementId: String, imageUri: Uri): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.placementLogo(collegeId, placementId)
        storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = imageUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadPlacementAttachment(collegeId: String, placementId: String, fileUri: Uri, extension: String): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.placementAttachment(collegeId, placementId, extension)
        storageManager.uploadPdf(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            pdfUri = fileUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteFile(path: String): Result<Unit> = storageManager.deleteFile(
        bucket = StorageConstants.MEDIA_BUCKET,
        path = path
    )
}
