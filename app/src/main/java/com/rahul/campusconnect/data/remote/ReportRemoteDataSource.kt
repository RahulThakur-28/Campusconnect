package com.rahul.campusconnect.data.remote

import com.rahul.campusconnect.domain.model.Report

interface ReportRemoteDataSource {
    suspend fun saveReport(collegeId: String, report: Report): Result<Unit>
    suspend fun getReportsByUser(collegeId: String, userId: String): Result<List<Report>>
}
