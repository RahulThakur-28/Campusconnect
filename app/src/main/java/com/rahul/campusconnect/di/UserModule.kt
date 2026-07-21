package com.rahul.campusconnect.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.data.remote.UserRemoteDataSource
import com.rahul.campusconnect.data.remote.UserRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.data.repository.UserRepositoryImpl
import com.rahul.campusconnect.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storageManager: StorageManager
    ): UserRemoteDataSource {
        return UserRemoteDataSourceImpl(
            firestore = firestore,
            auth = auth,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        remoteDataSource: UserRemoteDataSource
    ): UserRepository {
        return UserRepositoryImpl(remoteDataSource)
    }
}