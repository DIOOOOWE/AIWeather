
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HourlyItem {

    private String time;
    private Integer temp;
    private String description;
    private Integer weatherCode;
    private Integer humidity;
}
