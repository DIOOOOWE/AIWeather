
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherForecastResponse {

    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private String timezone = "";
    private CurrentWeather currentWeather;
    private CurrentDetails current;
    private DailyForecast daily;
    private HourlyForecast hourly;
}
