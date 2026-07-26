package com.rahul.campusconnect.di

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.NotificationRemoteDataSource
import com.rahul.campusconnect.data.remote.NotificationRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.repository.NotificationRepositoryImpl
import com.rahul.campusconnect.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationRemoteDataSource(
        pathProvider: FirestorePathProvider
    ): NotificationRemoteDataSource {
        return NotificationRemoteDataSourceImpl(pathProvider)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        remoteDataSource: NotificationRemoteDataSource,
        sessionManager: SessionManager
    ): NotificationRepository {
        return NotificationRepositoryImpl(remoteDataSource, sessionManager)
    }
}
