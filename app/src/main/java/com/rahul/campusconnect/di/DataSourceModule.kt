package com.rahul.campusconnect.di

import com.rahul.campusconnect.data.remote.UserRemoteDataSource
import com.rahul.campusconnect.data.remote.UserRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    @Singleton
    abstract fun bindUserRemoteDataSource(
        impl: UserRemoteDataSourceImpl
    ): UserRemoteDataSource
}