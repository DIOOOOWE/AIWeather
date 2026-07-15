package com.ai.weather.ui.state

import com.ai.weather.data.model.AiWeatherInsight
import com.ai.weather.data.model.Location
import com.ai.weather.data.model.TempUnit
import com.ai.weather.data.model.WeatherInfo

/**
 * MVI - 单一数据源状态
 * 与参考项目的多个LiveData不同：使用单个不可变UiState
 */
data class WeatherUiState(
    val isLoading: Boolean = false,
    val isAiLoading: Boolean = false,
    val weather: WeatherInfo? = null,
    val aiInsight: AiWeatherInsight? = null,
    val searchResults: List<Location> = emptyList(),
    val tempUnit: TempUnit = TempUnit.CELSIUS,
    val errorMessage: String? = null,
    val isSearchPanelOpen: Boolean = false,
    val currentLocation: Location? = null
)

/**
 * 用户意图 - 封装所有可能的用户操作
 */
sealed interface WeatherIntent {
    data class SearchCity(val name: String) : WeatherIntent
    data class SelectCity(val location: Location) : WeatherIntent
    data class LoadWeather(val location: Location) : WeatherIntent
    object ToggleTempUnit : WeatherIntent
    object Refresh : WeatherIntent
    object ToggleSearchPanel : WeatherIntent
    object ClearError : WeatherIntent
    object RequestAiAnalysis : WeatherIntent
}

/**
 * 一次性事件（如Toast）
 */
sealed interface WeatherEvent {
    data class ShowToast(val message: String) : WeatherEvent
}
