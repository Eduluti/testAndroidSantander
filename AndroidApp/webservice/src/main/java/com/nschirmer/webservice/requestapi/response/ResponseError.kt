package com.nschirmer.webservice.requestapi.response

import java.io.Serializable


internal class ResponseError (val code: Int?, val message: String?): Serializable {

    override fun toString(): String {
        return code?.toString() + "\n" + message
    }

}