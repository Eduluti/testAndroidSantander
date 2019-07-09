package com.nschirmer.networkchecker

import android.os.AsyncTask
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


/**
 * Really checks if the device has internet connection.
 * The Android framework tools to check connectivity availability only checks if the device is connected
 * to a network and does not test if there really is internet capabilities.
 *
 * Due to the delay of the test and being a http request, a [AsyncTask] is needed to free up the UiThread.
 *
 * @example how to use

    InternetChecker { canConnect ->
        ... some code ....
    }

 *
 * **/
internal class InternetChecker(private val url: String = BuildConfig.NETWORK_URL_TEST, canConnect: (Boolean) -> Unit) {

    init {
        doAsync {
            try {
                (URL(url).openConnection() as HttpURLConnection).run {
                    setRequestProperty("User-Agent", "Test")
                    setRequestProperty("Connection", "close")
                    connectTimeout = 1500
                    connect()
                    canConnect(responseCode == HttpURLConnection.HTTP_OK)
                }
            } catch (e: IOException) {
                canConnect(false)
            }
        }.execute()
    }


    @Suppress("ClassName")
    private class doAsync(val handler: () -> Unit) : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg params: Void?): Void? {
            handler()
            return null
        }
    }

}