package com.rahul.campusconnect.model

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: UserRole = UserRole.STUDENT,
    val department: String = "",
    val semester: String = "",
    val studentId: String = "",
    val profilePictureUrl: String? = null,
    val phoneNumber: String = "",
    val bio: String = "",
    val isVerified: Boolean = false,
    val stats: ProfileStats = ProfileStats()
)

data class ProfileStats(
    val notesUploaded: Int = 0,
    val eventsJoined: Int = 0,
    val placementApplications: Int = 0,
    val lostFoundReports: Int = 0
)
