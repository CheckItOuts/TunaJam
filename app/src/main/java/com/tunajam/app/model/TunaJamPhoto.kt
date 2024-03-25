package com.tunajam.app.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * This data class defines a Mars photo which includes an ID, and the image URL.
 */
@Serializable
data class TunaJamPhoto(
    val id: String,
    @SerialName(value = "img_src")
    val imgSrc: String
)