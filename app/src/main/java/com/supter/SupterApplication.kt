package com.supter

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.supter.data.db.PurchaseDatabase
import com.supter.data.network.*
import com.supter.repository.UserRepository
import com.supter.repository.UserRepositoryImpl
import com.supter.repository.PurchaseRepository
import com.supter.repository.PurchaseRepositoryImpl
import com.supter.ui.auth.login.LoginViewModelFactory
import com.supter.ui.auth.signup.SignUpViewModelFactory
import com.supter.ui.main.dashboard.DashboardViewModelFactory
import com.supter.ui.main.profile.ProfileViewModelFactory
import com.supter.ui.main.purchase.add.AddPurchaseViewModelFactory
import com.supter.ui.moviedetail.MovieDetailViewModelFactory
import com.supter.ui.movielist.MovieListViewModelFactory
import org.kodein.di.*
import org.kodein.di.android.x.androidXModule

class SupterApplication : Application(), DIAware {

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
        bind<UserRepository>() with singleton {
            UserRepositoryImpl(
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
        bind() from provider { ProfileViewModelFactory(instance()) }
    }

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}