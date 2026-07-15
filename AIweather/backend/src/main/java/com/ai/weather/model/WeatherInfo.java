
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInfo {

    private String cityName;
    private String region = "";
    private String country = "";
    private Integer temperature;
    private Integer feelsLike;
    private String description;
    private Integer humidity;
    private Double windSpeed;
    private Integer windDir = 0;
    private Double pressure = 0.0;
    private Double uvIndex = 0.0;
    private String uvDescription = "";
    private String sunrise = "";
    private String sunset = "";
    private Integer precipProbability = 0;
    private Integer weatherCode = 0;
    private Boolean isDay = true;

    @Builder.Default
    private List<DailyItem> dailyForecast = new ArrayList<>();

    @Builder.Default
    private List<HourlyItem> hourlyForecast = new ArrayList<>();
}
