package com.ai.weather.data.repository

import com.ai.weather.data.local.FavoriteCityDao
import com.ai.weather.data.local.FavoriteCityEntity
import com.ai.weather.data.local.SearchHistoryDao
import com.ai.weather.data.local.SearchHistoryEntity
import com.ai.weather.data.model.AiWeatherInsight
import com.ai.weather.data.model.DailyItem
import com.ai.weather.data.model.GeocodingResult
import com.ai.weather.data.model.HourlyItem
import com.ai.weather.data.model.Location
import com.ai.weather.data.model.TempUnit
import com.ai.weather.data.model.WeatherCodeMapper
import com.ai.weather.data.model.WeatherForecastResponse
import com.ai.weather.data.model.WeatherInfo
import com.ai.weather.data.remote.AiWeatherAnalyzer
import com.ai.weather.data.remote.OpenMeteoApiService
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 天气数据仓库 - 单一数据源
 * 与参考项目不同：
 * 1. 使用Kotlin Coroutines suspend函数（替代同步execute）
 * 2. 错误处理使用Result/sealed class（替代try-catch返回null）
 * 3. 集成AI分析
 * 4. 使用Flow观察本地数据
 */
@Singleton
class WeatherRepository @Inject constructor(
    private val api: OpenMeteoApiService,
    private val analyzer: AiWeatherAnalyzer,
    private val historyDao: SearchHistoryDao,
    private val favoriteDao: FavoriteCityDao
) {

    /** 搜索城市 */
    suspend fun searchCity(name: String): Result<List<Location>> = try {
        val result: GeocodingResult = api.searchCity(name)
        Result.success(result.results.orEmpty())
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** 获取天气并转换为UI模型 */
    suspend fun fetchWeather(location: Location, unit: TempUnit): Result<WeatherInfo> = try {
        val response: WeatherForecastResponse = api.getWeather(
            location.latitude, location.longitude
        )
        val info = mapToWeatherInfo(location, response, unit)
        // 保存搜索历史
        historyDao.insert(
            SearchHistoryEntity(
                cityName = location.name,
                region = location.region,
                country = location.country,
                latitude = location.latitude,
                longitude = location.longitude
            )
        )
        Result.success(info)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** AI天气分析 */
    suspend fun analyzeWeather(info: WeatherInfo): Result<AiWeatherInsight> = try {
        Result.success(analyzer.analyze(info))
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** 观察搜索历史 */
    fun observeHistory(): Flow<List<SearchHistoryEntity>> = historyDao.observeAll()

    /** 清空搜索历史 */
    suspend fun clearHistory() = historyDao.clear()

    /** 观察收藏城市 */
    fun observeFavorites(): Flow<List<FavoriteCityEntity>> = favoriteDao.observeAll()

    /** 收藏/取消收藏 */
    suspend fun toggleFavorite(location: Location, isFavorite: Boolean) {
        if (isFavorite) {
            favoriteDao.remove(location.name)
        } else {
            favoriteDao.add(
                FavoriteCityEntity(
                    cityName = location.name,
                    region = location.region,
                    country = location.country,
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }
    }

    fun observeFavorite(cityName: String): Flow<Boolean> = favoriteDao.isFavorite(cityName)

    /** API响应 -> UI模型 */
    private fun mapToWeatherInfo(
        location: Location,
        response: WeatherForecastResponse,
        unit: TempUnit
    ): WeatherInfo {
        val current = response.currentWeather
        val details = response.current
        val daily = response.daily
        val hourly = response.hourly

        val celsius = current?.temperature?.toInt() ?: 0
        val temp = unit.convert(celsius)

        val feelCelsius = details?.apparentTemperature?.toInt() ?: celsius + 2
        val feelsLike = unit.convert(feelCelsius)

        val code = current?.weathercode ?: 0
        val description = WeatherCodeMapper.describe(code)
        val uvMax = daily?.uvIndexMax?.firstOrNull() ?: 0.0

        val dailyItems = daily?.let { d ->
            d.time.indices.map { i ->
                DailyItem(
                    date = d.time.getOrNull(i).orEmpty(),
                    maxTemp = unit.convert(d.tempMax.getOrElse(i) { 0.0 }.toInt()),
                    minTemp = unit.convert(d.tempMin.getOrElse(i) { 0.0 }.toInt()),
                    description = WeatherCodeMapper.describe(d.weatherCode.getOrElse(i) { 0 }),
                    weatherCode = d.weatherCode.getOrElse(i) { 0 },
                    precipProb = d.precipProbMax.getOrElse(i) { 0 }
                )
            }
        }.orEmpty()

        // 取未来24小时
        val hourlyItems = hourly?.let { h ->
            val now = System.currentTimeMillis()
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            h.time.indices.mapNotNull { i ->
                val timeStr = h.time.getOrNull(i).orEmpty()
                val date = runCatching { sdf.parse(timeStr) }.getOrNull() ?: return@mapNotNull null
                if (date.time >= now) {
                    HourlyItem(
                        time = timeStr.substring(11, 16),
                        temp = unit.convert(h.temperature.getOrElse(i) { 0.0 }.toInt()),
                        description = WeatherCodeMapper.describe(h.weatherCode.getOrElse(i) { 0 }),
                        weatherCode = h.weatherCode.getOrElse(i) { 0 },
                        humidity = h.humidity.getOrElse(i) { 0 }
                    )
                } else null
            }.take(24)
        }.orEmpty()

        return WeatherInfo(
            cityName = location.name,
            region = location.region,
            country = location.country,
            temperature = temp,
            feelsLike = feelsLike,
            description = description,
            humidity = details?.humidity ?: 60,
            windSpeed = current?.windspeed ?: 0.0,
            windDir = current?.winddirection ?: 0,
            pressure = details?.pressure ?: 1013.0,
            uvIndex = uvMax,
            uvDescription = WeatherCodeMapper.uvDescription(uvMax),
            sunrise = daily?.sunrise?.firstOrNull()?.let { formatTime(it) }.orEmpty(),
            sunset = daily?.sunset?.firstOrNull()?.let { formatTime(it) }.orEmpty(),
            precipProbability = daily?.precipProbMax?.firstOrNull() ?: 0,
            weatherCode = code,
            isDay = (details?.isDay ?: 1) == 1,
            dailyForecast = dailyItems,
            hourlyForecast = hourlyItems
        )
    }

    private fun formatTime(isoTime: String): String =
        if (isoTime.length >= 16) isoTime.substring(11, 16) else "00:00"
}
