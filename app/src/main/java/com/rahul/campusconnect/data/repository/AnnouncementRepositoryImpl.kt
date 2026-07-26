package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.AnnouncementRemoteDataSource
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.domain.repository.AnnouncementRepository
import javax.inject.Inject

class AnnouncementRepositoryImpl @Inject constructor(
    private val remoteDataSource: AnnouncementRemoteDataSource,
    private val sessionManager: SessionManager
) : AnnouncementRepository {

    private fun getCollegeId(): String {
        return sessionManager.getCollegeId() ?: throw IllegalStateException("College ID not found in session")
    }

    override suspend fun getAnnouncements(): Result<List<Announcement>> {
        return remoteDataSource.getAnnouncements(getCollegeId())
    }

    override suspend fun getAnnouncementById(announcementId: String): Result<Announcement?> {
        return remoteDataSource.getAnnouncementById(getCollegeId(), announcementId)
    }

    override suspend fun createAnnouncement(announcement: Announcement): Result<String> {
        return remoteDataSource.createAnnouncement(getCollegeId(), announcement)
    }

    override suspend fun updateAnnouncement(announcement: Announcement): Result<Unit> {
        return remoteDataSource.updateAnnouncement(getCollegeId(), announcement)
    }

    override suspend fun deleteAnnouncement(announcementId: String): Result<Unit> {
        return remoteDataSource.deleteAnnouncement(getCollegeId(), announcementId)
    }

    override suspend fun getMyAnnouncements(userId: String): Result<List<Announcement>> {
        return remoteDataSource.getMyAnnouncements(getCollegeId(), userId)
    }

    override suspend fun uploadAnnouncementImage(
        announcementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> {
        return remoteDataSource.uploadAnnouncementImage(getCollegeId(), announcementId, imageUri)
    }

    override suspend fun uploadAnnouncementAttachment(
        announcementId: String,
        attachmentUri: Uri
    ): Result<Pair<String, String>> {
        return remoteDataSource.uploadAnnouncementAttachment(getCollegeId(), announcementId, attachmentUri)
    }

    override suspend fun deleteFile(path: String): Result<Unit> {
        return remoteDataSource.deleteFile(path)
    }
}
