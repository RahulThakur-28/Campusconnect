package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {

    suspend fun register(
        user: User,
        password: String
    ): Result<Unit>

    suspend fun login(
        email: String,
        password: String
    ): Result<Unit>

    suspend fun logout(): Result<Unit>

    fun isUserLoggedIn(): Boolean
}
