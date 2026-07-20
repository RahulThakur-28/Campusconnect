package com.rahul.campusconnect.domain.model

data class User(

// Basic Info
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val profileImage: String = "",
    val phoneNumber: String = "",
    val bio: String = "",

// College Info
    val collegeId: String = "",
    val studentId: String = "",
    val branch: String = "",
    val year: String = "",
    val section: String = "",

// Permissions
    val role: String = "STUDENT",
    val verificationStatus: String = "PENDING",

// Status
    val isEmailVerified: Boolean = false,
    val isProfileCompleted: Boolean = false,

// Metadata
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false

)