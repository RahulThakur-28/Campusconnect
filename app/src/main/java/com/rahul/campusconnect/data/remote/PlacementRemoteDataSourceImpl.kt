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

            val placementWithId = placement.copy(
                id = document.id
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

            placementsRef
                .document(placement.id)
                .set(placement)
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
                .update("isDeleted", true)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}