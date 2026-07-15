
package com.ai.weather.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private Long id = 0L;
    private String name = "";
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private String country = "";
    private String region = "";
}
