package com.rahul.campusconnect.di


import com.rahul.campusconnect.data.repository.AuthRepositoryImpl
import com.rahul.campusconnect.data.repository.SettingsRepositoryImpl
import com.rahul.campusconnect.data.repository.UserRepositoryImpl
import com.rahul.campusconnect.data.repository.VerificationRepositoryImpl
import com.rahul.campusconnect.domain.repository.AuthRepository
import com.rahul.campusconnect.domain.repository.SettingsRepository
import com.rahul.campusconnect.domain.repository.UserRepository
import com.rahul.campusconnect.domain.repository.VerificationRepository
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

    @Binds
    @Singleton
    abstract fun bindVerificationRepository(
        impl: VerificationRepositoryImpl
    ): VerificationRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: SettingsRepositoryImpl
    ): SettingsRepository
}
