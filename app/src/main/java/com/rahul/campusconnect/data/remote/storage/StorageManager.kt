package com.rahul.campusconnect.data.remote.storage

import android.net.Uri

interface StorageManager {

    /**
     * Upload image and return public URL.
     */
    suspend fun uploadImage(
        bucket: String,
        path: String,
        imageUri: Uri
    ): Result<String>

    /**
     * Upload PDF and return public URL.
     */
    suspend fun uploadPdf(
        bucket: String,
        path: String,
        pdfUri: Uri
    ): Result<String>

    /**
     * Delete file from storage.
     */
    suspend fun deleteFile(
        bucket: String,
        path: String
    ): Result<Unit>
}