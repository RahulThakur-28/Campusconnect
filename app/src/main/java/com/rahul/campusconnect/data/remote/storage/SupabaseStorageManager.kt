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

    override suspend fun uploadImage(
        bucket: String,
        path: String,
        imageUri: Uri
    ): Result<String> {

        return try {

            Log.d("SUPABASE_DEBUG", "===================================")
            Log.d("SUPABASE_DEBUG", "Bucket = $bucket")
            Log.d("SUPABASE_DEBUG", "Path = $path")
            Log.d("SUPABASE_DEBUG", "Image Uri = $imageUri")
            Log.d("SUPABASE_DEBUG", "Reading image...")

            val bytes = context.contentResolver
                .openInputStream(imageUri)
                ?.use { it.readBytes() }
                ?: return Result.failure(IOException("Unable to read image"))

            Log.d("SUPABASE_DEBUG", "Bytes = ${bytes.size}")

            val storage = supabaseClient.storage.from(bucket)

            Log.d("SUPABASE_DEBUG", "Using bucket = $bucket")
            Log.d("SUPABASE_DEBUG", "Uploading to -> $bucket/$path")

            storage.upload(
                path = path,
                data = bytes
            ) {
                upsert = true
            }

            val url = storage.publicUrl(path)

            Log.d("SUPABASE_DEBUG", "Public URL = $url")
            Log.d("SUPABASE_DEBUG", "===================================")

            Result.success(url)

        } catch (e: Exception) {

            Log.e("SUPABASE_DEBUG", "===================================")
            Log.e("SUPABASE_DEBUG", "Bucket = $bucket")
            Log.e("SUPABASE_DEBUG", "Path = $path")
            Log.e("SUPABASE_DEBUG", "Exception = ${e.message}", e)
            Log.e("SUPABASE_DEBUG", "===================================")

            Result.failure(e)
        }
    }

    override suspend fun uploadPdf(
        bucket: String,
        path: String,
        pdfUri: Uri
    ): Result<String> {

        return try {

            val bytes = context.contentResolver
                .openInputStream(pdfUri)
                ?.use { it.readBytes() }
                ?: return Result.failure(IOException("Unable to read pdf"))

            val storage = supabaseClient.storage.from(bucket)

            storage.upload(
                path = path,
                data = bytes
            ) {
                upsert = true
            }

            Result.success(storage.publicUrl(path))

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteFile(
        bucket: String,
        path: String
    ): Result<Unit> {

        return try {

            supabaseClient.storage
                .from(bucket)
                .delete(path)

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}