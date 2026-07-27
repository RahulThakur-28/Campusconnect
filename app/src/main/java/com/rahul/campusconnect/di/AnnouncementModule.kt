package com.rahul.campusconnect.di

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.AnnouncementRemoteDataSource
import com.rahul.campusconnect.data.remote.AnnouncementRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.data.repository.AnnouncementRepositoryImpl
import com.rahul.campusconnect.domain.repository.AnnouncementRepository
import com.rahul.campusconnect.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnnouncementModule {

    @Provides
    @Singleton
    fun provideAnnouncementRemoteDataSource(
        pathProvider: FirestorePathProvider,
        storageManager: StorageManager
    ): AnnouncementRemoteDataSource {
        return AnnouncementRemoteDataSourceImpl(
            pathProvider = pathProvider,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideAnnouncementRepository(
        remoteDataSource: AnnouncementRemoteDataSource,
        sessionManager: SessionManager,
        notificationRepository: NotificationRepository
    ): AnnouncementRepository {
        return AnnouncementRepositoryImpl(
            remoteDataSource,
            sessionManager,
            notificationRepository
        )
    }
}
