package com.nschirmer.webservice.features.login

import com.nschirmer.responseobjects.UserAccount
import com.nschirmer.webservice.requestapi.response.Response
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

internal interface LoginServiceContract {

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("login/")
    fun getAccountInfo(
        @Field("user") username: String,
        @Field("password") password: String): Call<Response<UserAccount>>

}