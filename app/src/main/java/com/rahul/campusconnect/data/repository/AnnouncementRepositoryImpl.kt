package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.data.remote.AnnouncementRemoteDataSource
import com.rahul.campusconnect.domain.model.Announcement
import com.rahul.campusconnect.domain.repository.AnnouncementRepository
import javax.inject.Inject

class AnnouncementRepositoryImpl @Inject constructor(
    private val remoteDataSource: AnnouncementRemoteDataSource
) : AnnouncementRepository {

    override suspend fun getAnnouncements(): Result<List<Announcement>> {
        return remoteDataSource.getAnnouncements()
    }

    override suspend fun getAnnouncementById(announcementId: String): Result<Announcement?> {
        return remoteDataSource.getAnnouncementById(announcementId)
    }

    override suspend fun createAnnouncement(announcement: Announcement): Result<String> {
        return remoteDataSource.createAnnouncement(announcement)
    }

    override suspend fun updateAnnouncement(announcement: Announcement): Result<Unit> {
        return remoteDataSource.updateAnnouncement(announcement)
    }

    override suspend fun deleteAnnouncement(announcementId: String): Result<Unit> {
        return remoteDataSource.deleteAnnouncement(announcementId)
    }

    override suspend fun getMyAnnouncements(userId: String): Result<List<Announcement>> {
        return remoteDataSource.getMyAnnouncements(userId)
    }

    override suspend fun uploadAnnouncementImage(
        announcementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> {
        return remoteDataSource.uploadAnnouncementImage(announcementId, imageUri)
    }

    override suspend fun uploadAnnouncementAttachment(
        announcementId: String,
        attachmentUri: Uri
    ): Result<Pair<String, String>> {
        return remoteDataSource.uploadAnnouncementAttachment(announcementId, attachmentUri)
    }

    override suspend fun deleteFile(path: String): Result<Unit> {
        return remoteDataSource.deleteFile(path)
    }
}
