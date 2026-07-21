package com.rahul.campusconnect.di


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.data.remote.EventRemoteDataSource
import com.rahul.campusconnect.data.remote.EventRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.data.repository.EventRepositoryImpl
import com.rahul.campusconnect.domain.repository.EventRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EventModule {

    @Provides
    @Singleton
    fun provideEventRemoteDataSource(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storageManager: StorageManager
    ): EventRemoteDataSource {
        return EventRemoteDataSourceImpl(
            firestore = firestore,
            auth = auth,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        remoteDataSource: EventRemoteDataSource
    ): EventRepository {
        return EventRepositoryImpl(remoteDataSource)
    }
}