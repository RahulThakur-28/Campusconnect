package com.rahul.campusconnect.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.data.remote.LostFoundRemoteDataSource
import com.rahul.campusconnect.data.remote.LostFoundRemoteDataSourceImpl
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
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storageManager: StorageManager
    ): LostFoundRemoteDataSource {
        return LostFoundRemoteDataSourceImpl(
            firestore = firestore,
            auth = auth,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideLostFoundRepository(
        remoteDataSource: LostFoundRemoteDataSource
    ): LostFoundRepository {
        return LostFoundRepositoryImpl(remoteDataSource)
    }
}
