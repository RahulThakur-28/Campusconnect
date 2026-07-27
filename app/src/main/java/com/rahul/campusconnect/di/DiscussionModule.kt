package com.rahul.campusconnect.di

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.DiscussionRemoteDataSource
import com.rahul.campusconnect.data.remote.DiscussionRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.repository.DiscussionRepositoryImpl
import com.rahul.campusconnect.domain.repository.EventQARepository
import com.rahul.campusconnect.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiscussionModule {

    @Provides
    @Singleton
    fun provideDiscussionRemoteDataSource(
        pathProvider: FirestorePathProvider
    ): DiscussionRemoteDataSource {
        return DiscussionRemoteDataSourceImpl(pathProvider)
    }

    @Provides
    @Singleton
    fun provideEventQARepository(
        remoteDataSource: DiscussionRemoteDataSource,
        sessionManager: SessionManager,
        notificationRepository: NotificationRepository
    ): EventQARepository {
        return DiscussionRepositoryImpl(
            remoteDataSource,
            sessionManager,
            notificationRepository
        )
    }
}
