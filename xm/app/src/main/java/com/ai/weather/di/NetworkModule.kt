package com.ai.weather.di

import com.ai.weather.data.remote.DeepSeekApiService
import com.ai.weather.data.remote.OpenMeteoApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

/**
 * 网络模块 - 提供Ktor HttpClient
 * 与参考项目的RetrofitClient静态单例不同：使用Hilt管理生命周期
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
        install(Logging) {
            level = LogLevel.NONE
        }
        defaultRequest {
            contentType(ContentType.Application.Json)
        }
    }

    @Provides
    @Singleton
    fun provideOpenMeteoApi(client: HttpClient): OpenMeteoApiService =
        OpenMeteoApiService(client)

    @Provides
    @Singleton
    fun provideDeepSeekApi(client: HttpClient): DeepSeekApiService =
        DeepSeekApiService(client)
}
