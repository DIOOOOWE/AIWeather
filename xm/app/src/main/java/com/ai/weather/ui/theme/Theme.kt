package com.ai.weather.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.ai.weather.data.model.WeatherCodeMapper

/**
 * 根据天气状态动态切换主题色
 * 与参考项目的固定XML主题不同：使用动态天气主题
 */
private val SunnyLightScheme = lightColorScheme(
    primary = SunnyPrimary,
    background = SunnyBackground,
    surface = SunnySurface,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val CloudyLightScheme = lightColorScheme(
    primary = CloudyPrimary,
    background = CloudyBackground,
    surface = CloudySurface,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val RainyLightScheme = lightColorScheme(
    primary = RainyPrimary,
    background = RainyBackground,
    surface = RainySurface,
    onPrimary = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val NightScheme = darkColorScheme(
    primary = NightPrimary,
    background = NightBackground,
    surface = NightSurface,
    onPrimary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AIWeatherTheme(
    weatherCode: Int = 0,
    isDay: Boolean = true,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        !isDay -> NightScheme
        else -> when (WeatherCodeMapper.category(weatherCode)) {
            WeatherCodeMapper.WeatherCategory.SUNNY -> SunnyLightScheme
            WeatherCodeMapper.WeatherCategory.CLOUDY -> CloudyLightScheme
            WeatherCodeMapper.WeatherCategory.FOGGY -> CloudyLightScheme
            WeatherCodeMapper.WeatherCategory.RAINY -> RainyLightScheme
            WeatherCodeMapper.WeatherCategory.SNOWY -> CloudyLightScheme
            WeatherCodeMapper.WeatherCategory.THUNDER -> RainyLightScheme
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = isDay
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
