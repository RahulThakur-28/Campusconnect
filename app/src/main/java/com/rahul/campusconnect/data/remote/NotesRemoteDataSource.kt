package com.rahul.campusconnect.data.remote

import android.net.Uri
import com.rahul.campusconnect.domain.model.Note

interface NotesRemoteDataSource {
    suspend fun getNotes(collegeId: String): Result<List<Note>>
    suspend fun getNoteById(collegeId: String, noteId: String): Result<Note?>
    suspend fun createNote(collegeId: String, note: Note): Result<String>
    suspend fun updateNote(collegeId: String, note: Note): Result<Unit>
    suspend fun deleteNote(collegeId: String, noteId: String): Result<Unit>
    suspend fun getMyNotes(collegeId: String, userId: String): Result<List<Note>>
    suspend fun uploadAttachment(collegeId: String, noteId: String, fileUri: Uri, extension: String): Result<Pair<String, String>>
    suspend fun uploadThumbnail(collegeId: String, noteId: String, imageUri: Uri): Result<Pair<String, String>>
    suspend fun deleteFile(path: String): Result<Unit>
    suspend fun incrementDownloadCount(collegeId: String, noteId: String): Result<Unit>
    fun generateNoteId(): String
}
