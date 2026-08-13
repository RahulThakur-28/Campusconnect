package com.rahul.campusconnect.common.storage

import com.rahul.campusconnect.common.constant.StorageConstants
import java.util.UUID

object StoragePathGenerator {

    fun placementLogo(
        collegeId: String,
        placementId: String
    ): String {
        val uniqueFileId = UUID.randomUUID()
        return "colleges/$collegeId/${StorageConstants.Folder.PLACEMENTS}/${StorageConstants.Folder.LOGOS}/$placementId/$uniqueFileId.jpg"
    }

    fun placementAttachment(
        collegeId: String,
        placementId: String,
        extension: String
    ): String {
        val uniqueFileId = UUID.randomUUID()
        return "colleges/$collegeId/${StorageConstants.Folder.PLACEMENTS}/${StorageConstants.Folder.DOCUMENTS}/$placementId/$uniqueFileId.$extension"
    }


    fun eventBanner(
        collegeId: String,
        eventId: String
    ): String {

        val uniqueFileId = UUID.randomUUID()

        return buildString {

            append("colleges/")
            append(collegeId)
            append("/")
            append(StorageConstants.Folder.EVENTS)
            append("/")
            append(StorageConstants.Folder.BANNERS)
            append("/")
            append(eventId)
            append("/")
            append(uniqueFileId)
            append(".jpg")
        }
    }

    fun profileImage(
        collegeId: String,
        userId: String
    ): String {
        val uniqueFileId = UUID.randomUUID()
        return "colleges/$collegeId/${StorageConstants.Folder.PROFILE}/$userId/$uniqueFileId.jpg"
    }

    fun announcementBanner(
        collegeId: String,
        announcementId: String
    ): String {
        val uniqueFileId = UUID.randomUUID()
        return "colleges/$collegeId/${StorageConstants.Folder.ANNOUNCEMENTS}/${StorageConstants.Folder.BANNERS}/$announcementId/$uniqueFileId.jpg"
    }

    fun announcementAttachment(
        collegeId: String,
        announcementId: String,
        extension: String
    ): String {
        val uniqueFileId = UUID.randomUUID()
        return "colleges/$collegeId/${StorageConstants.Folder.ANNOUNCEMENTS}/${StorageConstants.Folder.DOCUMENTS}/$announcementId/$uniqueFileId.$extension"
    }

    fun noteFile(
        collegeId: String,
        noteId: String,
        extension: String
    ): String {
        val uniqueFileId = UUID.randomUUID()
        return "colleges/$collegeId/${StorageConstants.Folder.NOTES}/${StorageConstants.Folder.DOCUMENTS}/$noteId/$uniqueFileId.$extension"
    }

    fun noteThumbnail(
        collegeId: String,
        noteId: String
    ): String {
        val uniqueFileId = UUID.randomUUID()
        return "colleges/$collegeId/${StorageConstants.Folder.NOTES}/${StorageConstants.Folder.BANNERS}/$noteId/$uniqueFileId.jpg"
    }

    fun lostFoundImage(
        collegeId: String,
        itemId: String
    ): String {
        val uniqueFileId = UUID.randomUUID()
        return "colleges/$collegeId/${StorageConstants.Folder.LOST_FOUND}/$itemId/$uniqueFileId.jpg"
    }

    fun verificationDocument(
        collegeId: String,
        userId: String
    ): String {
        return "colleges/$collegeId/verification/$userId.jpg"
    }
}