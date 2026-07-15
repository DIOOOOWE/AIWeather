package com.ai.weather.data.remote

import com.ai.weather.data.model.DeepSeekRequest
import com.ai.weather.data.model.DeepSeekResponse
import com.ai.weather.data.model.GeocodingResult
import com.ai.weather.data.model.WeatherForecastResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.URLBuilder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Open-Meteo API 服务
 * 与参考项目的Retrofit接口不同：使用Ktor DSL + suspend函数 + URLBuilder
 */
@Singleton
class OpenMeteoApiService @Inject constructor(
    private val client: HttpClient
) {
    companion object {
        private const val GEO_BASE = "https://geocoding-api.open-meteo.com"
        private const val WEATHER_BASE = "https://api.open-meteo.com"
    }

    suspend fun searchCity(name: String): GeocodingResult = client.get("$GEO_BASE/v1/search") {
        parameter("name", name)
        parameter("count", 10)
        parameter("language", "zh")
        parameter("format", "json")
    }.body()

    suspend fun getWeather(lat: Double, lon: Double): WeatherForecastResponse {
        val url = URLBuilder("$WEATHER_BASE/v1/forecast").apply {
            parameters.append("latitude", lat.toString())
            parameters.append("longitude", lon.toString())
            // 当前天气 + 扩展字段
            parameters.append("current", "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,pressure_msl")
            parameters.append("current_weather", "true")
            // 每小时（24小时）
            parameters.append("hourly", "temperature_2m,weathercode,relative_humidity_2m")
            // 每日（7天）
            parameters.append("daily", "weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_probability_max")
            parameters.append("timezone", "Asia/Shanghai")
            parameters.append("forecast_days", "7")
        }.buildString()

        return client.get(url).body()
    }
}

/**
 * DeepSeek AI 服务 - 生成自然语言天气洞察
 */
@Singleton
class DeepSeekApiService @Inject constructor(
    private val client: HttpClient
) {
    suspend fun chat(request: DeepSeekRequest): DeepSeekResponse = client.post(
        "https://api.deepseek.com/chat/completions"
    ) {
        contentType(ContentType.Application.Json)
        setBody(request)
    }.body()
}
