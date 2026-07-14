package com.ai.weather.data.model

/**
 * WMO天气代码映射
 * 与参考项目的if-else链不同，使用when表达式 + Map查找，更符合Kotlin风格
 */
object WeatherCodeMapper {

    private val codeMap = mapOf(
        0 to "晴朗",
        1 to "大部晴朗",
        2 to "局部多云",
        3 to "阴天",
        45 to "雾",
        48 to "冻雾",
        51 to "毛毛雨",
        53 to "毛毛雨",
        55 to "毛毛雨",
        56 to "冻毛毛雨",
        57 to "冻毛毛雨",
        61 to "小雨",
        63 to "中雨",
        65 to "大雨",
        66 to "冻雨",
        67 to "冻雨",
        71 to "小雪",
        73 to "中雪",
        75 to "大雪",
        77 to "雪粒",
        80 to "阵雨",
        81 to "强阵雨",
        82 to "暴雨",
        85 to "阵雪",
        86 to "强阵雪",
        95 to "雷阵雨",
        96 to "雷阵雨伴冰雹",
        99 to "强雷阵雨伴冰雹"
    )

    fun describe(code: Int): String = codeMap[code] ?: "未知"

    /** 天气类别分组 - 用于UI图标与主题色选择 */
    enum class WeatherCategory { SUNNY, CLOUDY, FOGGY, RAINY, SNOWY, THUNDER }

    fun category(code: Int): WeatherCategory = when (code) {
        0, 1 -> WeatherCategory.SUNNY
        2, 3 -> WeatherCategory.CLOUDY
        45, 48 -> WeatherCategory.FOGGY
        in 51..67, in 80..82 -> WeatherCategory.RAINY
        in 71..77, in 85..86 -> WeatherCategory.SNOWY
        in 95..99 -> WeatherCategory.THUNDER
        else -> WeatherCategory.SUNNY
    }

    fun uvDescription(uv: Double): String = when {
        uv < 3 -> "低"
        uv < 6 -> "中等"
        uv < 8 -> "高"
        uv < 11 -> "很高"
        else -> "极高"
    }
}
