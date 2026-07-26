package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.College
import com.rahul.campusconnect.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {

    val currentUser: StateFlow<User?>

    suspend fun loadUserSession(): Result<Unit>

    suspend fun getUserProfile(uid: String, collegeId: String): Result<User?>

    suspend fun updateProfile(user: User): Result<Unit>

    suspend fun validateCollegeId(collegeId: String): Result<College?>

    suspend fun isEnrollmentRegistered(collegeId: String, enrollmentNumber: String): Result<Boolean>

    suspend fun uploadProfileImage(imageUri: Uri): Result<String>


    suspend fun getCurrentUser(): Result<User>

    fun clearSession()
}
