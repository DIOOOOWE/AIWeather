package com.ai.weather.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Open-Meteo 天气预报响应
 * 与参考项目的Java POJO不同：使用Kotlin data class + 不可变属性 + 默认值
 */
@Serializable
data class WeatherForecastResponse(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timezone: String = "",
    @SerialName("current_weather") val currentWeather: CurrentWeather? = null,
    val current: CurrentDetails? = null,
    val daily: DailyForecast? = null,
    val hourly: HourlyForecast? = null
)

@Serializable
data class CurrentWeather(
    val temperature: Double = 0.0,
    val windspeed: Double = 0.0,
    val winddirection: Int = 0,
    val weathercode: Int = 0,
    val time: String = ""
)

@Serializable
data class CurrentDetails(
    @SerialName("relative_humidity_2m") val humidity: Int = 0,
    @SerialName("apparent_temperature") val apparentTemperature: Double = 0.0,
    @SerialName("pressure_msl") val pressure: Double = 0.0,
    @SerialName("is_day") val isDay: Int = 1
)

@Serializable
data class DailyForecast(
    val time: List<String> = emptyList(),
    @SerialName("weathercode") val weatherCode: List<Int> = emptyList(),
    @SerialName("temperature_2m_max") val tempMax: List<Double> = emptyList(),
    @SerialName("temperature_2m_min") val tempMin: List<Double> = emptyList(),
    val sunrise: List<String> = emptyList(),
    val sunset: List<String> = emptyList(),
    @SerialName("uv_index_max") val uvIndexMax: List<Double> = emptyList(),
    @SerialName("precipitation_probability_max") val precipProbMax: List<Int> = emptyList()
)

@Serializable
data class HourlyForecast(
    val time: List<String> = emptyList(),
    @SerialName("temperature_2m") val temperature: List<Double> = emptyList(),
    @SerialName("weathercode") val weatherCode: List<Int> = emptyList(),
    @SerialName("relative_humidity_2m") val humidity: List<Int> = emptyList()
)
