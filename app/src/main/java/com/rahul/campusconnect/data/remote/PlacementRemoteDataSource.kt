package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.Placement

interface PlacementRemoteDataSource {

    suspend fun getPlacements(collegeId: String): Result<List<Placement>>

    suspend fun getPlacementById(
        collegeId: String,
        placementId: String
    ): Result<Placement?>

    suspend fun createPlacement(
        collegeId: String,
        placement: Placement
    ): Result<String>

    suspend fun updatePlacement(
        collegeId: String,
        placement: Placement
    ): Result<Unit>

    fun generatePlacementId(): String

    suspend fun deletePlacement(
        collegeId: String,
        placementId: String
    ): Result<Unit>

    suspend fun getPlacementsByCategory(
        collegeId: String,
        category: String
    ): Result<List<Placement>>

    suspend fun searchPlacements(
        collegeId: String,
        query: String
    ): Result<List<Placement>>

    suspend fun getMyPlacements(
        collegeId: String,
        userId: String
    ): Result<List<Placement>>

    suspend fun uploadPlacementLogo(
        collegeId: String,
        placementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>>

    suspend fun uploadPlacementAttachment(
        collegeId: String,
        placementId: String,
        fileUri: Uri,
        extension: String
    ): Result<Pair<String, String>>

    suspend fun deleteFile(path: String): Result<Unit>
}
