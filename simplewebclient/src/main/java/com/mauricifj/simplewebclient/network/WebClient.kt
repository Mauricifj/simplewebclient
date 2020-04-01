package com.mauricifj.simplewebclient.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Create a webclient from a url string.
 *
 * @param [apiUrl] url string of the endpoint
 **/
class WebClient(apiUrl: String) {

    private val retrofit = Builder()
        .baseUrl(apiUrl)
        .addConverterFactory(GsonConverterFactory.create())

    private val httpClient = OkHttpClient().newBuilder()

    /**
     * Creates a service from an interface
     *
     * @param [service] interface api
     * @return interface api as a service from webclient
     */
    fun <T> createService(service: Class<T>): T {

        val client = httpClient
            .connectTimeout(45, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .writeTimeout(45, TimeUnit.SECONDS)
            .build()

        return retrofit
            .client(client)
            .build()
            .create(service)
    }
}