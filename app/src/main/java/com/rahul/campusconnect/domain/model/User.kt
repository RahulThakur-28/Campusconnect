package com.rahul.campusconnect.domain.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val collegeId: String = "",
    val collegeName: String = "",
    val enrollmentNumber: String = "",
    val department: String = "",
    val academicYear: String = "",
    val section: String? = null,
    val role: UserRole = UserRole.STUDENT,
    val verificationStatus: String = "PENDING",
    val profileImage: String = "",
    val phone: String = "",
    val bio: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deleted: Boolean = false
)
