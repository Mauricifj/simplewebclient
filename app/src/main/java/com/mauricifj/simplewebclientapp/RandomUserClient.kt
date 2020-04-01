package com.mauricifj.simplewebclientapp

import com.mauricifj.simplelogger.SimpleLogger
import com.mauricifj.simplewebclient.extensions.HttpStatusCode
import com.mauricifj.simplewebclient.extensions.toStatusCode
import com.mauricifj.simplewebclient.network.ClientResult
import com.mauricifj.simplewebclient.network.WebClient

class RandomUserClient() {
    fun getRandomUser(): ClientResult<User> {

        return try {

            val webClient = WebClient("https://randomuser.me/")
            val call = webClient.createService(RandomUserApi::class.java).getRandomUser()

            val response = call.execute()

            when (response.isSuccessful) {
                true -> ClientResult(
                    response.body()?.results?.first(),
                    response.code().toStatusCode()
                )
                false -> ClientResult(
                    null,
                    response.code().toStatusCode(),
                    response.errorBody()?.string()
                )
            }

        } catch (ex: Throwable) {
            SimpleLogger.error("Error getting random user", ex)
            ClientResult(
                null,
                HttpStatusCode.Unknown,
                ex.localizedMessage
            )
        }
    }
}