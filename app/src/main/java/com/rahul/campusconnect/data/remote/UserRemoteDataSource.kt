package com.rahul.campusconnect.data.remote


import android.net.Uri
import com.rahul.campusconnect.domain.model.User

interface UserRemoteDataSource {

    /**
     * Get current logged-in user's profile
     */
    suspend fun getCurrentUser(): Result<User>

    /**
     * Update complete user profile
     */
    suspend fun updateUser(user: User): Result<Unit>

    /**
     * Update only profile image URL
     */
    suspend fun updateProfileImage(
        imageUrl: String
    ): Result<Unit>

    /**
     * Upload profile image to Firebase Storage
     */
    suspend fun uploadProfileImage(
        imageUri: Uri
    ): Result<String>

}