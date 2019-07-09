package com.nschirmer.webservice.features.login

import android.content.Context
import com.nschirmer.responseobjects.UserAccount
import com.nschirmer.webservice.requestapi.BaseService
import com.nschirmer.webservice.requestapi.ConnectionListener


class  LoginService(private val context: Context) {

    private val serviceApi by lazy {
        BaseService(context, LoginServiceContract::class.java)
    }


    /**
     * @param user is the username (CPF/email) that need to be validated before handing it to the service by the Validatior library.
     * @param password need to be validated before handing it to the service by the Validatior library.
     * @param connectionListener is the request status @see [ConnectionListener]
     *
     * @return [UserAccount] through the [connectionListener], a error or no internet status
     * **/
    fun getAccountInfo(user: String, password: String, connectionListener: ConnectionListener<UserAccount>){
        serviceApi.callServerApi(serviceApi.clientApi.getAccountInfo(user, password), connectionListener)
    }

}