package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Placement
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlacementRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageManager: StorageManager
) : PlacementRemoteDataSource {

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

    private suspend fun placementsCollection(): CollectionReference {
        return firestore.collection(Constants.COLLEGES)
            .document(getCollegeId())
            .collection(Constants.PLACEMENTS)
    }

    override suspend fun getPlacements(): Result<List<Placement>> {
        return try {
            Log.d("PLACEMENT_QUERY", "Fetching placements...")

            val snapshot = placementsCollection()
                .whereEqualTo(Constants.DELETED, false)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("PLACEMENT_QUERY", "Documents found = ${snapshot.size()}")

            val placements = snapshot.documents.mapNotNull {
                it.toObject(Placement::class.java)?.copy(id = it.id)
            }

            Result.success(placements)

        } catch (e: FirebaseFirestoreException) {
            Log.e("PLACEMENT_QUERY", "Firestore Exception", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("PLACEMENT_QUERY", "General Exception", e)
            Result.failure(e)
        }
    }

    override suspend fun getPlacementById(placementId: String): Result<Placement?> {
        return try {
            val document = placementsCollection()
                .document(placementId)
                .get()
                .await()

            val placement = document.toObject(Placement::class.java)?.copy(id = document.id)
            
            if (placement?.deleted == true) {
                Result.success(null)
            } else {
                Result.success(placement)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPlacement(placement: Placement): Result<String> {
        return try {
            val collection = placementsCollection()
            val document = if (placement.id.isBlank()) collection.document() else collection.document(placement.id)
            val currentTime = System.currentTimeMillis()
            val collegeId = getCollegeId()

            val finalPlacement = placement.copy(
                id = document.id,
                collegeId = collegeId,
                postedAt = currentTime,
                updatedAt = currentTime,
                deleted = false
            )

            document.set(finalPlacement).await()
            Log.d("PLACEMENT_CREATE", "Placement created: ${document.id}")
            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlacement(placement: Placement): Result<Unit> {
        return try {
            val updatedPlacement = placement.copy(
                updatedAt = System.currentTimeMillis()
            )

            placementsCollection()
                .document(updatedPlacement.id)
                .set(updatedPlacement)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePlacement(placementId: String): Result<Unit> {
        return try {
            placementsCollection()
                .document(placementId)
                .update(
                    mapOf(
                        Constants.DELETED to true,
                        Constants.UPDATED_AT to System.currentTimeMillis()
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlacementsByCategory(category: String): Result<List<Placement>> {
        return try {
            val snapshot = placementsCollection()
                .whereEqualTo(Constants.DELETED, false)
                .whereEqualTo("category", category)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val placements = snapshot.documents.mapNotNull {
                it.toObject(Placement::class.java)?.copy(id = it.id)
            }

            Result.success(placements)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun generatePlacementId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    override suspend fun searchPlacements(query: String): Result<List<Placement>> {
        return try {
            val snapshot = placementsCollection()
                .whereEqualTo(Constants.DELETED, false)
                .orderBy("companyName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()

            val placements = snapshot.documents.mapNotNull {
                it.toObject(Placement::class.java)?.copy(id = it.id)
            }

            Result.success(placements)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMyPlacements(userId: String): Result<List<Placement>> {
        return try {
            val snapshot = placementsCollection()
                .whereEqualTo(Constants.DELETED, false)
                .whereEqualTo("createdBy", userId)
                .orderBy("postedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val placements = snapshot.documents.mapNotNull {
                it.toObject(Placement::class.java)?.copy(id = it.id)
            }

            Result.success(placements)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPlacementLogo(
        placementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> {
        return try {
            val path = StoragePathGenerator.placementLogo(getCollegeId(), placementId)
            storageManager.uploadImage(
                bucket = StorageConstants.MEDIA_BUCKET,
                path = path,
                imageUri = imageUri
            ).map { url -> Pair(url, path) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPlacementAttachment(
        placementId: String,
        fileUri: Uri,
        extension: String
    ): Result<Pair<String, String>> {
        return try {
            val path = StoragePathGenerator.placementAttachment(getCollegeId(), placementId, extension)
            storageManager.uploadPdf(
                bucket = StorageConstants.MEDIA_BUCKET,
                path = path,
                pdfUri = fileUri
            ).map { url -> Pair(url, path) }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFile(path: String): Result<Unit> {
        return storageManager.deleteFile(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path
        )
    }
}
