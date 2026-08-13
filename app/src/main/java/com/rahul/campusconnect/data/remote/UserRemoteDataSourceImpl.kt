package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.College
import com.rahul.campusconnect.domain.model.User
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider,
    private val storageManager: StorageManager
) : UserRemoteDataSource {

    override suspend fun getUserProfile(uid: String, collegeId: String): User? {
        return try {
            pathProvider.users(collegeId)
                .document(uid)
                .get()
                .await()
                .toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override fun getUserListener(uid: String, collegeId: String, onUpdate: (User?) -> Unit): ListenerRegistration {
        return pathProvider.users(collegeId)
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                onUpdate(snapshot?.toObject(User::class.java))
            }
    }

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            pathProvider.users(user.collegeId)
                .document(user.uid)
                .set(user)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getCollege(collegeId: String): College? {

        val snapshot = pathProvider.college(collegeId)
            .get()
            .await()

        Log.d("COLLEGE", "Exists = ${snapshot.exists()}")

        if (!snapshot.exists()) {
            return null
        }

        return snapshot.toObject(College::class.java)
    }

    override suspend fun isEnrollmentRegistered(collegeId: String, enrollmentNumber: String): Boolean {
        return try {
            val query = pathProvider.users(collegeId)
                .whereEqualTo("enrollmentNumber", enrollmentNumber)
                .limit(1)
                .get()
                .await()
            !query.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun uploadProfileImage(collegeId: String, userId: String, imageUri: Uri): Result<Pair<String, String>> {
        val path = StoragePathGenerator.profileImage(collegeId, userId)
        return storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = imageUri
        ).map { url -> Pair(url, path) }
    }

    override suspend fun updateProfile(user: User): Result<Unit> {
        return try {
            val updates = mapOf(
                "fullName" to user.fullName,
                "phone" to user.phone,
                "bio" to user.bio,
                "profileImage" to user.profileImage,
                "profileImageStoragePath" to user.profileImageStoragePath,
                "department" to user.department,
                "academicYear" to user.academicYear,
                "section" to user.section,
                "updatedAt" to System.currentTimeMillis()
            )
            pathProvider.users(user.collegeId)
                .document(user.uid)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserRole(uid: String, collegeId: String, newRole: String): Result<Unit> = try {
        pathProvider.users(collegeId).document(uid).update("role", newRole).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getUsersByCollege(collegeId: String): Result<List<User>> = try {
        val snapshot = pathProvider.users(collegeId).get().await()
        val users = snapshot.documents.mapNotNull { it.toObject(User::class.java)?.copy(uid = it.id) }
        Result.success(users)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteUser(uid: String, collegeId: String): Result<Unit> {
        return try {
            val user = getUserProfile(uid, collegeId)
            user?.profileImageStoragePath?.let { path ->
                if (path.isNotBlank()) {
                    storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
                }
            }

            pathProvider.users(collegeId).document(uid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFile(path: String): Result<Unit> {
        return storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
    }
}
