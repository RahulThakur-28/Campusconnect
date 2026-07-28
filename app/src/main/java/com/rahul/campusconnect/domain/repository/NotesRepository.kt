package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.Note

interface NotesRepository {
    suspend fun getNotes(): Result<List<Note>>
    suspend fun getNoteById(noteId: String): Result<Note?>
    suspend fun createNote(note: Note): Result<String>
    suspend fun updateNote(note: Note): Result<Unit>
    suspend fun deleteNote(noteId: String): Result<Unit>
    suspend fun getMyNotes(userId: String): Result<List<Note>>
    suspend fun uploadAttachment(noteId: String, fileUri: Uri, extension: String): Result<Pair<String, String>>
    suspend fun uploadThumbnail(noteId: String, imageUri: Uri): Result<Pair<String, String>>
    suspend fun deleteFile(path: String): Result<Unit>
    suspend fun incrementDownloadCount(noteId: String): Result<Unit>
}
