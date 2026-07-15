package com.ai.weather

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.weather.ui.screens.WeatherScreen
import com.ai.weather.ui.state.WeatherViewModel
import com.ai.weather.ui.theme.AIWeatherTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: WeatherViewModel = hiltViewModel()
            val state by viewModel.state.collectAsState()
            // 根据天气状态动态切换主题
            AIWeatherTheme(
                weatherCode = state.weather?.weatherCode ?: 0,
                isDay = state.weather?.isDay ?: true
            ) {
                WeatherScreen(viewModel = viewModel)
            }
        }
    }
}
