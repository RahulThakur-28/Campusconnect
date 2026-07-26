package com.rahul.campusconnect.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.rahul.campusconnect.common.session.PreferenceManager
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.UserRemoteDataSource
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val sessionManager: SessionManager,
    private val preferenceManager: PreferenceManager
) : AuthRepository {

    override suspend fun register(user: User, password: String): Result<Unit> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(user.email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Auth failed")
            
            val newUser = user.copy(uid = uid)
            userRemoteDataSource.saveUser(newUser).getOrThrow()
            
            preferenceManager.saveCollegeId(newUser.collegeId)
            sessionManager.updateSession(newUser)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Auth failed")
            
            val collegeId = preferenceManager.getCollegeId() 
                ?: throw Exception("Please enter your College ID first on this device")
            
            val user = userRemoteDataSource.getUserProfile(uid, collegeId)
                ?: throw Exception("User profile not found in college $collegeId")
            
            sessionManager.updateSession(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            sessionManager.clearSession()
            preferenceManager.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}
