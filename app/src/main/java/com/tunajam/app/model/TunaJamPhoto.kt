package com.tunajam.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This data class defines an external photo which includes an ID, and the image URL.
 */
@Serializable
data class TunaJamPhoto(
    val id: String,
    @SerialName(value = "img_src")
    val imgSrc: String
)