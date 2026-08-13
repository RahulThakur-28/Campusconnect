package com.rahul.campusconnect.data.remote.storage

import android.content.Context
import android.net.Uri
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import java.io.IOException
import javax.inject.Inject

class SupabaseStorageManager @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val supabaseClient: SupabaseClient
) : StorageManager {

    companion object {
        private const val TAG = "SUPABASE_STORAGE"
    }

    /**
     * Upload image to Supabase Storage.
     *
     * Example:
     * bucket = "media"
     * path   = "colleges/88825228/events/banners/uuid.jpg"
     */
    override suspend fun uploadImage(
        bucket: String,
        path: String,
        imageUri: Uri
    ): Result<String> {

        return try {

            Log.d(TAG, "======================================")
            Log.d(TAG, "IMAGE UPLOAD START")
            Log.d(TAG, "Bucket: $bucket")
            Log.d(TAG, "Path: $path")
            Log.d(TAG, "Uri: $imageUri")

            // ----------------------------------------------------
            // Read image bytes
            // ----------------------------------------------------

            val bytes = context.contentResolver
                .openInputStream(imageUri)
                ?.use { inputStream ->
                    inputStream.readBytes()
                }
                ?: return Result.failure(
                    IOException("Unable to read selected image")
                )

            if (bytes.isEmpty()) {
                return Result.failure(
                    IOException("Selected image is empty")
                )
            }

            Log.d(TAG, "Image bytes: ${bytes.size}")

            // ----------------------------------------------------
            // Get Supabase Storage bucket
            // ----------------------------------------------------

            val storage = supabaseClient.storage.from(bucket)

            // ----------------------------------------------------
            // Upload
            //
            // IMPORTANT:
            // upsert = false
            //
            // Files generated with UUID paths should be new files.
            // This avoids unnecessary UPDATE permission requirements.
            // ----------------------------------------------------

            storage.upload(
                path = path,
                data = bytes
            ) {
                upsert = false
            }

            Log.d(TAG, "Image upload successful")

            // ----------------------------------------------------
            // Public URL
            // ----------------------------------------------------

            val publicUrl = storage.publicUrl(path)

            Log.d(TAG, "Public URL: $publicUrl")
            Log.d(TAG, "IMAGE UPLOAD END")
            Log.d(TAG, "======================================")

            Result.success(publicUrl)

        } catch (e: Exception) {

            Log.e(TAG, "======================================")
            Log.e(TAG, "IMAGE UPLOAD FAILED")
            Log.e(TAG, "Bucket: $bucket")
            Log.e(TAG, "Path: $path")
            Log.e(TAG, "Error: ${e.message}", e)
            Log.e(TAG, "======================================")

            Result.failure(e)
        }
    }


    /**
     * Upload PDF/file to Supabase Storage.
     *
     * Example:
     * bucket = "media"
     * path   = "colleges/88825228/notes/uuid.pdf"
     */
    override suspend fun uploadPdf(
        bucket: String,
        path: String,
        pdfUri: Uri
    ): Result<String> {

        return try {

            Log.d(TAG, "======================================")
            Log.d(TAG, "PDF UPLOAD START")
            Log.d(TAG, "Bucket: $bucket")
            Log.d(TAG, "Path: $path")
            Log.d(TAG, "Uri: $pdfUri")

            // ----------------------------------------------------
            // Read PDF bytes
            // ----------------------------------------------------

            val bytes = context.contentResolver
                .openInputStream(pdfUri)
                ?.use { inputStream ->
                    inputStream.readBytes()
                }
                ?: return Result.failure(
                    IOException("Unable to read selected PDF")
                )

            if (bytes.isEmpty()) {
                return Result.failure(
                    IOException("Selected PDF is empty")
                )
            }

            Log.d(TAG, "PDF bytes: ${bytes.size}")

            // ----------------------------------------------------
            // Get Storage bucket
            // ----------------------------------------------------

            val storage = supabaseClient.storage.from(bucket)

            // ----------------------------------------------------
            // Upload
            // ----------------------------------------------------

            storage.upload(
                path = path,
                data = bytes
            ) {
                upsert = false
            }

            Log.d(TAG, "PDF upload successful")

            // ----------------------------------------------------
            // Public URL
            // ----------------------------------------------------

            val publicUrl = storage.publicUrl(path)

            Log.d(TAG, "Public URL: $publicUrl")
            Log.d(TAG, "PDF UPLOAD END")
            Log.d(TAG, "======================================")

            Result.success(publicUrl)

        } catch (e: Exception) {

            Log.e(TAG, "======================================")
            Log.e(TAG, "PDF UPLOAD FAILED")
            Log.e(TAG, "Bucket: $bucket")
            Log.e(TAG, "Path: $path")
            Log.e(TAG, "Error: ${e.message}", e)
            Log.e(TAG, "======================================")

            Result.failure(e)
        }
    }


    /**
     * Delete a file from Supabase Storage.
     */
    override suspend fun deleteFile(
        bucket: String,
        path: String
    ): Result<Unit> {

        return try {

            Log.d(TAG, "======================================")
            Log.d(TAG, "DELETE FILE START")
            Log.d(TAG, "Bucket: $bucket")
            Log.d(TAG, "Path: $path")

            supabaseClient.storage
                .from(bucket)
                .delete(path)

            Log.d(TAG, "File deleted successfully")
            Log.d(TAG, "======================================")

            Result.success(Unit)

        } catch (e: Exception) {

            Log.e(TAG, "======================================")
            Log.e(TAG, "DELETE FILE FAILED")
            Log.e(TAG, "Bucket: $bucket")
            Log.e(TAG, "Path: $path")
            Log.e(TAG, "Error: ${e.message}", e)
            Log.e(TAG, "======================================")

            Result.failure(e)
        }
    }
}