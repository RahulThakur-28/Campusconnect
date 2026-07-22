package com.rahul.campusconnect.data.repository

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
}