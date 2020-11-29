package com.supter.data.network

import android.content.Context
import com.supter.utils.exceptions.NoConnectivityException
import com.supter.utils.isOnline
import okhttp3.Interceptor
import okhttp3.Response

class ConnectivityInterceptorImpl(
    context: Context
) : ConnectivityInterceptor {

    private val appContext = context.applicationContext

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline(this.appContext))
            throw NoConnectivityException()
        return chain.proceed(chain.request())
    }

}