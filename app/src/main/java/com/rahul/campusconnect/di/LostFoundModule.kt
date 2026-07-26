package com.rahul.campusconnect.di

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.LostFoundRemoteDataSource
import com.rahul.campusconnect.data.remote.LostFoundRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.data.repository.LostFoundRepositoryImpl
import com.rahul.campusconnect.domain.repository.LostFoundRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LostFoundModule {

    @Provides
    @Singleton
    fun provideLostFoundRemoteDataSource(
        pathProvider: FirestorePathProvider,
        storageManager: StorageManager
    ): LostFoundRemoteDataSource {
        return LostFoundRemoteDataSourceImpl(
            pathProvider = pathProvider,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideLostFoundRepository(
        remoteDataSource: LostFoundRemoteDataSource,
        sessionManager: SessionManager
    ): LostFoundRepository {
        return LostFoundRepositoryImpl(remoteDataSource, sessionManager)
    }
}
