package com.rahul.campusconnect.domain.model

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String? = null,
    val imageStoragePath: String? = null,
    val organizerId: String = "",
    val organizerName: String = "",
    val organizerRole: String = "",
    val venue: String = "",
    val category: String = "General",
    val collegeId: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val time: String = "",
    val maxParticipants: Int = 0,
    val registeredCount: Int = 0,
    val isFeatured: Boolean = false,
    val isRegistrationOpen: Boolean = true,
    val registrationLink: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)
