package com.rahul.campusconnect.common.storage

import com.rahul.campusconnect.common.constant.StorageConstants

object StoragePathGenerator {

    fun placementLogo(
        collegeId: String,
        placementId: String
    ): String {

        return buildString {

            append("colleges/")
            append(collegeId)
            append("/")
            append(StorageConstants.Folder.PLACEMENTS)
            append("/")
            append(StorageConstants.Folder.LOGOS)
            append("/")
            append(placementId)
            append(".jpg")

        }
    }

    fun placementAttachment(
        collegeId: String,
        placementId: String,
        extension: String
    ): String {
        return "colleges/$collegeId/${StorageConstants.Folder.PLACEMENTS}/${StorageConstants.Folder.DOCUMENTS}/$placementId.$extension"
    }

    fun eventBanner(
        collegeId: String,
        eventId: String
    ): String {

        return buildString {

            append("colleges/")
            append(collegeId)
            append("/")
            append(StorageConstants.Folder.EVENTS)
            append("/")
            append(StorageConstants.Folder.BANNERS)
            append("/")
            append(eventId)
            append(".jpg")

        }
    }

    fun profileImage(
        collegeId: String,
        userId: String
    ): String {

        return buildString {

            append("colleges/")
            append(collegeId)
            append("/")
            append(StorageConstants.Folder.PROFILE)
            append("/")
            append(userId)
            append(".jpg")

        }
    }

    fun announcementBanner(
        collegeId: String,
        announcementId: String
    ): String {
        return "colleges/$collegeId/${StorageConstants.Folder.ANNOUNCEMENTS}/${StorageConstants.Folder.BANNERS}/$announcementId.jpg"
    }

    fun announcementAttachment(
        collegeId: String,
        announcementId: String,
        extension: String
    ): String {
        return "colleges/$collegeId/${StorageConstants.Folder.ANNOUNCEMENTS}/${StorageConstants.Folder.DOCUMENTS}/$announcementId.$extension"
    }

    fun noteFile(
        collegeId: String,
        noteId: String,
        extension: String
    ): String {
        return "colleges/$collegeId/${StorageConstants.Folder.NOTES}/${StorageConstants.Folder.DOCUMENTS}/$noteId.$extension"
    }

    fun noteThumbnail(
        collegeId: String,
        noteId: String
    ): String {
        return "colleges/$collegeId/${StorageConstants.Folder.NOTES}/${StorageConstants.Folder.BANNERS}/$noteId.jpg"
    }

    fun lostFoundImage(
        collegeId: String,
        itemId: String
    ): String {
        return "colleges/$collegeId/${StorageConstants.Folder.LOST_FOUND}/$itemId.jpg"
    }
}
