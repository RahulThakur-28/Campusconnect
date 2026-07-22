package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.domain.model.Placement

interface PlacementRepository {

    suspend fun getPlacements(): Result<List<Placement>>

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
}