
package com.ai.weather.service;

import com.ai.weather.config.WeatherConfig;
import com.ai.weather.model.AiWeatherInsight;
import com.ai.weather.model.WeatherInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AiWeatherAnalyzerTest {

    @Mock
    private DeepSeekApiService deepSeekApi;

    private AiWeatherAnalyzer aiAnalyzer;
    private WeatherConfig weatherConfig;

    @BeforeEach
    void setUp() {
        weatherConfig = new WeatherConfig();
        weatherConfig.getDeepseek().setApiKey("your_deepseek_api_key_here");
        aiAnalyzer = new AiWeatherAnalyzer(deepSeekApi, weatherConfig);
    }

    @Test
    @DisplayName("AI分析回退测试 - 晴朗天气")
    void testFallbackInsightSunny() {
        WeatherInfo info = WeatherInfo.builder()
                .cityName("北京")
                .temperature(25)
                .weatherCode(0)
                .build();

        AiWeatherInsight insight = aiAnalyzer.analyze(info);

        assertNotNull(insight);
        assertTrue(insight.getSummary().contains("北京"));
        assertTrue(insight.getSummary().contains("晴朗"));
        assertTrue(insight.getClothing().contains("薄单衣"));
        assertTrue(insight.getActivities().contains("户外活动"));
    }

    @Test
    @DisplayName("AI分析回退测试 - 雨天")
    void testFallbackInsightRainy() {
        WeatherInfo info = WeatherInfo.builder()
                .cityName("上海")
                .temperature(20)
                .weatherCode(61)
                .build();

        AiWeatherInsight insight = aiAnalyzer.analyze(info);

        assertNotNull(insight);
        assertTrue(insight.getSummary().contains("雨"));
        assertTrue(insight.getClothing().contains("薄外套"));
        assertTrue(insight.getActivities().contains("室内"));
    }

    @Test
    @DisplayName("AI分析回退测试 - 寒冷天气")
    void testFallbackInsightCold() {
        WeatherInfo info = WeatherInfo.builder()
                .cityName("哈尔滨")
                .temperature(-5)
                .weatherCode(71)
                .build();

        AiWeatherInsight insight = aiAnalyzer.analyze(info);

        assertNotNull(insight);
        assertTrue(insight.getSummary().contains("雪"));
        assertTrue(insight.getClothing().contains("羽绒服"));
    }

    @Test
    @DisplayName("AI分析回退测试 - 炎热天气")
    void testFallbackInsightHot() {
        WeatherInfo info = WeatherInfo.builder()
                .cityName("广州")
                .temperature(35)
                .weatherCode(0)
                .build();

        AiWeatherInsight insight = aiAnalyzer.analyze(info);

        assertNotNull(insight);
        assertTrue(insight.getClothing().contains("短袖"));
    }
}
