
package com.ai.weather.service;

import com.ai.weather.entity.FavoriteCityEntity;
import com.ai.weather.entity.SearchHistoryEntity;
import com.ai.weather.model.*;
import com.ai.weather.repository.FavoriteCityRepository;
import com.ai.weather.repository.SearchHistoryRepository;
import com.ai.weather.util.WeatherCodeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {

    private final OpenMeteoApiService openMeteoApi;
    private final AiWeatherAnalyzer aiAnalyzer;
    private final SearchHistoryRepository searchHistoryRepository;
    private final FavoriteCityRepository favoriteCityRepository;

    public List<Location> searchCity(String name) {
        try {
            GeocodingResult result = openMeteoApi.searchCity(name);
            if (result.getResults() != null) {
                return result.getResults();
            }
        } catch (Exception e) {
            log.error("Failed to search city: {}", e.getMessage());
        }
        return new ArrayList<>();
    }

    @Transactional
    public WeatherInfo fetchWeather(Location location, TempUnit unit) {
        try {
            WeatherForecastResponse response = openMeteoApi.getWeather(
                    location.getLatitude(), location.getLongitude()
            );

            WeatherInfo info = mapToWeatherInfo(location, response, unit);

            SearchHistoryEntity history = SearchHistoryEntity.builder()
                    .cityName(location.getName())
                    .region(location.getRegion())
                    .country(location.getCountry())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .timestamp(System.currentTimeMillis())
                    .build();
            searchHistoryRepository.save(history);

            return info;
        } catch (Exception e) {
            log.error("Failed to fetch weather: {}", e.getMessage());
            throw new RuntimeException("获取天气失败: " + e.getMessage());
        }
    }

    public AiWeatherInsight analyzeWeather(WeatherInfo info) {
        return aiAnalyzer.analyze(info);
    }

    @Transactional(readOnly = true)
    public List<SearchHistoryEntity> getSearchHistory() {
        return searchHistoryRepository.findTop20OrderByTimestampDesc();
    }

    @Transactional
    public void clearSearchHistory() {
        searchHistoryRepository.clearAll();
    }

    @Transactional(readOnly = true)
    public List<FavoriteCityEntity> getFavorites() {
        return favoriteCityRepository.findAllOrderByAddedAtDesc();
    }

    @Transactional
    public void toggleFavorite(Location location, boolean isFavorite) {
        if (isFavorite) {
            favoriteCityRepository.deleteByCityName(location.getName());
        } else {
            FavoriteCityEntity favorite = FavoriteCityEntity.builder()
                    .cityName(location.getName())
                    .region(location.getRegion())
                    .country(location.getCountry())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude())
                    .addedAt(System.currentTimeMillis())
                    .build();
            favoriteCityRepository.save(favorite);
        }
    }

    @Transactional(readOnly = true)
    public boolean isFavorite(String cityName) {
        return favoriteCityRepository.existsByCityName(cityName);
    }

    public Location toLocation(FavoriteCityEntity entity) {
        return Location.builder()
                .name(entity.getCityName())
                .region(entity.getRegion())
                .country(entity.getCountry())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .build();
    }

    private WeatherInfo mapToWeatherInfo(Location location, WeatherForecastResponse response, TempUnit unit) {
        CurrentWeather current = response.getCurrentWeather();
        CurrentDetails details = response.getCurrent();
        DailyForecast daily = response.getDaily();
        HourlyForecast hourly = response.getHourly();

        int celsius = current != null ? current.getTemperature().intValue() : 0;
        int temp = unit.convert(celsius);

        int feelCelsius = details != null ? details.getApparentTemperature().intValue() : celsius + 2;
        int feelsLike = unit.convert(feelCelsius);

        int code = current != null ? current.getWeathercode() : 0;
        String description = WeatherCodeMapper.describe(code);
        double uvMax = daily != null && daily.getUvIndexMax() != null && !daily.getUvIndexMax().isEmpty()
                ? daily.getUvIndexMax().get(0) : 0.0;

        List<DailyItem> dailyItems = new ArrayList<>();
        if (daily != null && daily.getTime() != null) {
            for (int i = 0; i < daily.getTime().size(); i++) {
                DailyItem item = DailyItem.builder()
                        .date(daily.getTime().get(i))
                        .maxTemp(unit.convert(daily.getTempMax() != null ? daily.getTempMax().get(i).intValue() : 0))
                        .minTemp(unit.convert(daily.getTempMin() != null ? daily.getTempMin().get(i).intValue() : 0))
                        .description(WeatherCodeMapper.describe(daily.getWeatherCode() != null ? daily.getWeatherCode().get(i) : 0))
                        .weatherCode(daily.getWeatherCode() != null ? daily.getWeatherCode().get(i) : 0)
                        .precipProb(daily.getPrecipProbMax() != null ? daily.getPrecipProbMax().get(i) : 0)
                        .build();
                dailyItems.add(item);
            }
        }

        List<HourlyItem> hourlyItems = new ArrayList<>();
        if (hourly != null && hourly.getTime() != null) {
            long now = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());

            for (int i = 0; i < hourly.getTime().size(); i++) {
                String timeStr = hourly.getTime().get(i);
                try {
                    Date date = sdf.parse(timeStr);
                    if (date != null && date.getTime() >= now) {
                        HourlyItem item = HourlyItem.builder()
                                .time(timeStr.substring(11, 16))
                                .temp(unit.convert(hourly.getTemperature() != null ? hourly.getTemperature().get(i).intValue() : 0))
                                .description(WeatherCodeMapper.describe(hourly.getWeatherCode() != null ? hourly.getWeatherCode().get(i) : 0))
                                .weatherCode(hourly.getWeatherCode() != null ? hourly.getWeatherCode().get(i) : 0)
                                .humidity(hourly.getHumidity() != null ? hourly.getHumidity().get(i) : 0)
                                .build();
                        hourlyItems.add(item);
                    }
                } catch (ParseException e) {
                    log.warn("Failed to parse time: {}", timeStr);
                }
            }

            if (hourlyItems.size() > 24) {
                hourlyItems = hourlyItems.subList(0, 24);
            }
        }

        String sunrise = "";
        String sunset = "";
        if (daily != null && daily.getSunrise() != null && !daily.getSunrise().isEmpty()) {
            sunrise = formatTime(daily.getSunrise().get(0));
        }
        if (daily != null && daily.getSunset() != null && !daily.getSunset().isEmpty()) {
            sunset = formatTime(daily.getSunset().get(0));
        }

        return WeatherInfo.builder()
                .cityName(location.getName())
                .region(location.getRegion())
                .country(location.getCountry())
                .temperature(temp)
                .feelsLike(feelsLike)
                .description(description)
                .humidity(details != null ? details.getHumidity() : 60)
                .windSpeed(current != null ? current.getWindspeed() : 0.0)
                .windDir(current != null ? current.getWinddirection() : 0)
                .pressure(details != null ? details.getPressure() : 1013.0)
                .uvIndex(uvMax)
                .uvDescription(WeatherCodeMapper.uvDescription(uvMax))
                .sunrise(sunrise)
                .sunset(sunset)
                .precipProbability(daily != null && daily.getPrecipProbMax() != null && !daily.getPrecipProbMax().isEmpty()
                        ? daily.getPrecipProbMax().get(0) : 0)
                .weatherCode(code)
                .isDay(details != null ? details.getIsDay() == 1 : true)
                .dailyForecast(dailyItems)
                .hourlyForecast(hourlyItems)
                .build();
    }

    private String formatTime(String isoTime) {
        if (isoTime != null && isoTime.length() >= 16) {
            return isoTime.substring(11, 16);
        }
        return "00:00";
    }
}
