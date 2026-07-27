package com.rahul.campusconnect.domain.model

data class VerificationRequest(
    val requestId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val collegeId: String = "",
    val currentRole: UserRole = UserRole.STUDENT,
    val requestedRole: UserRole = UserRole.VERIFIED_STUDENT,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val enrollmentNumber: String? = null,
    val employeeId: String? = null,
    val department: String = "",
    val academicYear: String? = null,
    val documentUrl: String = "",
    val documentStoragePath: String = "",
    val remarks: String? = null,
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val reviewedBy: String? = null,
    val rejectionReason: String? = null
)
