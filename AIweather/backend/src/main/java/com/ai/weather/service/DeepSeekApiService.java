
package com.ai.weather.service;

import com.ai.weather.config.WeatherConfig;
import com.ai.weather.model.ChatMessage;
import com.ai.weather.model.DeepSeekRequest;
import com.ai.weather.model.DeepSeekResponse;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekApiService {

    private final OkHttpClient okHttpClient;
    private final WeatherConfig weatherConfig;
    private final Gson gson;

    public DeepSeekResponse chat(DeepSeekRequest request) throws IOException {
        String json = gson.toJson(request);

        RequestBody body = RequestBody.create(
                json,
                MediaType.parse("application/json; charset=utf-8")
        );

        Request httpRequest = new Request.Builder()
                .url(weatherConfig.getDeepseek().getApiUrl())
                .header("Authorization", "Bearer " + weatherConfig.getDeepseek().getApiKey())
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(httpRequest).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("DeepSeek API call failed: " + response.code());
            }
            ResponseBody responseBody = response.body();
            if (responseBody == null) {
                throw new IOException("Empty response body");
            }
            return gson.fromJson(responseBody.string(), DeepSeekResponse.class);
        }
    }
}
