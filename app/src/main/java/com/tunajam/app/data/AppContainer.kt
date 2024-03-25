package com.tunajam.app.data

import com.tunajam.app.network.TunaJamApiService
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
interface AppContainer {
    val tunaJamRepository: TunaJamPhotoRepository
}

/**
 * Implementation for the Dependency Injection container at the application level.
 *
 * Variables are initialized lazily and the same instance is shared across the whole app.
 */
class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://android-kotlin-fun-mars-server.appspot.com/" //ACHANGER

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: TunaJamApiService by lazy {
        retrofit.create(TunaJamApiService::class.java)
    }

    /**
     * DI implementation for Mars photos repository
     */
    override val tunaJamRepository: TunaJamPhotoRepository by lazy {
        NetworkTunaJamPhotosRepository(retrofitService)
    }
}