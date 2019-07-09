package com.nschirmer.webservice.requestapi.retrofit

import okhttp3.OkHttpClient


internal class CustomOkHttpClient {

    /** @return OkHttpClient with [ChangeObjectNameInterceptor] **/
    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().let { clientBuilder ->
            clientBuilder.addInterceptor(ChangeObjectNameInterceptor())
            clientBuilder.build()
        }
    }

}