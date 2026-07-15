
package com.ai.weather.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "weather")
public class WeatherConfig {

    private OpenMeteoConfig openMeteo = new OpenMeteoConfig();
    private DeepSeekConfig deepseek = new DeepSeekConfig();

    @Data
    public static class OpenMeteoConfig {
        private String geoBaseUrl = "https://geocoding-api.open-meteo.com";
        private String weatherBaseUrl = "https://api.open-meteo.com";
    }

    @Data
    public static class DeepSeekConfig {
        private String apiUrl = "https://api.deepseek.com/chat/completions";
        private String apiKey = "";
        private String model = "deepseek-chat";
    }
}
