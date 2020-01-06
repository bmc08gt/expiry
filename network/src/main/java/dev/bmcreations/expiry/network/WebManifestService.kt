package dev.bmcreations.expiry.network

import dev.bmcreations.expiry.models.WebManifest
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface WebManifestService {

    @GET
    fun loadManifest(@Url url: String): Deferred<Response<WebManifest>>

    @GET
    fun loadJson(@Url url: String): Deferred<Response<WebManifest>>
}
