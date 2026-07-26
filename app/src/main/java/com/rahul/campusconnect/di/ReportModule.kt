package com.rahul.campusconnect.di

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.ReportRemoteDataSource
import com.rahul.campusconnect.data.remote.ReportRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.repository.ReportRepositoryImpl
import com.rahul.campusconnect.domain.repository.ReportRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportModule {

    @Provides
    @Singleton
    fun provideReportRemoteDataSource(
        pathProvider: FirestorePathProvider
    ): ReportRemoteDataSource {
        return ReportRemoteDataSourceImpl(pathProvider)
    }

    @Provides
    @Singleton
    fun provideReportRepository(
        remoteDataSource: ReportRemoteDataSource,
        sessionManager: SessionManager
    ): ReportRepository {
        return ReportRepositoryImpl(remoteDataSource, sessionManager)
    }
}
