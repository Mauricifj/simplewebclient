package com.mauricifj.simplewebclient.network

import com.mauricifj.simplewebclient.extensions.HttpStatusCode

data class ClientResult<T>(
    val result: T?,
    val statusCode: HttpStatusCode,
    val errorMessage: String? = null
)