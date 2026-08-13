package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.NotesRemoteDataSource
import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.repository.NotesRepository
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotesRemoteDataSource,
    private val sessionManager: SessionManager
) : NotesRepository {

    private fun getCollegeId(): String = sessionManager.getCollegeId() ?: throw IllegalStateException("No college ID")

    override suspend fun getNotes(): Result<List<Note>> = remoteDataSource.getNotes(getCollegeId())
    override suspend fun getNoteById(noteId: String): Result<Note?> = remoteDataSource.getNoteById(getCollegeId(), noteId)
    override suspend fun createNote(note: Note): Result<String> = remoteDataSource.createNote(getCollegeId(), note)
    override suspend fun updateNote(note: Note): Result<Unit> = remoteDataSource.updateNote(getCollegeId(), note)
    override suspend fun deleteNote(noteId: String): Result<Unit> = remoteDataSource.deleteNote(getCollegeId(), noteId)
    override suspend fun getMyNotes(userId: String): Result<List<Note>> = remoteDataSource.getMyNotes(getCollegeId(), userId)
    override suspend fun uploadAttachment(noteId: String, fileUri: Uri, extension: String): Result<Pair<String, String>> = try {
        uploadAttachment(getCollegeId(), noteId, fileUri, extension)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadAttachment(collegeId: String, noteId: String, fileUri: Uri, extension: String): Result<Pair<String, String>> = 
        remoteDataSource.uploadAttachment(collegeId, noteId, fileUri, extension)

    override suspend fun uploadThumbnail(noteId: String, imageUri: Uri): Result<Pair<String, String>> = try {
        uploadThumbnail(getCollegeId(), noteId, imageUri)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun uploadThumbnail(collegeId: String, noteId: String, imageUri: Uri): Result<Pair<String, String>> = 
        remoteDataSource.uploadThumbnail(collegeId, noteId, imageUri)
    override suspend fun deleteFile(path: String): Result<Unit> = remoteDataSource.deleteFile(path)
    override suspend fun incrementDownloadCount(noteId: String): Result<Unit> = remoteDataSource.incrementDownloadCount(getCollegeId(), noteId)

}
