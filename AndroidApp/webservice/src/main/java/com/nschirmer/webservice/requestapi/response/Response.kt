package com.nschirmer.webservice.requestapi.response

import java.io.Serializable


internal class Response<T> (val error: ResponseError?, val data: T?) : Serializable