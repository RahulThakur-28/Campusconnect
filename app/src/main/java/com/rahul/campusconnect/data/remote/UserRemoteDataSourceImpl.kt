package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.User
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageManager: StorageManager
) : UserRemoteDataSource {

    private val usersCollection =
        firestore.collection(Constants.USERS)

    override suspend fun getCurrentUser(): User {

        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in.")

        val snapshot = usersCollection
            .document(uid)
            .get()
            .await()

        return snapshot.toObject(User::class.java)
            ?: throw IllegalStateException("User not found.")
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            // update logic
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadProfileImage(
        imageUri: Uri
    ): Result<String> {

        val fileName = "${UUID.randomUUID()}.jpg"

        return storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = "${StorageConstants.PROFILE}/$fileName",
            imageUri = imageUri
        )
    }

    override suspend fun updateProfileImage(
        imageUrl: String
    ) {

        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User not logged in.")

        usersCollection
            .document(uid)
            .update(
                "profileImage",
                imageUrl,
                Constants.UPDATED_AT,
                System.currentTimeMillis()
            )
            .await()
    }
}