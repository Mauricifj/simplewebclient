# SimpleWebClient [![Status](https://travis-ci.com/Mauricifj/simplewebclient.svg?branch=master)](https://travis-ci.com/Mauricifj/simplewebclient) [![Download](https://api.bintray.com/packages/mauricifj/simplewebclient/simplewebclient/images/download.svg)](https://bintray.com/mauricifj/simplewebclient/simplewebclient/_latestVersion)

**This project is a library with transitive dependencies and a sample app to test travis ci + bintray publication process.**


## Setup

### Dependency

Add this dependency to your module level build.gradle in dependencies node

```kotlin
dependencies {
    ...
    implementation 'com.mauricifj:simplewebclient:1.0.0'
}
```

> Don't forget to check if you have **jcenter()** as a repository.

### Internet Permission

Add this permission to your app module level **AndroidManifest.xml**

```kotlin
    <uses-permission android:name="android.permission.INTERNET" />
```

## Usage

### Mapping responses

You need to map the responses.

If your api returns this JSON:

```json
{
  "key1": "value",
  "key2": 10,
  "keyWithAHardName": "CanBeMappedToADifferentVariable"
}
```

It should be mapped as:

```kotlin
// In case you want to have different names from JSON
import com.google.gson.annotations.SerializedName

data class Result(
    val key1: string,
    val key2: Int,
    @SerializedName("keyWithAHardName")
    val differentNameFromJson: string
)
```

> Check sample app for more information.

### Interface API

Create an interface to map endpoint resources.

Result will be your mapped class.

```kotlin
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface SampleApi {
    @GET("api/")
    fun get(): Call<Result>
    
    @POST("api/")
    fun post(): Call<Result>
}
```

### Client

Create a client to consume the api.

ClientResult model makes it easier to return mapped object, status code and an error message if something doesn't work.

```kotlin
import com.mauricifj.simplewebclient.extensions.HttpStatusCode
import com.mauricifj.simplewebclient.extensions.toStatusCode
import com.mauricifj.simplewebclient.network.ClientResult
import com.mauricifj.simplewebclient.network.WebClient

class SampleClient {
    fun get(): ClientResult<Result> {

        return try {

            val webClient = WebClient("https://sample.endpoint/")
            val call = webClient.createService(SampleApi::class.java).get()

            val response = call.execute()

            when (response.isSuccessful) {
                true -> ClientResult(
                    response.body(),
                    response.code().toStatusCode()
                )
                false -> ClientResult(
                    null,
                    response.code().toStatusCode(),
                    response.errorBody()?.string()
                )
            }

        } catch (ex: Throwable) {
            ClientResult(
                null,
                HttpStatusCode.Unknown,
                ex.localizedMessage
            )
        }
    }
}
```

### Making it work

Next step is to call your service from a different thread, not on Main Thread.

In this example, I'm using [coroutines](https://developer.android.com/kotlin/coroutines) to make this call.

```kotlin
val request = withContext(Dispatchers.IO) {
    async {
        val service = SampleClient()
        service.get()
    }
}

val response = request.await()
val result = response.result
```