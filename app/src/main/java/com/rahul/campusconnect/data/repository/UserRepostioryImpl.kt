package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.data.remote.UserRemoteDataSource
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun getCurrentUser(): Result<User> {
        return try {
            val user = remoteDataSource.getCurrentUser()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return remoteDataSource.updateUser(user)
    }

    override suspend fun uploadProfileImage(
        imageUri: Uri
    ): Result<String> {
        return remoteDataSource.uploadProfileImage(imageUri)
    }

    override  suspend fun updateProfileImage(imageUrl: String): Result<Unit> {
        return try {
            // update profile image logic

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}