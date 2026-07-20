package com.rahul.campusconnect.di


import com.rahul.campusconnect.data.repository.AuthRepositoryImpl
import com.rahul.campusconnect.data.repository.UserRepositoryImpl
import com.rahul.campusconnect.domain.repository.AuthRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository




}