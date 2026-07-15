
package com.ai.weather.controller;

import com.ai.weather.service.WeatherService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WeatherApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherService weatherService;

    @Test
    @DisplayName("API - 搜索城市接口测试")
    void testSearchCityApi() throws Exception {
        mockMvc.perform(get("/api/weather/search")
                        .param("name", "北京"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("API - 获取天气接口测试")
    void testGetWeatherApi() throws Exception {
        mockMvc.perform(get("/api/weather/forecast")
                        .param("lat", "39.9042")
                        .param("lon", "116.4074")
                        .param("unit", "CELSIUS"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("API - 获取搜索历史接口测试")
    void testGetHistoryApi() throws Exception {
        mockMvc.perform(get("/api/history"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("API - 获取收藏列表接口测试")
    void testGetFavoritesApi() throws Exception {
        mockMvc.perform(get("/api/favorites"))
                .andExpect(status().isOk());
    }
}
