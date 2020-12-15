package com.supter.data.network

import android.content.Context
import com.supter.utils.exceptions.NoConnectivityException
import com.supter.utils.isOnline
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ConnectivityInterceptorImpl @Inject constructor(
    @ApplicationContext context: Context
) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline(this.appContext))
            throw NoConnectivityException()
        return chain.proceed(chain.request())
    }

}