package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.Placement

interface PlacementRepository {

    suspend fun getPlacements(): Result<List<Placement>>

    suspend fun generatePlacementId(): String

    suspend fun getPlacementById(
        placementId: String
    ): Result<Placement?>

    suspend fun createPlacement(
        placement: Placement
    ): Result<String>

    suspend fun updatePlacement(
        placement: Placement
    ): Result<Unit>

    suspend fun deletePlacement(
        placementId: String
    ): Result<Unit>

    suspend fun getPlacementsByCategory(
        category: String
    ): Result<List<Placement>>

    suspend fun searchPlacements(
        query: String
    ): Result<List<Placement>>

    suspend fun getMyPlacements(
        userId: String
    ): Result<List<Placement>>

    suspend fun uploadPlacementLogo(
        placementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>>

    suspend fun uploadPlacementAttachment(
        placementId: String,
        fileUri: Uri,
        extension: String
    ): Result<Pair<String, String>>

    suspend fun deleteFile(path: String): Result<Unit>
}
