package com.nschirmer.webservice

import android.content.Context
import android.net.ConnectivityManager
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit





internal class RequestClient(private val context: Context, private val baseURL: String, private val withCache: Boolean = true) {

    /** Retrofit with GSON converter **/
    val retrofit: Retrofit by lazy {
        Retrofit.Builder().let {
            it.baseUrl(baseURL)
            it.addConverterFactory(GsonConverterFactory.create())
            if(withCache) it.client(getCachedOkHttpClient())
            it.build()
        }
    }


    /** @return OkHttpClient with cache enabled **/
    private fun getCachedOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(Cache(context.cacheDir, getCacheSize()))
            .addInterceptor { chain ->
                chain.request().let {
                    it.newBuilder().header("Cache-Control", getRequestValueCache()).build()
                    chain.proceed(it)
                }
            }
            .build()
    }


    /**
     * @return If there is Internet, get the cache that was stored  getNormalCacheTime() seconds ago.
     * If there is no Internet, get the cache that was stored [noInternetCacheTime] minutes ago.
     * **/
    private fun getRequestValueCache(): String {
        return "public, " + when {
            hasNetwork() -> {
                "max-age=" + getNormalCacheTime()

            } else -> {
                "only-if-cached, max-stale=" + getNoInternetCacheTime()
            }
        }
    }


    /** @return cache size in megabytes to retain objects **/
    private fun getCacheSize(): Long {
        return (1024 * 1024 * 5).toLong() // megabytes
    }


    /** @return time in minutes to retain objects in cache if there is no connection **/
    private fun getNormalCacheTime(): Long {
        return  TimeUnit.MINUTES.toSeconds(2) // minutes
    }


    /** @return time in seconds to retain objects in cache if there is connection **/
    private fun getNoInternetCacheTime(): Long {
        return 20 // seconds
    }


    /** @return true if is connected to a network (doesn't mean necessarily that the device has internet connection) **/
    private fun hasNetwork(): Boolean {
       return  (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
            val activeNetwork = activeNetworkInfo
            activeNetwork != null && activeNetwork.isConnectedOrConnecting
        }
    }

}