package com.rahul.campusconnect.data.repository

import android.net.Uri
import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.LostFoundRemoteDataSource
import com.rahul.campusconnect.domain.model.LostFoundItem
import com.rahul.campusconnect.domain.repository.LostFoundRepository
import javax.inject.Inject

class LostFoundRepositoryImpl @Inject constructor(
    private val remoteDataSource: LostFoundRemoteDataSource,
    private val sessionManager: SessionManager
) : LostFoundRepository {

    private fun getCollegeId(): String = sessionManager.getCollegeId() ?: throw IllegalStateException("No college ID")

    override suspend fun getItems(): Result<List<LostFoundItem>> = remoteDataSource.getItems(getCollegeId())
    override suspend fun getItemById(itemId: String): Result<LostFoundItem?> = remoteDataSource.getItemById(getCollegeId(), itemId)
    override suspend fun createItem(item: LostFoundItem): Result<String> = remoteDataSource.createItem(getCollegeId(), item)
    override suspend fun updateItem(item: LostFoundItem): Result<Unit> = remoteDataSource.updateItem(getCollegeId(), item)
    override suspend fun deleteItem(itemId: String, imagePath: String?): Result<Unit> = remoteDataSource.deleteItem(getCollegeId(), itemId, imagePath)
    override suspend fun markAsResolved(itemId: String): Result<Unit> = remoteDataSource.markAsResolved(getCollegeId(), itemId)
    override suspend fun uploadImage(itemId: String, imageUri: Uri): Result<Pair<String, String>> = remoteDataSource.uploadImage(getCollegeId(), itemId, imageUri)
    override suspend fun deleteFile(path: String): Result<Unit> = remoteDataSource.deleteFile(path)
}
