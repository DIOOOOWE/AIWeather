
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
public class DailyForecast {

    private List<String> time;

    @com.google.gson.annotations.SerializedName("weathercode")
    private List<Integer> weatherCode;

    @com.google.gson.annotations.SerializedName("temperature_2m_max")
    private List<Double> tempMax;

    @com.google.gson.annotations.SerializedName("temperature_2m_min")
    private List<Double> tempMin;

    private List<String> sunrise;
    private List<String> sunset;

    @com.google.gson.annotations.SerializedName("uv_index_max")
    private List<Double> uvIndexMax;

    @com.google.gson.annotations.SerializedName("precipitation_probability_max")
    private List<Integer> precipProbMax;
}
