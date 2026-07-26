package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.College
import com.rahul.campusconnect.domain.model.User

interface UserRemoteDataSource {

    suspend fun getUserProfile(uid: String, collegeId: String): User?

    suspend fun saveUser(user: User): Result<Unit>

    suspend fun getCollege(collegeId: String): College?

    suspend fun isEnrollmentRegistered(collegeId: String, enrollmentNumber: String): Boolean

    suspend fun uploadProfileImage(imageUri: Uri): Result<String>

    suspend fun updateProfile(user: User): Result<Unit>

}
