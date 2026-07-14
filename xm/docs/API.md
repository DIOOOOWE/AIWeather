# API 文档

本项目使用两个外部 API 服务：Open-Meteo（天气数据）和 DeepSeek（AI 分析）。

## 1. Open-Meteo 地理编码 API

用于将城市名称转换为地理坐标（经纬度）。

### 端点

```
GET https://geocoding-api.open-meteo.com/v1/search
```

### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| name | String | 是 | 城市名称 |
| count | Int | 否 | 返回结果数量（默认 10） |
| language | String | 否 | 语言代码（zh/en） |
| format | String | 否 | 响应格式（json） |

### 请求示例

```
GET https://geocoding-api.open-meteo.com/v1/search?name=北京&count=10&language=zh&format=json
```

### 响应结构

```json
{
  "results": [
    {
      "id": 1816670,
      "name": "北京市",
      "latitude": 39.9075,
      "longitude": 116.39723,
      "country": "China",
      "admin1": "北京"
    }
  ]
}
```

### 对应代码

- 接口定义：[`OpenMeteoApiService.searchCity()`](app/src/main/java/com/ai/weather/data/remote/ApiServices.kt)
- 数据模型：[`GeocodingResult.kt`](app/src/main/java/com/ai/weather/data/model/GeocodingResult.kt)

---

## 2. Open-Meteo 天气预报 API

获取指定坐标的天气数据。

### 端点

```
GET https://api.open-meteo.com/v1/forecast
```

### 请求参数

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| latitude | Double | 是 | 纬度 |
| longitude | Double | 是 | 经度 |
| current | String | 否 | 当前天气字段列表 |
| current_weather | Boolean | 否 | 是否返回基础当前天气 |
| hourly | String | 否 | 每小时字段列表 |
| daily | String | 否 | 每日字段列表 |
| timezone | String | 否 | 时区 |
| forecast_days | Int | 否 | 预报天数 |

### 请求示例

```
GET https://api.open-meteo.com/v1/forecast?latitude=39.9075&longitude=116.39723&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,pressure_msl&current_weather=true&hourly=temperature_2m,weathercode,relative_humidity_2m&daily=weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_probability_max&timezone=Asia/Shanghai&forecast_days=7
```

### 响应结构（简化）

```json
{
  "latitude": 39.9075,
  "longitude": 116.39723,
  "timezone": "Asia/Shanghai",
  "current_weather": {
    "temperature": 25.4,
    "windspeed": 12.3,
    "winddirection": 180,
    "weathercode": 0,
    "time": "2024-01-01T12:00"
  },
  "current": {
    "relative_humidity_2m": 45,
    "apparent_temperature": 26.0,
    "is_day": 1,
    "pressure_msl": 1013.2
  },
  "daily": {
    "time": ["2024-01-01", "2024-01-02"],
    "weathercode": [0, 1],
    "temperature_2m_max": [28.0, 27.0],
    "temperature_2m_min": [15.0, 14.0],
    "sunrise": ["2024-01-01T07:30"],
    "sunset": ["2024-01-01T17:45"],
    "uv_index_max": [5.5],
    "precipitation_probability_max": [10]
  },
  "hourly": {
    "time": ["2024-01-01T12:00", "2024-01-01T13:00"],
    "temperature_2m": [25.4, 26.0],
    "weathercode": [0, 0],
    "relative_humidity_2m": [45, 43]
  }
}
```

### WMO 天气代码对照表

| 代码 | 描述 |
|------|------|
| 0 | 晴朗 |
| 1-3 | 多云 |
| 45, 48 | 雾 |
| 51-57 | 毛毛雨 |
| 61-67 | 雨 |
| 71-77 | 雪 |
| 80-82 | 阵雨 |
| 85-86 | 阵雪 |
| 95-99 | 雷阵雨 |

### 对应代码

- 接口定义：[`OpenMeteoApiService.getWeather()`](app/src/main/java/com/ai/weather/data/remote/ApiServices.kt)
- 数据模型：[`WeatherForecastResponse.kt`](app/src/main/java/com/ai/weather/data/model/WeatherForecastResponse.kt)
- 代码映射：[`WeatherCodeMapper.kt`](app/src/main/java/com/ai/weather/data/model/WeatherCodeMapper.kt)

---

## 3. DeepSeek AI API

用于生成自然语言天气分析。

### 端点

```
POST https://api.deepseek.com/chat/completions
```

### 认证

在请求头中携带 API Key：
```
Authorization: Bearer sk-your-api-key
```

> 本项目通过 `local.properties` 配置 API Key，注入到 `BuildConfig.DEEPSEEK_API_KEY`。

### 请求体

```json
{
  "model": "deepseek-chat",
  "messages": [
    {
      "role": "system",
      "content": "你是一位专业气象分析师，根据天气数据生成简洁、贴心的中文天气分析。"
    },
    {
      "role": "user",
      "content": "城市：北京\n天气：晴朗，气温25°C，体感26°C\n湿度：45%，风速：12km/h\n紫外线：中等，降水概率：10%\n日出：07:30，日落：17:45\n\n请用JSON格式返回，包含三个字段：\n- summary：30字以内的天气总结\n- clothing：穿衣建议，40字以内\n- activities：户外活动建议，40字以内"
    }
  ],
  "max_tokens": 500,
  "temperature": 0.7,
  "stream": false
}
```

### 响应结构

```json
{
  "id": "chatcmpl-xxx",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "{\"summary\":\"北京今日晴朗温暖，气温适宜\",\"clothing\":\"建议穿短袖薄外套，早晚微凉\",\"activities\":\"适合户外散步和骑行\"}"
      },
      "finish_reason": "stop"
    }
  ]
}
```

### 降级策略

当出现以下情况时，应用自动降级为本地规则生成建议：
- API Key 未配置或为占位符
- 网络请求失败
- 响应解析失败

本地规则根据天气类别和温度区间生成固定的穿衣与活动建议。

### 对应代码

- 接口定义：[`DeepSeekApiService.chat()`](app/src/main/java/com/ai/weather/data/remote/ApiServices.kt)
- AI 分析逻辑：[`AiWeatherAnalyzer.kt`](app/src/main/java/com/ai/weather/data/remote/AiWeatherAnalyzer.kt)
- 数据模型：[`DeepSeekModels.kt`](app/src/main/java/com/ai/weather/data/model/DeepSeekModels.kt)

---

## 4. 完整数据流

```
用户输入城市名
    ↓
OpenMeteoApiService.searchCity()  →  地理编码 API  →  返回经纬度
    ↓
OpenMeteoApiService.getWeather()  →  天气预报 API  →  返回天气数据
    ↓
WeatherRepository.mapToWeatherInfo()  →  转换为 UI 模型
    ↓
AiWeatherAnalyzer.analyze()  →  DeepSeek API  →  返回 AI 分析
    ↓
WeatherViewModel  →  更新 StateFlow
    ↓
Compose UI 自动重组渲染
```

## 错误处理

所有 API 调用均通过 Kotlin `Result` 封装返回结果：

```kotlin
suspend fun fetchWeather(...): Result<WeatherInfo> = try {
    Result.success(data)
} catch (e: Exception) {
    Result.failure(e)
}
```

调用方通过 `.onSuccess { }` 和 `.onFailure { }` 处理结果，保证 UI 层不会因未捕获异常崩溃。
