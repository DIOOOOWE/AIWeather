
package com.ai.weather.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "search_history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String cityName;

    private String region = "";
    private String country = "";

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Long timestamp;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = System.currentTimeMillis();
        }
    }
}
