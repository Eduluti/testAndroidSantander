package com.nschirmer.networkchecker

import android.content.Context
import com.nschirmer.networkchecker.BuildConfig.NETWORK_URL_TEST
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import android.telephony.TelephonyManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.InetAddress


/** TODO **/
class NetworkChecker(private val context: Context) {


    fun hasInternetConnection(): Boolean {
        return getNetworkType() != NetworkType.NOT_CONNECTED
    }


    fun getNetworkType(): NetworkType {
        getTelephonyManager().networkType.run {
            return when {
                ! canConnectToExternal() -> NetworkType.NOT_CONNECTED
                isNetworkWifi() -> NetworkType.WIFI
                isNetwork2G(this) -> NetworkType.MOBILE_2G
                isNetwork3G(this)-> NetworkType.MOBILE_3G
                isNetwork4G(this) -> NetworkType.MOBILE_4G
                else -> NetworkType.OTHER
            }
        }
    }


    fun canConnectToExternal(url: String = NETWORK_URL_TEST): Boolean {
        try {
            (URL(url).openConnection() as HttpURLConnection).run {
                setRequestProperty("User-Agent", "Test")
                setRequestProperty("Connection", "close")
                connectTimeout = 1500
                connect()
                return responseCode == HttpURLConnection.HTTP_OK
            }

        } catch (e: IOException) {
            return false
        }
    }


    private fun isNetwork2G(networkState: Int): Boolean{
        return when (networkState) {
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> true
            else -> false
        }
    }


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


    private fun isNetwork4G(networkState: Int): Boolean{
        return when (networkState) {
            TelephonyManager.NETWORK_TYPE_LTE -> true
            else -> false
        }
    }


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


    private fun getConnectivityManager(): ConnectivityManager? =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?


    private fun getTelephonyManager(): TelephonyManager =
        context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager


}