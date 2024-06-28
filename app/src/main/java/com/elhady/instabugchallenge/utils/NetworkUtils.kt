package com.elhady.instabugchallenge.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object NetworkUtils : ConnectivityManager.NetworkCallback() {

    private val networkLiveData: MutableLiveData<Boolean> = MutableLiveData()

    fun getNetworkLiveData(context: Context): LiveData<Boolean> {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(this)
            Log.d(TAG, "Default network callback registered")
        } else {
            val builder = NetworkRequest.Builder()
            connectivityManager.registerNetworkCallback(builder.build(), this)
            Log.d(TAG, "Network callback registered for older versions")
        }

        updateCurrentNetworkStatus(connectivityManager)

        return networkLiveData
    }

    private fun updateCurrentNetworkStatus(connectivityManager: ConnectivityManager) {
        var isConnected = false
        connectivityManager.allNetworks.forEach { network ->
            val networkCapability = connectivityManager.getNetworkCapabilities(network)
            networkCapability?.let {
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    isConnected = true
                    return@forEach
                }
            }
        }
        networkLiveData.postValue(isConnected)
        Log.d(TAG, "Current network status updated: $isConnected")
    }

    override fun onAvailable(network: Network) {
        Log.d(TAG, "Network available")
        networkLiveData.postValue(true)
    }

    override fun onLost(network: Network) {
        Log.d(TAG, "Network lost")
        networkLiveData.postValue(false)
    }
}

private const val TAG = "NetworkUtils"