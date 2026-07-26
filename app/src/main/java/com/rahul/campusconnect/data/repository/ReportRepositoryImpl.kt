package com.rahul.campusconnect.data.repository

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.ReportRemoteDataSource
import com.rahul.campusconnect.domain.model.Report
import com.rahul.campusconnect.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val remoteDataSource: ReportRemoteDataSource,
    private val sessionManager: SessionManager
) : ReportRepository {

    private fun getCollegeId(): String? = sessionManager.getCollegeId()
    private fun getUserId(): String? = sessionManager.getUid()

    override suspend fun submitReport(report: Report): Result<Unit> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        return remoteDataSource.saveReport(collegeId, report)
    }

    override suspend fun getMyReports(): Result<List<Report>> {
        val collegeId = getCollegeId() ?: return Result.failure(Exception("No college ID"))
        val userId = getUserId() ?: return Result.failure(Exception("No user ID"))
        return remoteDataSource.getReportsByUser(collegeId, userId)
    }
}
