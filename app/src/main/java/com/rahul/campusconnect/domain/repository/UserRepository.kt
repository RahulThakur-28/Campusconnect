package com.rahul.campusconnect.domain.repository


import com.rahul.campusconnect.domain.model.User

interface UserRepository {

    suspend fun getCurrentUser(): Result<User>

    suspend fun updateUser(user: User): Result<Unit>

}