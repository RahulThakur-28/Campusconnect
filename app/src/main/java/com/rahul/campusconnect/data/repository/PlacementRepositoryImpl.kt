package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.data.remote.PlacementRemoteDataSource
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.repository.PlacementRepository
import javax.inject.Inject

class PlacementRepositoryImpl @Inject constructor(
    private val remoteDataSource: PlacementRemoteDataSource
) : PlacementRepository {

    override suspend fun getPlacements(): Result<List<Placement>> {
        return remoteDataSource.getPlacements()
    }

    override suspend fun getPlacementById(
        placementId: String
    ): Result<Placement?> {
        return remoteDataSource.getPlacementById(placementId)
    }

    override suspend fun createPlacement(
        placement: Placement
    ): Result<String> {
        return remoteDataSource.createPlacement(placement)
    }

    override suspend fun updatePlacement(
        placement: Placement
    ): Result<Unit> {
        return remoteDataSource.updatePlacement(placement)
    }

    override suspend fun deletePlacement(
        placementId: String
    ): Result<Unit> {
        return remoteDataSource.deletePlacement(placementId)
    }

    override suspend fun getPlacementsByCategory(
        category: String
    ): Result<List<Placement>> {
        return remoteDataSource.getPlacementsByCategory(category)
    }

    override suspend fun searchPlacements(
        query: String
    ): Result<List<Placement>> {
        return remoteDataSource.searchPlacements(query)
    }

    override suspend fun getMyPlacements(
        userId: String
    ): Result<List<Placement>> {
        return remoteDataSource.getMyPlacements(userId)
    }

    override suspend fun generatePlacementId(): String {
        return remoteDataSource.generatePlacementId()
    }

    override suspend fun uploadPlacementLogo(
        placementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> {
        return remoteDataSource.uploadPlacementLogo(placementId, imageUri)
    }

    override suspend fun uploadPlacementAttachment(
        placementId: String,
        fileUri: Uri,
        extension: String
    ): Result<Pair<String, String>> {
        return remoteDataSource.uploadPlacementAttachment(placementId, fileUri, extension)
    }

    override suspend fun deleteFile(path: String): Result<Unit> {
        return remoteDataSource.deleteFile(path)
    }
}
