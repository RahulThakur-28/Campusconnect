package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.Announcement

interface AnnouncementRepository {

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

    suspend fun uploadAnnouncementImage(
        announcementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>>

    suspend fun uploadAnnouncementImage(
        collegeId: String,
        announcementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>>

    suspend fun uploadAnnouncementAttachment(
        announcementId: String,
        attachmentUri: Uri
    ): Result<Pair<String, String>>

    suspend fun uploadAnnouncementAttachment(
        collegeId: String,
        announcementId: String,
        attachmentUri: Uri
    ): Result<Pair<String, String>>

    suspend fun deleteFile(path: String): Result<Unit>
}
