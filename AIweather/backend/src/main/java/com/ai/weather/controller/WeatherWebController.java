
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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WeatherWebController {

    private final WeatherService weatherService;

    @GetMapping
    public String home(Model model) {
        model.addAttribute("weather", null);
        model.addAttribute("aiInsight", null);
        model.addAttribute("searchResults", null);
        model.addAttribute("tempUnit", TempUnit.CELSIUS);
        model.addAttribute("history", weatherService.getSearchHistory());
        model.addAttribute("favorites", weatherService.getFavorites());
        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam String query, Model model) {
        log.debug("Web search: {}", query);
        List<Location> results = weatherService.searchCity(query);
        model.addAttribute("searchResults", results);
        model.addAttribute("searchQuery", query);
        model.addAttribute("weather", null);
        model.addAttribute("aiInsight", null);
        model.addAttribute("tempUnit", TempUnit.CELSIUS);
        model.addAttribute("history", weatherService.getSearchHistory());
        model.addAttribute("favorites", weatherService.getFavorites());
        return "index";
    }

    @GetMapping("/weather")
    public String getWeather(
            @RequestParam String cityName,
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false, defaultValue = "") String region,
            @RequestParam(required = false, defaultValue = "") String country,
            @RequestParam(required = false, defaultValue = "CELSIUS") String unit,
            Model model) {

        log.debug("Web weather: {} ({}, {})", cityName, lat, lon);

        TempUnit tempUnit = TempUnit.valueOf(unit.toUpperCase());
        Location location = Location.builder()
                .name(cityName)
                .latitude(lat)
                .longitude(lon)
                .region(region)
                .country(country)
                .build();

        WeatherInfo weather = weatherService.fetchWeather(location, tempUnit);
        AiWeatherInsight aiInsight = weatherService.analyzeWeather(weather);

        model.addAttribute("weather", weather);
        model.addAttribute("aiInsight", aiInsight);
        model.addAttribute("currentLocation", location);
        model.addAttribute("tempUnit", tempUnit);
        model.addAttribute("isFavorite", weatherService.isFavorite(cityName));
        model.addAttribute("history", weatherService.getSearchHistory());
        model.addAttribute("favorites", weatherService.getFavorites());

        return "index";
    }

    @PostMapping("/toggle-favorite")
    public String toggleFavorite(
            @RequestParam String cityName,
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam(required = false, defaultValue = "") String region,
            @RequestParam(required = false, defaultValue = "") String country,
            @RequestParam(required = false, defaultValue = "CELSIUS") String unit,
            Model model) {

        Location location = Location.builder()
                .name(cityName)
                .latitude(lat)
                .longitude(lon)
                .region(region)
                .country(country)
                .build();

        boolean isFavorite = weatherService.isFavorite(cityName);
        weatherService.toggleFavorite(location, isFavorite);

        TempUnit tempUnit = TempUnit.valueOf(unit.toUpperCase());
        WeatherInfo weather = weatherService.fetchWeather(location, tempUnit);
        AiWeatherInsight aiInsight = weatherService.analyzeWeather(weather);

        model.addAttribute("weather", weather);
        model.addAttribute("aiInsight", aiInsight);
        model.addAttribute("currentLocation", location);
        model.addAttribute("tempUnit", tempUnit);
        model.addAttribute("isFavorite", !isFavorite);
        model.addAttribute("history", weatherService.getSearchHistory());
        model.addAttribute("favorites", weatherService.getFavorites());

        return "index";
    }

    @GetMapping("/favorites/{cityName}")
    public String loadFavorite(@PathVariable String cityName, Model model) {
        List<FavoriteCityEntity> favorites = weatherService.getFavorites();
        for (FavoriteCityEntity fav : favorites) {
            if (fav.getCityName().equals(cityName)) {
                Location location = weatherService.toLocation(fav);
                return getWeather(cityName, fav.getLatitude(), fav.getLongitude(),
                        fav.getRegion(), fav.getCountry(), "CELSIUS", model);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/clear-history")
    public String clearHistory(Model model) {
        weatherService.clearSearchHistory();
        return home(model);
    }
}
