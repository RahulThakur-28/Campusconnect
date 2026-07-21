package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.User

interface UserRepository {

    suspend fun getCurrentUser(): Result<User>

    suspend fun updateUser(user: User): Result<Unit>

    suspend fun uploadProfileImage(
        imageUri: Uri
    ): Result<String>

    suspend fun updateProfileImage(
        imageUrl: String
    ): Result<Unit>
}