package com.rahul.campusconnect.di

import com.rahul.campusconnect.data.remote.PlacementRemoteDataSource
import com.rahul.campusconnect.data.remote.PlacementRemoteDataSourceImpl
import com.rahul.campusconnect.data.repository.PlacementRepositoryImpl
import com.rahul.campusconnect.domain.repository.PlacementRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlacementModule {

    @Binds
    @Singleton
    abstract fun bindPlacementRemoteDataSource(
        impl: PlacementRemoteDataSourceImpl
    ): PlacementRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPlacementRepository(
        impl: PlacementRepositoryImpl
    ): PlacementRepository
}