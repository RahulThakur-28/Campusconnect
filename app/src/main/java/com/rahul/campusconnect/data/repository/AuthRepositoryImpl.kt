package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.rahul.campusconnect.domain.model.User
import com.rahul.campusconnect.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepositoryImpl  @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : AuthRepository {

    override suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        collegeId: String,
        branch: String,
        imageUri: Uri?
    ): Result<Unit> {

        return try {

            auth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            val uid = auth.currentUser!!.uid

            val user = User(
                uid = uid,
                fullName = fullName,
                email = email,
                collegeId = collegeId,
                branch = branch
            )

            firestore
                .collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }
    override suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {

        return try {

            auth.signInWithEmailAndPassword(
                email,
                password
            ).await()

            Result.success(Unit)

        } catch (e: Exception) {

            Result.failure(e)

        }
    }
}