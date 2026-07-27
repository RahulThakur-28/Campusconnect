package com.rahul.campusconnect.domain.repository

import android.net.Uri
import com.rahul.campusconnect.domain.model.VerificationRequest
import kotlinx.coroutines.flow.Flow

interface VerificationRepository {
    suspend fun submitVerificationRequest(request: VerificationRequest, documentUri: Uri): Result<Unit>
    fun getRequestsByStatus(collegeId: String, status: String): Flow<List<VerificationRequest>>
    suspend fun approveRequest(request: VerificationRequest, adminId: String): Result<Unit>
    suspend fun rejectRequest(collegeId: String, userId: String, reason: String, adminId: String): Result<Unit>
    suspend fun getRequestByUserId(userId: String, collegeId: String): Result<VerificationRequest?>
}
