package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Note
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotesRemoteDataSourceImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storageManager: StorageManager
) : NotesRemoteDataSource {

    private suspend fun getCollegeId(): String {
        val uid = auth.currentUser?.uid
            ?: throw IllegalStateException("User is not logged in.")

        val snapshot = firestore
            .collection(Constants.USERS)
            .document(uid)
            .get()
            .await()

        return snapshot.getString("collegeId")
            ?: throw IllegalStateException("College ID not found.")
    }

    private suspend fun notesCollection(): CollectionReference {
        return firestore.collection(Constants.COLLEGES)
            .document(getCollegeId())
            .collection(Constants.NOTES)
    }

    override suspend fun getNotes(): Result<List<Note>> {

        return try {

            Log.d("NOTES_QUERY", "Fetching notes...")

            val snapshot = notesCollection()
                .whereEqualTo(Constants.DELETED, false)
                .orderBy(Constants.CREATED_AT, Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d(
                "NOTES_QUERY",
                "Documents found = ${snapshot.size()}"
            )

            snapshot.documents.forEach { document ->

                Log.d(
                    "NOTES_QUERY",
                    document.data.toString()
                )
            }

            val notes = snapshot.documents.mapNotNull {

                it.toObject(Note::class.java)?.copy(id = it.id)

            }

            Result.success(notes)

        } catch (e: FirebaseFirestoreException) {

            Log.e("NOTES_QUERY", "========================================")
            Log.e("NOTES_QUERY", "Firestore Exception")
            Log.e("NOTES_QUERY", "Code    : ${e.code}")
            Log.e("NOTES_QUERY", "Message : ${e.message}")
            Log.e("NOTES_QUERY", "Cause   : ${e.cause}")
            Log.e("NOTES_QUERY", "========================================", e)

            Result.failure(e)

        } catch (e: Exception) {

            Log.e("NOTES_QUERY", "General Exception", e)

            Result.failure(e)
        }
    }

    override suspend fun getNoteById(noteId: String): Result<Note?> = try {
        val document = notesCollection().document(noteId).get().await()
        val note = document.toObject(Note::class.java)?.copy(id = document.id)
        if (note?.deleted == true) Result.success(null) else Result.success(note)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createNote(note: Note): Result<String> = try {
        val document = notesCollection().document(note.id.ifBlank { generateNoteId() })
        val currentTime = System.currentTimeMillis()
        val finalNote = note.copy(
            id = document.id,
            collegeId = getCollegeId(),
            createdAt = currentTime,
            updatedAt = currentTime,
            deleted = false
        )
        document.set(finalNote).await()
        Log.d("NOTES_CREATE", "Note created with ID: ${document.id}")
        Result.success(document.id)
    } catch (e: Exception) {
        Log.e("NOTES_CREATE", "Error creating note", e)
        Result.failure(e)
    }

    override suspend fun updateNote(note: Note): Result<Unit> = try {
        val updatedNote = note.copy(updatedAt = System.currentTimeMillis())
        notesCollection().document(note.id).set(updatedNote).await()
        Log.d("NOTES_UPDATE", "Note updated: ${note.id}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("NOTES_UPDATE", "Error updating note", e)
        Result.failure(e)
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> = try {
        notesCollection().document(noteId).update(
            mapOf(
                "deleted" to true,
                "updatedAt" to System.currentTimeMillis()
            )
        ).await()
        Log.d("NOTES_DELETE", "Note soft deleted: $noteId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e("NOTES_DELETE", "Error deleting note", e)
        Result.failure(e)
    }

    override suspend fun getMyNotes(userId: String): Result<List<Note>> = try {
        val snapshot = notesCollection()
            .whereEqualTo("deleted", false)
            .whereEqualTo("uploadedBy", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java)?.copy(id = it.id) }
        Result.success(notes)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadAttachment(
        noteId: String,
        fileUri: Uri,
        extension: String
    ): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.noteFile(getCollegeId(), noteId, extension)
        storageManager.uploadPdf(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            pdfUri = fileUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Log.e("NOTES_UPLOAD", "Error uploading attachment", e)
        Result.failure(e)
    }

    override suspend fun deleteFile(path: String): Result<Unit> = try {
        storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun incrementDownloadCount(noteId: String): Result<Unit> = try {
        notesCollection().document(noteId).update("downloadCount", FieldValue.increment(1)).await()
        Log.d("NOTES_DOWNLOAD", "Incremented download count for $noteId")
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun generateNoteId(): String = firestore.collection("temp").document().id
}
