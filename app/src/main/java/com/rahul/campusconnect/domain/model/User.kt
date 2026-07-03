package com.rahul.campusconnect.domain.model

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val collegeId: String = "",
    val branch: String = "",
    val profileImage: String = "",
    val createdAt: Long = System.currentTimeMillis()
)