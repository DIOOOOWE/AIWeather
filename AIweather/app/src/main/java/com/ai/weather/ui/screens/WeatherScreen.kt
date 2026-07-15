package com.ai.weather.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ai.weather.ui.components.AiInsightCard
import com.ai.weather.ui.components.DailyForecastList
import com.ai.weather.ui.components.HourlyForecastRow
import com.ai.weather.ui.components.MainWeatherCard
import com.ai.weather.ui.components.SearchResultsPanel
import com.ai.weather.ui.components.WeatherDetailsGrid
import com.ai.weather.ui.state.WeatherEvent
import com.ai.weather.ui.state.WeatherIntent
import com.ai.weather.ui.state.WeatherViewModel
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }

    // 处理一次性事件
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is WeatherEvent.ShowToast -> android.widget.Toast
                    .makeText(context, event.message, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI 天气预报",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // 温度单位切换
                    IconButton(onClick = { viewModel.handleIntent(WeatherIntent.ToggleTempUnit) }) {
                        Icon(
                            imageVector = Icons.Filled.Thermostat,
                            contentDescription = "切换温度单位"
                        )
                    }
                    // 刷新
                    IconButton(onClick = { viewModel.handleIntent(WeatherIntent.Refresh) }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "刷新")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.handleIntent(WeatherIntent.RequestAiAnalysis) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Text("AI", fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // 搜索框
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("输入城市名称") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = {
                        viewModel.handleIntent(WeatherIntent.SearchCity(query))
                    }) {
                        Icon(Icons.Filled.Search, contentDescription = "搜索")
                    }
                },
                singleLine = true
            )

            // 搜索结果面板
            if (state.isSearchPanelOpen) {
                SearchResultsPanel(
                    results = state.searchResults,
                    onSelect = { loc ->
                        viewModel.handleIntent(WeatherIntent.SelectCity(loc))
                    }
                )
            }

            // 加载中
            if (state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // 错误信息
            state.errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // 天气主卡片
            state.weather?.let { info ->
                MainWeatherCard(info = info, unitSymbol = state.tempUnit.symbol)

                // AI分析卡片
                Spacer(Modifier.height(4.dp))
                AiInsightCard(insight = state.aiInsight, isLoading = state.isAiLoading)

                // 详细信息
                WeatherDetailsGrid(info)

                // 24小时预报
                HourlyForecastRow(info.hourlyForecast)

                // 7日预报
                DailyForecastList(info.dailyForecast)
            }

            // 空状态
            if (state.weather == null && !state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "搜索城市开始查看天气",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}
