
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentDetails {

    @com.google.gson.annotations.SerializedName("relative_humidity_2m")
    private Integer humidity = 0;

    @com.google.gson.annotations.SerializedName("apparent_temperature")
    private Double apparentTemperature = 0.0;

    @com.google.gson.annotations.SerializedName("pressure_msl")
    private Double pressure = 0.0;

    @com.google.gson.annotations.SerializedName("is_day")
    private Integer isDay = 1;
}
