package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.google.firebase.firestore.ListenerRegistration
import com.rahul.campusconnect.domain.model.College
import com.rahul.campusconnect.domain.model.User

interface UserRemoteDataSource {

    suspend fun getUserProfile(uid: String, collegeId: String): User?

    fun getUserListener(uid: String, collegeId: String, onUpdate: (User?) -> Unit): ListenerRegistration

    suspend fun saveUser(user: User): Result<Unit>

    suspend fun getCollege(collegeId: String): College?

    suspend fun isEnrollmentRegistered(collegeId: String, enrollmentNumber: String): Boolean

    suspend fun uploadProfileImage(collegeId: String, userId: String, imageUri: Uri): Result<Pair<String, String>>

    suspend fun deleteFile(path: String): Result<Unit>

    suspend fun updateProfile(user: User): Result<Unit>

    suspend fun updateUserRole(uid: String, collegeId: String, newRole: String): Result<Unit>

    suspend fun getUsersByCollege(collegeId: String): Result<List<User>>

    suspend fun deleteUser(uid: String, collegeId: String): Result<Unit>

}
