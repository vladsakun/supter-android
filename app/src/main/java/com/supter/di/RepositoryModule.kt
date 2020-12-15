package com.supter.di

import com.supter.repository.PurchaseRepository
import com.supter.repository.PurchaseRepositoryImpl
import com.supter.repository.UserRepository
import com.supter.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class PurchaseRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindPurchaseRepository(impl: PurchaseRepositoryImpl): PurchaseRepository
}

@InstallIn(ApplicationComponent::class)
@Module
abstract class UserRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}

