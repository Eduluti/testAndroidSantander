package com.nschirmer.webservice.features.statement

import android.content.Context
import com.nschirmer.responseobjects.Statement
import com.nschirmer.webservice.requestapi.BaseService
import com.nschirmer.webservice.requestapi.ConnectionListener


class StatementService(private val context: Context) {

    private val serviceApi by lazy {
        BaseService(context, StatementServiceContract::class.java)
    }


    /**
     * @param accountID is the user ID given by the [UserAccount]
     * @param connectionListener is the request status @see [ConnectionListener]
     *
     * @return [ArrayList] of [Statement] through the [connectionListener], a error or no internet status
     * **/
    fun getStatements(accountID: String, connectionListener: ConnectionListener<ArrayList<Statement>>){
        serviceApi.callServerApi(serviceApi.clientApi.getStatements(accountID), connectionListener)
    }

}