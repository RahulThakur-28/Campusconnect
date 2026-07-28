package com.rahul.campusconnect.data.remote

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.rahul.campusconnect.common.constant.Constants
import com.rahul.campusconnect.common.constant.StorageConstants
import com.rahul.campusconnect.common.storage.StoragePathGenerator
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.domain.model.Note
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class NotesRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider,
    private val storageManager: StorageManager
) : NotesRemoteDataSource {

    override suspend fun getNotes(collegeId: String): Result<List<Note>> = try {
        val snapshot = pathProvider.notes(collegeId)
            .whereEqualTo(Constants.DELETED, false)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java)?.copy(id = it.id) }
        Result.success(notes)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getNoteById(collegeId: String, noteId: String): Result<Note?> = try {
        val document = pathProvider.notes(collegeId).document(noteId).get().await()
        val note = document.toObject(Note::class.java)?.copy(id = document.id)
        if (note?.deleted == true) Result.success(null) else Result.success(note)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun createNote(collegeId: String, note: Note): Result<String> = try {
        val document = pathProvider.notes(collegeId).document(note.id.ifBlank { generateNoteId() })
        val finalNote = note.copy(id = document.id, collegeId = collegeId)
        document.set(finalNote).await()
        Result.success(document.id)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateNote(collegeId: String, note: Note): Result<Unit> = try {
        pathProvider.notes(collegeId).document(note.id).set(note.copy(updatedAt = System.currentTimeMillis())).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteNote(collegeId: String, noteId: String): Result<Unit> = try {
        val noteRef = pathProvider.notes(collegeId).document(noteId)
        val note = noteRef.get().await().toObject(Note::class.java)

        // Hard delete associated files from storage
        note?.storagePath?.let { path ->
            if (path.isNotBlank()) storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
        }
        note?.thumbnailStoragePath?.let { path ->
            if (path.isNotBlank()) storageManager.deleteFile(StorageConstants.MEDIA_BUCKET, path)
        }

        // Delete from Firestore
        noteRef.delete().await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getMyNotes(collegeId: String, userId: String): Result<List<Note>> = try {
        val snapshot = pathProvider.notes(collegeId)
            .whereEqualTo("uploadedBy", userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()
        val notes = snapshot.documents.mapNotNull { it.toObject(Note::class.java)?.copy(id = it.id) }
        Result.success(notes)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadAttachment(collegeId: String, noteId: String, fileUri: Uri, extension: String): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.noteFile(collegeId, noteId, extension)
        storageManager.uploadPdf(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            pdfUri = fileUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadThumbnail(collegeId: String, noteId: String, imageUri: Uri): Result<Pair<String, String>> = try {
        val path = StoragePathGenerator.noteThumbnail(collegeId, noteId)
        storageManager.uploadImage(
            bucket = StorageConstants.MEDIA_BUCKET,
            path = path,
            imageUri = imageUri
        ).map { url -> Pair(url, path) }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun deleteFile(path: String): Result<Unit> = storageManager.deleteFile(
        bucket = StorageConstants.MEDIA_BUCKET,
        path = path
    )

    override suspend fun incrementDownloadCount(collegeId: String, noteId: String): Result<Unit> = try {
        pathProvider.notes(collegeId).document(noteId).update("downloadCount", FieldValue.increment(1)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override fun generateNoteId(): String = UUID.randomUUID().toString()
}
