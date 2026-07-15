package com.ai.weather.data.remote

import com.ai.weather.data.model.AiWeatherInsight
import com.ai.weather.data.model.ChatMessage
import com.ai.weather.data.model.DeepSeekRequest
import com.ai.weather.data.model.WeatherInfo
import com.ai.weather.data.model.WeatherCodeMapper
import com.ai.weather.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AI天气分析服务
 * 将天气数据转为自然语言描述 + 穿衣建议 + 活动推荐
 *
 * 与参考项目纯静态文本不同：使用DeepSeek LLM动态生成个性化天气解读
 */
@Singleton
class AiWeatherAnalyzer @Inject constructor(
    private val deepSeekApi: DeepSeekApiService
) {

    suspend fun analyze(info: WeatherInfo): AiWeatherInsight {
        if (BuildConfig.DEEPSEEK_API_KEY.isBlank() ||
            BuildConfig.DEEPSEEK_API_KEY == "your_deepseek_api_key_here"
        ) {
            return fallbackInsight(info)
        }

        val prompt = buildPrompt(info)
        val request = DeepSeekRequest(
            messages = listOf(
                ChatMessage(
                    role = "system",
                    content = "你是一位专业气象分析师，根据天气数据生成简洁、贴心的中文天气分析。"
                ),
                ChatMessage(role = "user", content = prompt)
            ),
            maxTokens = 500
        )

        return try {
            val response = deepSeekApi.chat(request)
            val content = response.choices.firstOrNull()?.message?.content.orEmpty()
            parseInsight(content, info)
        } catch (e: Exception) {
            fallbackInsight(info)
        }
    }

    private fun buildPrompt(info: WeatherInfo): String = buildString {
        appendLine("城市：${info.cityName}")
        appendLine("天气：${info.description}，气温${info.temperature}°C，体感${info.feelsLike}°C")
        appendLine("湿度：${info.humidity}%，风速：${info.windSpeed}km/h")
        appendLine("紫外线：${info.uvDescription}，降水概率：${info.precipProbability}%")
        appendLine("日出：${info.sunrise}，日落：${info.sunset}")
        appendLine()
        appendLine("请用JSON格式返回，包含三个字段：")
        appendLine("- summary：30字以内的天气总结")
        appendLine("- clothing：穿衣建议，40字以内")
        appendLine("- activities：户外活动建议，40字以内")
    }

    private fun parseInsight(content: String, info: WeatherInfo): AiWeatherInsight {
        // 尝试解析JSON，失败则使用原始文本
        return try {
            val json = extractJson(content)
            val summary = regex(json, """"summary"\s*:\s*"([^"]+)"""")
            val clothing = regex(json, """"clothing"\s*:\s*"([^"]+)"""")
            val activities = regex(json, """"activities"\s*:\s*"([^"]+)"""")
            AiWeatherInsight(
                summary = summary.ifBlank { content.take(40) },
                clothing = clothing,
                activities = activities
            )
        } catch (e: Exception) {
            AiWeatherInsight(content.take(80), "", "")
        }
    }

    private fun extractJson(text: String): String {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        return if (start in 0 until end) text.substring(start, end + 1) else text
    }

    private fun regex(text: String, pattern: String): String =
        Regex(pattern).find(text)?.groupValues?.getOrNull(1).orEmpty()

    /**
     * API不可用时的兜底方案（保持功能可用）
     */
    private fun fallbackInsight(info: WeatherInfo): AiWeatherInsight {
        val cat = WeatherCodeMapper.category(info.weatherCode)
        val summary = when (cat) {
            WeatherCodeMapper.WeatherCategory.SUNNY -> "${info.cityName}今日晴朗，气温${info.temperature}°C"
            WeatherCodeMapper.WeatherCategory.CLOUDY -> "${info.cityName}多云，气温${info.temperature}°C"
            WeatherCodeMapper.WeatherCategory.RAINY -> "${info.cityName}有雨，气温${info.temperature}°C，记得带伞"
            WeatherCodeMapper.WeatherCategory.SNOWY -> "${info.cityName}有雪，气温${info.temperature}°C，注意保暖"
            WeatherCodeMapper.WeatherCategory.FOGGY -> "${info.cityName}有雾，能见度低，出行注意安全"
            WeatherCodeMapper.WeatherCategory.THUNDER -> "${info.cityName}雷阵雨，气温${info.temperature}°C，避免户外活动"
        }
        val clothing = when {
            info.temperature >= 28 -> "炎热天气，建议穿短袖、短裤"
            info.temperature in 20..27 -> "温暖舒适，穿薄单衣即可"
            info.temperature in 15..19 -> "微凉，建议加薄外套"
            info.temperature in 5..14 -> "较冷，需穿毛衣或夹克"
            else -> "寒冷，需穿羽绒服或厚棉衣"
        }
        val activities = when (cat) {
            WeatherCodeMapper.WeatherCategory.SUNNY -> "适合户外运动、郊游"
            WeatherCodeMapper.WeatherCategory.CLOUDY -> "适宜散步、骑行"
            WeatherCodeMapper.WeatherCategory.RAINY -> "建议室内活动"
            WeatherCodeMapper.WeatherCategory.SNOWY -> "注意保暖，减少外出"
            WeatherCodeMapper.WeatherCategory.FOGGY -> "出行注意安全"
            WeatherCodeMapper.WeatherCategory.THUNDER -> "避免户外活动"
        }
        return AiWeatherInsight(summary, clothing, activities)
    }
}
