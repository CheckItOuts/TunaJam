package com.tunajam.app.network

import com.tunajam.app.model.TunaJamPhoto
import retrofit2.http.GET

interface TunaJamApiService {
    /**
     * Returns a [List] of [PlaylistPhoto] and this method can be called from a Coroutine.
     * The @GET annotation indicates that the "photos" endpoint will be requested with the GET
     * HTTP method
     */
    @GET("photos")
    suspend fun getPhotos(): List<TunaJamPhoto>
}