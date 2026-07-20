package com.rahul.campusconnect.data.remote


import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : UserRemoteDataSource {

    private val usersCollection = firestore.collection("users")

    override suspend fun getCurrentUser(): Result<User> {
        return try {

            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))

            val snapshot = usersCollection
                .document(uid)
                .get()
                .await()

            val user = snapshot.toObject(User::class.java)
                ?: return Result.failure(Exception("User not found"))

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {

            usersCollection
                .document(user.uid)
                .set(user)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfileImage(
        imageUrl: String
    ): Result<Unit> {

        TODO("Will implement with Firebase Storage")
    }

    override suspend fun uploadProfileImage(
        imageUri: Uri
    ): Result<String> {

        TODO("Will implement with Firebase Storage")
    }
}