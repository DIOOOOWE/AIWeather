package com.ai.weather.data.model

/**
 * 应用领域模型 - UI层使用
 * 与API响应模型分离，体现Clean Architecture分层思想
 */
data class WeatherInfo(
    val cityName: String,
    val region: String = "",
    val country: String = "",
    val temperature: Int,
    val feelsLike: Int,
    val description: String,
    val humidity: Int,
    val windSpeed: Double,
    val windDir: Int = 0,
    val pressure: Double = 0.0,
    val uvIndex: Double = 0.0,
    val uvDescription: String = "",
    val sunrise: String = "",
    val sunset: String = "",
    val precipProbability: Int = 0,
    val weatherCode: Int = 0,
    val isDay: Boolean = true,
    val dailyForecast: List<DailyItem> = emptyList(),
    val hourlyForecast: List<HourlyItem> = emptyList()
)

data class DailyItem(
    val date: String,
    val maxTemp: Int,
    val minTemp: Int,
    val description: String,
    val weatherCode: Int,
    val precipProb: Int
)

data class HourlyItem(
    val time: String,
    val temp: Int,
    val description: String,
    val weatherCode: Int,
    val humidity: Int
)

/**
 * AI生成的智能天气分析
 */
data class AiWeatherInsight(
    val summary: String,        // 自然语言天气摘要
    val clothing: String,       // 穿衣建议
    val activities: String      // 活动建议
)

/**
 * 温度单位枚举 - 类型安全，替代参考项目的boolean isCelsius
 */
enum class TempUnit(val symbol: String, val label: String) {
    CELSIUS("°C", "摄氏度"),
    FAHRENHEIT("°F", "华氏度");

    fun convert(celsius: Int): Int = when (this) {
        CELSIUS -> celsius
        FAHRENHEIT -> celsius * 9 / 5 + 32
    }
}
