package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.data.remote.LostFoundRemoteDataSource
import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.domain.repository.LostFoundRepository
import javax.inject.Inject

class LostFoundRepositoryImpl @Inject constructor(
    private val remoteDataSource: LostFoundRemoteDataSource
) : LostFoundRepository {

    override suspend fun getItems(): Result<List<LostFoundItem>> = remoteDataSource.getItems()

    override suspend fun getItemById(itemId: String): Result<LostFoundItem?> = remoteDataSource.getItemById(itemId)

    override suspend fun createItem(item: LostFoundItem): Result<String> = remoteDataSource.createItem(item)

    override suspend fun updateItem(item: LostFoundItem): Result<Unit> = remoteDataSource.updateItem(item)

    override suspend fun deleteItem(itemId: String, imagePath: String?): Result<Unit> = remoteDataSource.deleteItem(itemId, imagePath)

    override suspend fun markAsResolved(itemId: String): Result<Unit> = remoteDataSource.markAsResolved(itemId)

    override suspend fun uploadImage(itemId: String, imageUri: Uri): Result<Pair<String, String>> = remoteDataSource.uploadImage(itemId, imageUri)

    override suspend fun deleteFile(path: String): Result<Unit> = remoteDataSource.deleteFile(path)
}
