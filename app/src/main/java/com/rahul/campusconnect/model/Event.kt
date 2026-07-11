package com.rahul.campusconnect.model


data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val bannerUrl: String = "",
    val date: String = "",
    val time: String = "",
    val venue: String = "",
    val organizer: String = "",
    val registeredCount: Int = 0,
    val registrationLink: String = "",
    val isRegistrationOpen: Boolean = false
)