package com.rahul.campusconnect.data.remote

import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.domain.model.Report
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReportRemoteDataSourceImpl @Inject constructor(
    private val pathProvider: FirestorePathProvider
) : ReportRemoteDataSource {

    override suspend fun saveReport(collegeId: String, report: Report): Result<Unit> = try {
        val doc = pathProvider.reports(collegeId).document()
        pathProvider.reports(collegeId).document(doc.id).set(report.copy(id = doc.id, collegeId = collegeId)).await()
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getReportsByUser(collegeId: String, userId: String): Result<List<Report>> = try {
        val snapshot = pathProvider.reports(collegeId)
            .whereEqualTo("userId", userId)
            .get()
            .await()
        val reports = snapshot.documents.mapNotNull { it.toObject(Report::class.java)?.copy(id = it.id) }
        Result.success(reports)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
