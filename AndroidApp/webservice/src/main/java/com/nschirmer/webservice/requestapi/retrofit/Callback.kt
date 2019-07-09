package com.nschirmer.webservice.requestapi.retrofit

import android.content.Context
import com.nschirmer.webservice.requestapi.ConnectionListener
import com.nschirmer.webservice.requestapi.response.Response
import retrofit2.Call
import retrofit2.Callback


/**
 * Custom service callback receiving [Response] as object and check if there is an error.
 *
 * Return all objects and status through [callback] @see [ConnectionListener].
 *
 * WARNING: If the service response changes the [Response] structure, this class will not work and you will always get [ConnectionListener.onFail] status.
 *
 * [R] is the [Response] object received on the [retrofit2.Callback].
 * [T] is the object type that is expected in the [retrofit2.Response].
 * **/
internal class Callback <T, R> (private val context: Context, private val callback: ConnectionListener<T>): Callback<R> {


    private val castError: String by lazy {
        context.getString(com.nschirmer.webservice.R.string.cast_error)
    }

    private val failError: String by lazy {
        context.getString(com.nschirmer.webservice.R.string.not_able_to_connect_error)
    }


    /**
     *  Response from the server.
     *
     *  Check if the [retrofit2.Response] came with http success and no null [retrofit2.Response.body].
     *  If a error will be found, it will send the to string of [Response.error].
     *  If there is an cast error, it will send [ConnectionListener.onFail] status.
     *  **/
    override fun onResponse(call: Call<R>, response: retrofit2.Response<R>) {
        return when {
            responseIsSuccessful(response) -> processResponseData(response)
            else -> callback.onFail(getErrorResponse(cast(response)))
        }
    }


    /** It will trigger this if there is any server communication error or an [ChangeObjectNameInterceptor] error. **/
    override fun onFailure(call: Call<R>, t: Throwable) {
        callback.onFail(failError)
    }


    /**
     * Check if the [retrofit2.Response] was successful and
     * check if the [Response.error] has any null variables, indicating there is no error given by the server.
     * **/
    private fun responseIsSuccessful(response: retrofit2.Response<R>): Boolean {
        return response.isSuccessful && when {
            response.body() == null -> false
            else -> {
                val responseCasted: retrofit2.Response<Response<T>>? = cast(response)
                responseCasted?.body()?.error?.code == null
            }
        }
    }


    /** Try to cast the [Response.data] object. **/
    private fun processResponseData(response: retrofit2.Response<R>){
        getDataResponse(cast(response.body())).run{
            when {
                this != null -> callback.onSuccess(this)
                else -> callback.onFail(castError)
            }
        }
    }


    private fun getDataResponse(response: Response<T>?): T? {
        return response?.data
    }


    /** @return the code and message from the [Response.error] if there is no casting errors. **/
    private fun getErrorResponse(response: retrofit2.Response<T>?): String? {
        getResponseCasted(response)?.body()?.error.run {
            return when {
                this != null -> this.toString()
                else -> castError
            }
        }
    }


    private inline fun <reified T> cast(from: Any?): T? = from as? T

    private fun getResponseCasted(response: retrofit2.Response<T>?): retrofit2.Response<Response<T>>? = cast(response)

}