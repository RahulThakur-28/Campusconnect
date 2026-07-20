package com.rahul.campusconnect.data.repository

import com.rahul.campusconnect.data.remote.UserRemoteDataSource
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource
) : UserRepository {

    override suspend fun getCurrentUser(): Result<User> {
        return remoteDataSource.getCurrentUser()
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return remoteDataSource.updateUser(user)
    }
}