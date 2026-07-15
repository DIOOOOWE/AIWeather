
package com.ai.weather.service;

import com.ai.weather.config.WeatherConfig;
import com.ai.weather.model.GeocodingResult;
import com.ai.weather.model.WeatherForecastResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenMeteoApiService {

    private final OkHttpClient okHttpClient;
    private final WeatherConfig weatherConfig;
    private final Gson gson;

    public GeocodingResult searchCity(String name) throws IOException {
        String url = String.format("%s/v1/search?name=%s&count=10&language=zh&format=json",
                weatherConfig.getOpenMeteo().getGeoBaseUrl(),
                name);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            return gson.fromJson(body.string(), GeocodingResult.class);
        }
    }

    public WeatherForecastResponse getWeather(double lat, double lon) throws IOException {
        String url = String.format(
                "%s/v1/forecast?latitude=%f&longitude=%f&current=temperature_2m,relative_humidity_2m,apparent_temperature,is_day,pressure_msl&current_weather=true&hourly=temperature_2m,weathercode,relative_humidity_2m&daily=weathercode,temperature_2m_max,temperature_2m_min,sunrise,sunset,uv_index_max,precipitation_probability_max&timezone=Asia/Shanghai&forecast_days=7",
                weatherConfig.getOpenMeteo().getWeatherBaseUrl(),
                lat, lon);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            ResponseBody body = response.body();
            if (body == null) {
                throw new IOException("Empty response body");
            }
            return gson.fromJson(body.string(), WeatherForecastResponse.class);
        }
    }
}
