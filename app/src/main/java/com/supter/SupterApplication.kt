package com.supter

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.supter.data.db.PurchaseDatabase
import com.supter.data.network.*
import com.supter.data.repository.AuthRepository
import com.supter.data.repository.AuthRepositoryImpl
import com.supter.data.repository.PurchaseRepository
import com.supter.data.repository.PurchaseRepositoryImpl
import com.supter.ui.auth.login.LoginViewModelFactory
import com.supter.ui.auth.signup.SignUpViewModelFactory
import com.supter.ui.main.dashboard.DashboardViewModelFactory
import com.supter.ui.main.purchase.add.AddPurchaseViewModel
import com.supter.ui.main.purchase.add.AddPurchaseViewModelFactory
import com.supter.ui.moviedetail.MovieDetailViewModelFactory
import com.supter.ui.movielist.MovieListViewModelFactory
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule

class SupterApplication : Application(), DIAware {

    companion object {
        lateinit var instance: SupterApplication
    }

    override val di by DI.lazy {

        import(androidXModule(this@SupterApplication))

        //Database
        bind() from singleton { PurchaseDatabase(instance()) }

        //Dao
        bind() from singleton { instance<PurchaseDatabase>().movieDao() }

        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }

        //DataSource
        bind<PurchaseNetworkDataSource>() with singleton { PurchaseNetworkDataSourceImpl(instance()) }

        //Api service
        bind() from singleton { Api(instance()) }

        //Repository
        bind<PurchaseRepository>() with singleton {
            PurchaseRepositoryImpl(
                instance(),
                instance(),
                instance()
            )
        }
        bind<AuthRepository>() with singleton {
            AuthRepositoryImpl(
                instance(),
                instance(),
                instance()
            )
        }

        //ViewModelFactories
        bind() from provider { LoginViewModelFactory(instance()) }
        bind() from provider { MovieListViewModelFactory(instance()) }
        bind() from provider { MovieDetailViewModelFactory(instance()) }
        bind() from provider { DashboardViewModelFactory(instance()) }
        bind() from provider { SignUpViewModelFactory(instance()) }
        bind() from provider { AddPurchaseViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}