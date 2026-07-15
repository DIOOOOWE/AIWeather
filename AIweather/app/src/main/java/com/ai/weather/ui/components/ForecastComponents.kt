package com.ai.weather.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ai.weather.data.model.AiWeatherInsight
import com.ai.weather.data.model.DailyItem
import com.ai.weather.data.model.HourlyItem
import com.ai.weather.data.model.WeatherCodeMapper

/**
 * AI天气分析卡片 - 核心差异化功能
 */
@Composable
fun AiInsightCard(insight: AiWeatherInsight?, isLoading: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "AI 天气分析",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(Modifier.height(8.dp))
            when {
                isLoading -> {
                    Text("AI 正在分析天气...", style = MaterialTheme.typography.bodyLarge)
                }
                insight != null -> {
                    if (insight.summary.isNotBlank()) {
                        Text(insight.summary, style = MaterialTheme.typography.bodyLarge)
                    }
                    if (insight.clothing.isNotBlank()) {
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text("穿衣：", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(insight.clothing, fontSize = 14.sp)
                        }
                    }
                    if (insight.activities.isNotBlank()) {
                        Spacer(Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Top) {
                            Text("活动：", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            Text(insight.activities, fontSize = 14.sp)
                        }
                    }
                }
                else -> {
                    Text("点击下方按钮获取AI天气分析", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

/**
 * 24小时预报横向列表
 */
@Composable
fun HourlyForecastRow(hours: List<HourlyItem>) {
    if (hours.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "逐小时预报",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(hours) { hour ->
                    HourlyItemView(hour)
                }
            }
        }
    }
}

@Composable
private fun HourlyItemView(hour: HourlyItem) {
    Column(
        modifier = Modifier.width(56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(hour.time, fontSize = 12.sp)
        Text(hour.temp.toString() + "°", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Text(
            WeatherCodeMapper.category(hour.weatherCode).name,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

/**
 * 7日预报列表
 */
@Composable
fun DailyForecastList(days: List<DailyItem>) {
    if (days.isEmpty()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "7日预报",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            days.forEach { day ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = day.date.takeLast(5),
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = day.description,
                        modifier = Modifier.weight(1.5f),
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "${day.minTemp}° / ${day.maxTemp}°",
                        modifier = Modifier.weight(1f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
