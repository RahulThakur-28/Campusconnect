package com.rahul.campusconnect.di


import com.rahul.campusconnect.data.remote.storage.StorageManager
import com.rahul.campusconnect.data.remote.storage.SupabaseStorageManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindStorageManager(
        supabaseStorageManager: SupabaseStorageManager
    ): StorageManager
}