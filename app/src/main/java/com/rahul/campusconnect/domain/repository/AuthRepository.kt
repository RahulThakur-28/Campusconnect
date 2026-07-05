package com.rahul.campusconnect.domain.repository

import android.net.Uri

interface AuthRepository {

    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        collegeId: String,
        branch: String,
        imageUri: Uri?
    ): Result<Unit>

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>

}