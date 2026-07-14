package com.ai.weather.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ai.weather.data.model.Location
import com.ai.weather.data.model.TempUnit
import com.ai.weather.data.repository.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * MVI ViewModel
 * 与参考项目不同：
 * 1. 单一StateFlow（替代多个LiveData）
 * 2. Intent模式（替代直接暴露方法）
 * 3. Channel发送一次性事件
 * 4. 协程+Flow（替代Thread+Handler）
 */
@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WeatherUiState())
    val state: StateFlow<WeatherUiState> = _state.asStateFlow()

    private val _events = Channel<WeatherEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun handleIntent(intent: WeatherIntent) {
        when (intent) {
            is WeatherIntent.SearchCity -> searchCity(intent.name)
            is WeatherIntent.SelectCity -> selectCity(intent.location)
            is WeatherIntent.LoadWeather -> loadWeather(intent.location)
            WeatherIntent.ToggleTempUnit -> toggleUnit()
            WeatherIntent.Refresh -> refresh()
            WeatherIntent.ToggleSearchPanel -> togglePanel()
            WeatherIntent.ClearError -> clearError()
            WeatherIntent.RequestAiAnalysis -> requestAiAnalysis()
        }
    }

    private fun searchCity(name: String) {
        if (name.isBlank()) {
            _events.trySend(WeatherEvent.ShowToast("请输入城市名称"))
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            repository.searchCity(name)
                .onSuccess { results ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            searchResults = results,
                            isSearchPanelOpen = results.isNotEmpty()
                        )
                    }
                    if (results.isEmpty()) {
                        _events.trySend(WeatherEvent.ShowToast("未找到城市：$name"))
                    }
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false) }
                    _events.trySend(WeatherEvent.ShowToast("搜索失败：${e.message}"))
                }
        }
    }

    private fun selectCity(location: Location) {
        _state.update { it.copy(isSearchPanelOpen = false, searchResults = emptyList()) }
        loadWeather(location)
    }

    private fun loadWeather(location: Location) {
        val unit = _state.value.tempUnit
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    aiInsight = null,
                    currentLocation = location
                )
            }
            repository.fetchWeather(location, unit)
                .onSuccess { info ->
                    _state.update { it.copy(isLoading = false, weather = info) }
                    // 自动请求AI分析
                    requestAiAnalysis()
                }
                .onFailure { e ->
                    _state.update { it.copy(isLoading = false, errorMessage = "获取天气失败：${e.message}") }
                }
        }
    }

    private fun toggleUnit() {
        val oldUnit = _state.value.tempUnit
        val newUnit = if (oldUnit == TempUnit.CELSIUS) TempUnit.FAHRENHEIT else TempUnit.CELSIUS
        _state.update { it.copy(tempUnit = newUnit) }

        // 重新加载当前城市以应用新单位
        _state.value.currentLocation?.let { loc ->
            loadWeather(loc)
        }
    }

    private fun refresh() {
        _state.value.currentLocation?.let { loc ->
            loadWeather(loc)
        } ?: run {
            _events.trySend(WeatherEvent.ShowToast("请先选择城市"))
        }
    }

    private fun togglePanel() {
        _state.update { it.copy(isSearchPanelOpen = !it.isSearchPanelOpen) }
    }

    private fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun requestAiAnalysis() {
        val info = _state.value.weather ?: return
        viewModelScope.launch {
            _state.update { it.copy(isAiLoading = true) }
            repository.analyzeWeather(info)
                .onSuccess { insight ->
                    _state.update { it.copy(isAiLoading = false, aiInsight = insight) }
                }
                .onFailure {
                    _state.update { it.copy(isAiLoading = false) }
                }
        }
    }
}
