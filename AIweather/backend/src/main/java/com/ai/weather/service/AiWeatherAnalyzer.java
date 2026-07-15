
package com.ai.weather.service;

import com.ai.weather.config.WeatherConfig;
import com.ai.weather.model.AiWeatherInsight;
import com.ai.weather.model.ChatMessage;
import com.ai.weather.model.DeepSeekRequest;
import com.ai.weather.model.DeepSeekResponse;
import com.ai.weather.model.WeatherInfo;
import com.ai.weather.util.WeatherCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiWeatherAnalyzer {

    private final DeepSeekApiService deepSeekApi;
    private final WeatherConfig weatherConfig;

    public AiWeatherInsight analyze(WeatherInfo info) {
        String apiKey = weatherConfig.getDeepseek().getApiKey();
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("your_deepseek_api_key_here")) {
            log.info("DeepSeek API key not configured, using fallback insight");
            return fallbackInsight(info);
        }

        String prompt = buildPrompt(info);
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(ChatMessage.builder()
                .role("system")
                .content("你是一位专业气象分析师，根据天气数据生成简洁、贴心的中文天气分析。")
                .build());
        messages.add(ChatMessage.builder()
                .role("user")
                .content(prompt)
                .build());

        DeepSeekRequest request = DeepSeekRequest.builder()
                .messages(messages)
                .maxTokens(500)
                .build();

        try {
            DeepSeekResponse response = deepSeekApi.chat(request);
            if (response.getChoices() != null && !response.getChoices().isEmpty()) {
                String content = response.getChoices().get(0).getMessage().getContent();
                return parseInsight(content, info);
            }
        } catch (Exception e) {
            log.error("DeepSeek API call failed: {}", e.getMessage());
        }

        return fallbackInsight(info);
    }

    private String buildPrompt(WeatherInfo info) {
        StringBuilder sb = new StringBuilder();
        sb.append("城市：").append(info.getCityName()).append("\n");
        sb.append("天气：").append(info.getDescription()).append("，气温").append(info.getTemperature()).append("\u00B0C，体感").append(info.getFeelsLike()).append("\u00B0C\n");
        sb.append("湿度：").append(info.getHumidity()).append("%，风速：").append(info.getWindSpeed()).append("km/h\n");
        sb.append("紫外线：").append(info.getUvDescription()).append("，降水概率：").append(info.getPrecipProbability()).append("%\n");
        sb.append("日出：").append(info.getSunrise()).append("，日落：").append(info.getSunset()).append("\n\n");
        sb.append("请用JSON格式返回，包含三个字段：\n");
        sb.append("- summary：30字以内的天气总结\n");
        sb.append("- clothing：穿衣建议，40字以内\n");
        sb.append("- activities：户外活动建议，40字以内");
        return sb.toString();
    }

    private AiWeatherInsight parseInsight(String content, WeatherInfo info) {
        try {
            String json = extractJson(content);
            String summary = regex(json, "\"summary\"\\s*:\\s*\"([^\"]+)\"");
            String clothing = regex(json, "\"clothing\"\\s*:\\s*\"([^\"]+)\"");
            String activities = regex(json, "\"activities\"\\s*:\\s*\"([^\"]+)\"");

            return AiWeatherInsight.builder()
                    .summary(summary.isEmpty() ? content.substring(0, Math.min(40, content.length())) : summary)
                    .clothing(clothing)
                    .activities(activities)
                    .build();
        } catch (Exception e) {
            return AiWeatherInsight.builder()
                    .summary(content.substring(0, Math.min(80, content.length())))
                    .clothing("")
                    .activities("")
                    .build();
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    private String regex(String text, String pattern) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(text);
        if (m.find()) {
            return m.group(1);
        }
        return "";
    }

    private AiWeatherInsight fallbackInsight(WeatherInfo info) {
        WeatherCodeMapper.WeatherCategory cat = WeatherCodeMapper.category(info.getWeatherCode());

        String summary;
        switch (cat) {
            case SUNNY:
                summary = info.getCityName() + "今日晴朗，气温" + info.getTemperature() + "\u00B0C";
                break;
            case CLOUDY:
                summary = info.getCityName() + "多云，气温" + info.getTemperature() + "\u00B0C";
                break;
            case RAINY:
                summary = info.getCityName() + "有雨，气温" + info.getTemperature() + "\u00B0C，记得带伞";
                break;
            case SNOWY:
                summary = info.getCityName() + "有雪，气温" + info.getTemperature() + "\u00B0C，注意保暖";
                break;
            case FOGGY:
                summary = info.getCityName() + "有雾，能见度低，出行注意安全";
                break;
            case THUNDER:
                summary = info.getCityName() + "雷阵雨，气温" + info.getTemperature() + "\u00B0C，避免户外活动";
                break;
            default:
                summary = info.getCityName() + "天气" + info.getDescription() + "，气温" + info.getTemperature() + "\u00B0C";
        }

        String clothing;
        int temp = info.getTemperature();
        if (temp >= 28) {
            clothing = "炎热天气，建议穿短袖、短裤";
        } else if (temp >= 20 && temp <= 27) {
            clothing = "温暖舒适，穿薄单衣即可";
        } else if (temp >= 15 && temp <= 19) {
            clothing = "微凉，建议加薄外套";
        } else if (temp >= 5 && temp <= 14) {
            clothing = "较冷，需穿毛衣或夹克";
        } else {
            clothing = "寒冷，需穿羽绒服或厚棉衣";
        }

        String activities;
        switch (cat) {
            case SUNNY:
                activities = "适合户外运动、郊游";
                break;
            case CLOUDY:
                activities = "适宜散步、骑行";
                break;
            case RAINY:
                activities = "建议室内活动";
                break;
            case SNOWY:
                activities = "注意保暖，减少外出";
                break;
            case FOGGY:
                activities = "出行注意安全";
                break;
            case THUNDER:
                activities = "避免户外活动";
                break;
            default:
                activities = "根据天气情况安排活动";
        }

        return AiWeatherInsight.builder()
                .summary(summary)
                .clothing(clothing)
                .activities(activities)
                .build();
    }
}
