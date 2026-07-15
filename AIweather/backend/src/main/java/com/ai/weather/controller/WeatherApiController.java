
package com.ai.weather.controller;

import com.ai.weather.entity.FavoriteCityEntity;
import com.ai.weather.entity.SearchHistoryEntity;
import com.ai.weather.model.AiWeatherInsight;
import com.ai.weather.model.Location;
import com.ai.weather.model.TempUnit;
import com.ai.weather.model.WeatherInfo;
import com.ai.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WeatherApiController {

    private final WeatherService weatherService;

    @GetMapping("/weather/search")
    public ResponseEntity<List<Location>> searchCity(@RequestParam String name) {
        log.debug("Searching city: {}", name);
        List<Location> results = weatherService.searchCity(name);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/weather/forecast")
    public ResponseEntity<WeatherInfo> getWeather(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false, defaultValue = "CELSIUS") String unit) {
        log.debug("Getting weather for lat={}, lon={}, unit={}", lat, lon, unit);

        TempUnit tempUnit = TempUnit.valueOf(unit.toUpperCase());
        Location location = Location.builder()
                .latitude(lat)
                .longitude(lon)
                .build();

        WeatherInfo weather = weatherService.fetchWeather(location, tempUnit);
        return ResponseEntity.ok(weather);
    }

    @PostMapping("/weather/ai-analysis")
    public ResponseEntity<AiWeatherInsight> getAiAnalysis(@RequestBody WeatherInfo weatherInfo) {
        log.debug("Getting AI analysis for city: {}", weatherInfo.getCityName());
        AiWeatherInsight insight = weatherService.analyzeWeather(weatherInfo);
        return ResponseEntity.ok(insight);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteCityEntity>> getFavorites() {
        List<FavoriteCityEntity> favorites = weatherService.getFavorites();
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("/favorites/{cityName}")
    public ResponseEntity<Map<String, Boolean>> toggleFavorite(
            @PathVariable String cityName,
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false, defaultValue = "") String region,
            @RequestParam(required = false, defaultValue = "") String country) {

        boolean isFavorite = weatherService.isFavorite(cityName);

        Location location = Location.builder()
                .name(cityName)
                .latitude(lat)
                .longitude(lon)
                .region(region)
                .country(country)
                .build();

        weatherService.toggleFavorite(location, isFavorite);

        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", !isFavorite);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/favorites/{cityName}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String cityName) {
        Location location = Location.builder().name(cityName).build();
        weatherService.toggleFavorite(location, true);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<SearchHistoryEntity>> getSearchHistory() {
        List<SearchHistoryEntity> history = weatherService.getSearchHistory();
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/history")
    public ResponseEntity<Void> clearSearchHistory() {
        weatherService.clearSearchHistory();
        return ResponseEntity.noContent().build();
    }
}
