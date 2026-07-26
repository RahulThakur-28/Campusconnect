package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.data.remote.NotesRemoteDataSource
import com.rahul.campusconnect.domain.model.Note
import com.rahul.campusconnect.domain.repository.NotesRepository
import javax.inject.Inject

class NotesRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotesRemoteDataSource
) : NotesRepository {

    override suspend fun getNotes(): Result<List<Note>> = remoteDataSource.getNotes()

    override suspend fun getNoteById(noteId: String): Result<Note?> = remoteDataSource.getNoteById(noteId)

    override suspend fun createNote(note: Note): Result<String> = remoteDataSource.createNote(note)

    override suspend fun updateNote(note: Note): Result<Unit> = remoteDataSource.updateNote(note)

    override suspend fun deleteNote(noteId: String): Result<Unit> = remoteDataSource.deleteNote(noteId)

    override suspend fun getMyNotes(userId: String): Result<List<Note>> = remoteDataSource.getMyNotes(userId)

    override suspend fun uploadAttachment(
        noteId: String,
        fileUri: Uri,
        extension: String
    ): Result<Pair<String, String>> = remoteDataSource.uploadAttachment(noteId, fileUri, extension)

    override suspend fun deleteFile(path: String): Result<Unit> = remoteDataSource.deleteFile(path)

    override suspend fun incrementDownloadCount(noteId: String): Result<Unit> = remoteDataSource.incrementDownloadCount(noteId)
}
