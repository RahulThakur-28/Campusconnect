package com.rahul.campusconnect.domain.model

data class LostFoundItem(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val type: String = "LOST", // LOST or FOUND
    val status: String = "ACTIVE", // ACTIVE or RESOLVED
    val ownerId: String = "",
    val ownerName: String = "",
    val ownerRole: String = "",
    val contactEmail: String = "",
    val contactPhone: String? = null,
    val collegeId: String = "",
    val location: String = "",
    val imageUrl: String? = null,
    val imageStoragePath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)
