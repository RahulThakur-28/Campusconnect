package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.Announcement

interface AnnouncementRemoteDataSource {

    suspend fun getAnnouncements(): Result<List<Announcement>>

    suspend fun getAnnouncementById(
        announcementId: String
    ): Result<Announcement?>

    suspend fun createAnnouncement(
        announcement: Announcement
    ): Result<String>

    suspend fun updateAnnouncement(
        announcement: Announcement
    ): Result<Unit>

    suspend fun deleteAnnouncement(
        announcementId: String
    ): Result<Unit>

    suspend fun getMyAnnouncements(
        userId: String
    ): Result<List<Announcement>>

    fun generateAnnouncementId(): String

    suspend fun uploadAnnouncementImage(
        announcementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> // URL to Path

    suspend fun uploadAnnouncementAttachment(
        announcementId: String,
        attachmentUri: Uri
    ): Result<Pair<String, String>> // URL to Path

    suspend fun deleteFile(path: String): Result<Unit>
}
