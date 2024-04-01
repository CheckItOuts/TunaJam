package com.tunajam.app.data


import com.tunajam.app.model.TunaJamPhoto
import com.tunajam.app.network.TunaJamApiService

/**
 * Repository that fetch mars photos list from tunaJamApi.
 */
interface TunaJamPhotoRepository {
    /** Fetches list of TunaJam photos from tunaJamApi */
    suspend fun getTunaJamPhotos(): List<TunaJamPhoto>
}

/**
 * Network Implementation of Repository that fetch tunajam photos list from tunaJamApi.
 */
class NetworkTunaJamPhotosRepository(
    private val tunaJamApiService: TunaJamApiService
) : TunaJamPhotoRepository {
    /** Fetches list of TunaJamPhoto from tunaJamApi*/
    override suspend fun getTunaJamPhotos(): List<TunaJamPhoto> = tunaJamApiService.getPhotos()
}
