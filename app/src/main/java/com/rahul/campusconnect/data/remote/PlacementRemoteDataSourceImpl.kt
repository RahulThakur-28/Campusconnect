package com.rahul.campusconnect.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.domain.model.Placement
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PlacementRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : PlacementRemoteDataSource {

    companion object {
        private const val PLACEMENTS_COLLECTION = "placements"
    }

    private val placementsRef
        get() = firestore.collection(PLACEMENTS_COLLECTION)

    override suspend fun getPlacements(): Result<List<Placement>> {
        return try {

            val snapshot = placementsRef
                .whereEqualTo("isDeleted", false)
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

    override suspend fun getPlacementById(
        placementId: String
    ): Result<Placement?> {

        return try {

            val document = placementsRef
                .document(placementId)
                .get()
                .await()

            val placement = document
                .toObject(Placement::class.java)
                ?.copy(id = document.id)

            Result.success(placement)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPlacement(
        placement: Placement
    ): Result<String> {

        return try {

            val document = placementsRef.document()
            val currentTime = System.currentTimeMillis()

            val placementWithId = placement.copy(
                id = document.id,
                postedAt = currentTime,
                updatedAt = currentTime,
                isDeleted = false
            )

            document.set(placementWithId).await()

            Result.success(document.id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePlacement(
        placement: Placement
    ): Result<Unit> {

        return try {

            val updatedPlacement = placement.copy(
                updatedAt = System.currentTimeMillis()
            )

            placementsRef
                .document(updatedPlacement.id)
                .set(updatedPlacement)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePlacement(
        placementId: String
    ): Result<Unit> {

        return try {

            placementsRef
                .document(placementId)
                .update(
                    mapOf(
                        "isDeleted" to true,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlacementsByCategory(
        category: String
    ): Result<List<Placement>> {

        return try {

            val snapshot = placementsRef
                .whereEqualTo("isDeleted", false)
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
        return firestore
            .collection("placements")
            .document()
            .id
    }

    override suspend fun searchPlacements(
        query: String
    ): Result<List<Placement>> {

        return try {

            val snapshot = placementsRef
                .whereEqualTo("isDeleted", false)
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

    override suspend fun getMyPlacements(
        userId: String
    ): Result<List<Placement>> {

        return try {

            val snapshot = placementsRef
                .whereEqualTo("isDeleted", false)
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
}