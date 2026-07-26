package com.rahul.campusconnect.di

import com.rahul.campusconnect.common.session.SessionManager
import com.rahul.campusconnect.data.remote.NotesRemoteDataSource
import com.rahul.campusconnect.data.remote.NotesRemoteDataSourceImpl
import com.rahul.campusconnect.data.remote.firestore.FirestorePathProvider
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
        pathProvider: FirestorePathProvider,
        storageManager: StorageManager
    ): NotesRemoteDataSource {
        return NotesRemoteDataSourceImpl(
            pathProvider = pathProvider,
            storageManager = storageManager
        )
    }

    @Provides
    @Singleton
    fun provideNotesRepository(
        remoteDataSource: NotesRemoteDataSource,
        sessionManager: SessionManager
    ): NotesRepository {
        return NotesRepositoryImpl(remoteDataSource, sessionManager)
    }
}
