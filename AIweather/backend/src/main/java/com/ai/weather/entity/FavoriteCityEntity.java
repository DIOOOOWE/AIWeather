
package com.ai.weather.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "favorite_cities")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCityEntity {

    @Id
    @Column(nullable = false)
    private String cityName;

    private String region = "";
    private String country = "";

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Long addedAt;

    @PrePersist
    protected void onCreate() {
        if (addedAt == null) {
            addedAt = System.currentTimeMillis();
        }
    }
}
