package com.rahul.campusconnect.domain.model

import com.google.firebase.Timestamp

data class College(
    val collegeId: String = "",
    val collegeName: String = "",
    val databaseName: String = "",
    val city: String = "",
    val state: String = "",
    val country: String = "",
    val website: String = "",
    val logo: String = "",
    val adminEmail: String = "",
    val isActive: Boolean = true,
    val createdAt: Timestamp? = null,
    val updatedAt: Timestamp? = null
)