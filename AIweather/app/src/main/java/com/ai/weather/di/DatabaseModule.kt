package com.ai.weather.di

import android.content.Context
import androidx.room.Room
import com.ai.weather.data.local.FavoriteCityDao
import com.ai.weather.data.local.SearchHistoryDao
import com.ai.weather.data.local.WeatherDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): WeatherDatabase =
        Room.databaseBuilder(
            context,
            WeatherDatabase::class.java,
            "weather_db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideSearchHistoryDao(db: WeatherDatabase): SearchHistoryDao =
        db.searchHistoryDao()

    @Provides
    fun provideFavoriteCityDao(db: WeatherDatabase): FavoriteCityDao =
        db.favoriteCityDao()
}
