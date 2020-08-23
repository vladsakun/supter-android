package com.supter

import android.app.Application
import com.supter.data.db.PurchaseDatabase
import com.supter.data.network.*
import com.supter.data.repository.PurchaseRepository
import com.supter.data.repository.PurchaseRepositoryImpl
import com.supter.ui.moviedetail.MovieDetailViewModelFactory
import com.supter.ui.movielist.MovieListViewModelFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class SupterApplication : Application(), KodeinAware {

    override val kodein = Kodein.lazy {

        //AndroidX module
        import(androidXModule(this@SupterApplication))

        //Database
        bind() from singleton { PurchaseDatabase(instance()) }

        //Dao
        bind() from singleton { instance<PurchaseDatabase>().movieDao() }

        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }

        //DataSource
        bind<PurchaseNetworkDataSource>() with singleton { PurchaseNetworkDataSourceImpl(instance()) }

        //Api service
        bind() from singleton { PurchaseApiService(instance()) }

        //Repository
        bind<PurchaseRepository>() with singleton { PurchaseRepositoryImpl(instance(), instance(), instance()) }

        //ViewModelFactories
        bind() from provider {  MovieListViewModelFactory(instance()) }
        bind() from provider {  MovieDetailViewModelFactory(instance()) }
    }
}