package com.ai.weather.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Open-Meteo 地理编码响应
 */
@Serializable
data class GeocodingResult(
    @SerialName("results") val results: List<Location>? = null
)

@Serializable
data class Location(
    val id: Long = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val country: String = "",
    @SerialName("admin1") val region: String = ""
)
