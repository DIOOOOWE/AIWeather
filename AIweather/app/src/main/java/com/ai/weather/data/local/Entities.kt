package com.ai.weather.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 搜索历史实体
 */
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cityName: String,
    val region: String = "",
    val country: String = "",
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorite_cities")
data class FavoriteCityEntity(
    @PrimaryKey val cityName: String,
    val region: String = "",
    val country: String = "",
    val latitude: Double,
    val longitude: Double,
    val addedAt: Long = System.currentTimeMillis()
)
