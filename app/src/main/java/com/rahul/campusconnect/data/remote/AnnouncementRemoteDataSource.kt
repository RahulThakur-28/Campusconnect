package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.Announcement

interface AnnouncementRemoteDataSource {

    suspend fun getAnnouncements(collegeId: String): Result<List<Announcement>>

    suspend fun getAnnouncementById(
        collegeId: String,
        announcementId: String
    ): Result<Announcement?>

    suspend fun createAnnouncement(
        collegeId: String,
        announcement: Announcement
    ): Result<String>

    suspend fun updateAnnouncement(
        collegeId: String,
        announcement: Announcement
    ): Result<Unit>

    suspend fun deleteAnnouncement(
        collegeId: String,
        announcementId: String
    ): Result<Unit>

    suspend fun getMyAnnouncements(
        collegeId: String,
        userId: String
    ): Result<List<Announcement>>

    fun generateAnnouncementId(): String

    suspend fun uploadAnnouncementImage(
        collegeId: String,
        announcementId: String,
        imageUri: Uri
    ): Result<Pair<String, String>> // URL to Path

    suspend fun uploadAnnouncementAttachment(
        collegeId: String,
        announcementId: String,
        attachmentUri: Uri
    ): Result<Pair<String, String>> // URL to Path

    suspend fun deleteFile(path: String): Result<Unit>
}
