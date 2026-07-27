package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.PlacementRemoteDataSource
import com.rahul.campusconnect.domain.model.Placement
import com.rahul.campusconnect.domain.model.Notification
import com.rahul.campusconnect.domain.model.NotificationType
import com.rahul.campusconnect.domain.repository.NotificationRepository
import com.rahul.campusconnect.domain.repository.PlacementRepository
import javax.inject.Inject

class PlacementRepositoryImpl @Inject constructor(
    private val remoteDataSource: PlacementRemoteDataSource,
    private val sessionManager: SessionManager,
    private val notificationRepository: NotificationRepository
) : PlacementRepository {

    private fun getCollegeId(): String {
        return sessionManager.getCollegeId() ?: throw IllegalStateException("College ID not found in session")
    }

    override suspend fun getPlacements(): Result<List<Placement>> = remoteDataSource.getPlacements(getCollegeId())

    override suspend fun getPlacementById(placementId: String): Result<Placement?> = 
        remoteDataSource.getPlacementById(getCollegeId(), placementId)

    override suspend fun createPlacement(placement: Placement): Result<String> {
        val result = remoteDataSource.createPlacement(getCollegeId(), placement)
        result.onSuccess { id ->
            notificationRepository.sendNotification(
                Notification(
                    userId = "ALL",
                    title = "New Placement: ${placement.companyName}",
                    message = "Role: ${placement.jobRole} for ${placement.batch} batch",
                    type = NotificationType.PLACEMENT,
                    relatedId = id,
                    collegeId = placement.collegeId
                )
            )
        }
        return result
    }

    override suspend fun updatePlacement(placement: Placement): Result<Unit> = 
        remoteDataSource.updatePlacement(getCollegeId(), placement)

    override suspend fun deletePlacement(placementId: String): Result<Unit> = 
        remoteDataSource.deletePlacement(getCollegeId(), placementId)

    override suspend fun getPlacementsByCategory(category: String): Result<List<Placement>> = 
        remoteDataSource.getPlacementsByCategory(getCollegeId(), category)

    override suspend fun searchPlacements(query: String): Result<List<Placement>> = 
        remoteDataSource.searchPlacements(getCollegeId(), query)

    override suspend fun getMyPlacements(userId: String): Result<List<Placement>> = 
        remoteDataSource.getMyPlacements(getCollegeId(), userId)

    override suspend fun generatePlacementId(): String = remoteDataSource.generatePlacementId()

    override suspend fun uploadPlacementLogo(placementId: String, imageUri: Uri): Result<Pair<String, String>> = 
        remoteDataSource.uploadPlacementLogo(getCollegeId(), placementId, imageUri)

    override suspend fun uploadPlacementAttachment(placementId: String, fileUri: Uri, extension: String): Result<Pair<String, String>> = 
        remoteDataSource.uploadPlacementAttachment(getCollegeId(), placementId, fileUri, extension)

    override suspend fun deleteFile(path: String): Result<Unit> = remoteDataSource.deleteFile(path)
}
