package com.rahul.campusconnect.domain.model

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val organizerId: String = "",
    val organizerName: String = "",
    val organizerRole: String = "",
    val venue: String = "",
    val category: String = "",
    val startDate: Long = 0L,
    val endDate: Long = 0L,
    val date: String = "", // Keeping for UI compatibility if needed, but Long preferred
    val time: String = "", // Keeping for UI compatibility
    val maxParticipants: Int = 0,
    val registeredCount: Int = 0,
    val isFeatured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val isRegistrationOpen: Boolean = true,
    val registrationLink: String = ""
)
