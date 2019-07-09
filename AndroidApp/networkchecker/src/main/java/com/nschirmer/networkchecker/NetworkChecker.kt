package com.nschirmer.networkchecker

import android.content.Context
import android.telephony.TelephonyManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


/**
 *  Listener to check when the internet test has finished @see [InternetChecker]
 *
 * @param canConnectToInternet return if there is internet connection
 * @param networkType return the connection type through [NetworkType]
 *  **/
typealias NetworkCheckerListener = (canConnectToInternet: Boolean, networkType: NetworkType) -> Unit


/**
 * Check the network type and if really has internet connection @see [InternetChecker]
 *
 * @example

    NetworkChecker(context){ canConnectToInternet, networkType ->
            ... some code ....
    }

 * **/
class NetworkChecker(private val context: Context, private val networkCheckerListener: NetworkCheckerListener) {

    init {
        getNetworkType()
    }


    /** Wait until the internet check test is done and check the connection type **/
    private fun getNetworkType() {
        InternetChecker { hasInternet ->
            val networkType: NetworkType = getTelephonyManager().networkType.run {
                when {
                    ! hasInternet -> NetworkType.NOT_CONNECTED
                    isNetworkWifi() -> NetworkType.WIFI
                    isNetwork2G(this) -> NetworkType.MOBILE_2G
                    isNetwork3G(this)-> NetworkType.MOBILE_3G
                    isNetwork4G(this) -> NetworkType.MOBILE_4G
                    else -> NetworkType.OTHER
                }
            }
            networkCheckerListener(hasInternet, networkType)
        }
    }


    /**
     *  @return If the connected network is 2G connection.
     *  Speed: 14-100 kbps
     *  **/
    private fun isNetwork2G(networkState: Int): Boolean{
        return when (networkState) {
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> true
            else -> false
        }
    }


    /**
     *  @return If the connected network is 3G connection.
     *  Speed: 400 kbps - 20 Mbps
     *  **/
    private fun isNetwork3G(networkState: Int): Boolean{
        return when (networkState) {
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP -> true
            else -> false
        }
    }


    /**
     *  @return If the connected network is 4G connection.
     *  Speed: 10+ Mbps
     *  **/
    private fun isNetwork4G(networkState: Int): Boolean{
        return when (networkState) {
            TelephonyManager.NETWORK_TYPE_LTE -> true
            else -> false
        }
    }


    /**
     *  @return If the deivce is connected to a WiFi connection.
     * This only test if the device is connected to an access point and does not check if there is
     * a real internet connection.
     * **/
    private fun isNetworkWifi(): Boolean {
        getConnectivityManager().run {
            if (this != null) {
                when {
                    android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M -> {
                        val capabilities = this.getNetworkCapabilities(this.activeNetwork) ?: return false
                        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    }

                    else -> {
                        // This is sad, but Google deprecated the NetworkInfo without giving any alternatives
                        // to check if is in a WIFI connection on devices running API < 23
                        val networkInfo = this.getNetworkInfo(ConnectivityManager.TYPE_WIFI) ?: return false
                        return networkInfo.isConnected
                    }
                }
            } else {
                return false
            }
        }
    }


    /** @return a fresh [ConnectivityManager] status **/
    private fun getConnectivityManager(): ConnectivityManager? =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?


    /** @return a fresh [TelephonyManager] status **/
    private fun getTelephonyManager(): TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


}