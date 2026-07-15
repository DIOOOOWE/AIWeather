
package com.ai.weather.service;

import com.ai.weather.config.WeatherConfig;
import com.ai.weather.model.AiWeatherInsight;
import com.ai.weather.model.Location;
import com.ai.weather.model.TempUnit;
import com.ai.weather.model.WeatherInfo;
import com.ai.weather.repository.FavoriteCityRepository;
import com.ai.weather.repository.SearchHistoryRepository;
import com.ai.weather.util.WeatherCodeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private OpenMeteoApiService openMeteoApi;

    @Mock
    private AiWeatherAnalyzer aiAnalyzer;

    @Mock
    private SearchHistoryRepository searchHistoryRepository;

    @Mock
    private FavoriteCityRepository favoriteCityRepository;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        weatherService = new WeatherService(openMeteoApi, aiAnalyzer, searchHistoryRepository, favoriteCityRepository);
    }

    @Test
    @DisplayName("天气代码映射测试 - 晴朗")
    void testWeatherCodeMapperSunny() {
        assertEquals("晴朗", WeatherCodeMapper.describe(0));
        assertEquals("大部晴朗", WeatherCodeMapper.describe(1));
    }

    @Test
    @DisplayName("天气代码映射测试 - 多云")
    void testWeatherCodeMapperCloudy() {
        assertEquals("局部多云", WeatherCodeMapper.describe(2));
        assertEquals("阴天", WeatherCodeMapper.describe(3));
    }

    @Test
    @DisplayName("天气代码映射测试 - 雨天")
    void testWeatherCodeMapperRainy() {
        assertEquals("小雨", WeatherCodeMapper.describe(61));
        assertEquals("中雨", WeatherCodeMapper.describe(63));
        assertEquals("大雨", WeatherCodeMapper.describe(65));
    }

    @Test
    @DisplayName("温度单位转换测试")
    void testTemperatureConversion() {
        assertEquals(25, TempUnit.CELSIUS.convert(25));
        assertEquals(77, TempUnit.FAHRENHEIT.convert(25));
        assertEquals(0, TempUnit.FAHRENHEIT.convert(0));
        assertEquals(32, TempUnit.FAHRENHEIT.convert(0));
    }

    @Test
    @DisplayName("紫外线描述测试")
    void testUvDescription() {
        assertEquals("低", WeatherCodeMapper.uvDescription(2.0));
        assertEquals("中等", WeatherCodeMapper.uvDescription(4.5));
        assertEquals("高", WeatherCodeMapper.uvDescription(7.0));
        assertEquals("很高", WeatherCodeMapper.uvDescription(9.5));
        assertEquals("极高", WeatherCodeMapper.uvDescription(12.0));
    }

    @Test
    @DisplayName("天气分类测试")
    void testWeatherCategory() {
        assertEquals(WeatherCodeMapper.WeatherCategory.SUNNY, WeatherCodeMapper.category(0));
        assertEquals(WeatherCodeMapper.WeatherCategory.CLOUDY, WeatherCodeMapper.category(2));
        assertEquals(WeatherCodeMapper.WeatherCategory.RAINY, WeatherCodeMapper.category(61));
        assertEquals(WeatherCodeMapper.WeatherCategory.SNOWY, WeatherCodeMapper.category(71));
        assertEquals(WeatherCodeMapper.WeatherCategory.THUNDER, WeatherCodeMapper.category(95));
    }

    @Test
    @DisplayName("Location转FavoriteLocation测试")
    void testToLocation() {
        Location location = Location.builder()
                .name("北京")
                .region("北京")
                .country("中国")
                .latitude(39.9042)
                .longitude(116.4074)
                .build();

        assertNotNull(location);
        assertEquals("北京", location.getName());
        assertEquals(39.9042, location.getLatitude());
        assertEquals(116.4074, location.getLongitude());
    }
}
