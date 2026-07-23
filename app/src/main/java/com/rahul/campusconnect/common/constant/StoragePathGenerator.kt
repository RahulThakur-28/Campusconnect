package com.rahul.campusconnect.common.storage

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
}