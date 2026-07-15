
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentWeather {

    private Double temperature = 0.0;
    private Double windspeed = 0.0;
    private Integer winddirection = 0;
    private Integer weathercode = 0;
    private String time = "";
}
