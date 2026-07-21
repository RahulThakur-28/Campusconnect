package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.User

interface UserRemoteDataSource {

    suspend fun getCurrentUser(): User

    suspend fun updateUser(user: User)

    suspend fun uploadProfileImage(
        imageUri: Uri
    ): Result<String>

    suspend fun updateProfileImage(
        imageUrl: String
    )
}