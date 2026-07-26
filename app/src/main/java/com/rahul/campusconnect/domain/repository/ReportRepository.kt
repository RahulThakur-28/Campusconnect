package com.rahul.campusconnect.domain.repository

import com.rahul.campusconnect.domain.model.Report

interface ReportRepository {
    suspend fun submitReport(report: Report): Result<Unit>
    suspend fun getMyReports(): Result<List<Report>>
}
