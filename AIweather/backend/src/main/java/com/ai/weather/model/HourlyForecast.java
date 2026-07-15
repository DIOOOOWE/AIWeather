
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyForecast {

    private List<String> time;

    @com.google.gson.annotations.SerializedName("temperature_2m")
    private List<Double> temperature;

    @com.google.gson.annotations.SerializedName("weathercode")
    private List<Integer> weatherCode;

    @com.google.gson.annotations.SerializedName("relative_humidity_2m")
    private List<Integer> humidity;
}
