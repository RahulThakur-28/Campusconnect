package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.rahul.campusconnect.common.session.PreferenceManager
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.UserRemoteDataSource
import com.rahul.campusconnect.domain.model.College
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.UserRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val sessionManager: SessionManager,
    private val preferenceManager: PreferenceManager,
    private val auth: FirebaseAuth
) : UserRepository {

    private var userListener: ListenerRegistration? = null

    override val currentUser: StateFlow<User?> = sessionManager.currentUser

    override suspend fun loadUserSession(): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Not logged in"))
            val collegeId = preferenceManager.getCollegeId() ?: return Result.failure(Exception("College ID missing in session"))
            
            startUserListener(uid, collegeId)
            
            val user = remoteDataSource.getUserProfile(uid, collegeId)
            if (user != null) {
                sessionManager.updateSession(user)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Profile not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun startUserListener(uid: String, collegeId: String) {
        userListener?.remove()
        userListener = remoteDataSource.getUserListener(uid, collegeId) { user ->
            sessionManager.updateSession(user)
        }
    }

    override suspend fun getUserProfile(uid: String, collegeId: String): Result<User?> {
        return try {
            val user = remoteDataSource.getUserProfile(uid, collegeId)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(user: User): Result<Unit> {
        return try {
            remoteDataSource.updateProfile(user).onSuccess {
                if (user.uid == sessionManager.getUid()) {
                    sessionManager.updateSession(user)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun validateCollegeId(collegeId: String): Result<College?> {
        return try {
            val college = remoteDataSource.getCollege(collegeId)
            Result.success(college)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isEnrollmentRegistered(collegeId: String, enrollmentNumber: String): Result<Boolean> {
        return try {
            val exists = remoteDataSource.isEnrollmentRegistered(collegeId, enrollmentNumber)
            Result.success(exists)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfileImage(imageUri: Uri): Result<String> {
        return remoteDataSource.uploadProfileImage(imageUri)
    }

    override suspend fun getUsersByCollege(collegeId: String): Result<List<User>> {
        return remoteDataSource.getUsersByCollege(collegeId)
    }

    override suspend fun updateUserRole(
        uid: String,
        collegeId: String,
        newRole: com.rahul.campusconnect.domain.model.UserRole
    ): Result<Unit> {
        return remoteDataSource.updateUserRole(uid, collegeId, newRole.name)
    }

    override suspend fun getCurrentUser(): Result<User> {
        val user = sessionManager.currentUser.value
        return if (user != null) {
            Result.success(user)
        } else {
            loadUserSession().map { sessionManager.currentUser.value!! }
        }
    }

    override fun clearSession() {
        userListener?.remove()
        userListener = null
        sessionManager.clearSession()
    }
}
