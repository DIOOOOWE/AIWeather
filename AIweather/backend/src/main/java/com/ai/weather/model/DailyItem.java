
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyItem {

    private String date;
    private Integer maxTemp;
    private Integer minTemp;
    private String description;
    private Integer weatherCode;
    private Integer precipProb;
}
