package com.example.supter.data.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.supter.isOnline


//Connectivity status receiver for offline message (snackbar)
class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener!!.onNetworkConnectionChanged(
                isOnline(context!!.applicationContext)
            )
        }

    }

    interface ConnectivityReceiverListener {
        fun onNetworkConnectionChanged(isConnected: Boolean)
    }

    companion object {
        var connectivityReceiverListener: ConnectivityReceiverListener? = null
    }
}