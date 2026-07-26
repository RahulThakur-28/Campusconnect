package com.rahul.campusconnect.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rahul.campusconnect.data.remote.NotesRemoteDataSource
import com.rahul.campusconnect.data.remote.NotesRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.data.repository.NotesRepositoryImpl
import com.rahul.campusconnect.domain.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotesModule {

    @Provides
    @Singleton
    fun provideNotesRemoteDataSource(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storageManager: StorageManager
    ): NotesRemoteDataSource {
        return NotesRemoteDataSourceImpl(
            firestore = firestore,
            auth = auth,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideNotesRepository(
        remoteDataSource: NotesRemoteDataSource
    ): NotesRepository {
        return NotesRepositoryImpl(remoteDataSource)
    }
}
