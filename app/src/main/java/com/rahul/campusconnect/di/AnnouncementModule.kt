package com.rahul.campusconnect.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.data.remote.AnnouncementRemoteDataSource
import com.rahul.campusconnect.data.remote.AnnouncementRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.data.repository.AnnouncementRepositoryImpl
import com.rahul.campusconnect.domain.repository.AnnouncementRepository
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
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storageManager: StorageManager
    ): AnnouncementRemoteDataSource {
        return AnnouncementRemoteDataSourceImpl(
            firestore = firestore,
            auth = auth,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideAnnouncementRepository(
        remoteDataSource: AnnouncementRemoteDataSource
    ): AnnouncementRepository {
        return AnnouncementRepositoryImpl(remoteDataSource)
    }
}
