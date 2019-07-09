package com.nschirmer.webservice.features.statement

import com.nschirmer.responseobjects.Statement
import com.nschirmer.webservice.requestapi.response.Response
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

internal interface StatementServiceContract {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @GET("statements/{account}/")
    fun getStatements(@Path("account") account: String): Call<Response<ArrayList<Statement>>>

}