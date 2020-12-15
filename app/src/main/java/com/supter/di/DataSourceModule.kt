package com.supter.di

import com.supter.data.network.PurchaseNetworkDataSource
import com.supter.data.network.PurchaseNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
abstract class DataSourceModule {
    @Singleton
    @Binds
    abstract fun bindPurchaseDataSource(impl: PurchaseNetworkDataSourceImpl): PurchaseNetworkDataSource
}