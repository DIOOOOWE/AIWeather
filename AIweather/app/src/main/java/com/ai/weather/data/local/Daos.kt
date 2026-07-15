package com.ai.weather.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT 20")
    fun observeAll(): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE cityName = :cityName")
    suspend fun deleteByName(cityName: String)

    @Query("DELETE FROM search_history")
    suspend fun clear()
}

@Dao
interface FavoriteCityDao {

    @Query("SELECT * FROM favorite_cities ORDER BY addedAt DESC")
    fun observeAll(): Flow<List<FavoriteCityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(entity: FavoriteCityEntity)

    @Query("DELETE FROM favorite_cities WHERE cityName = :cityName")
    suspend fun remove(cityName: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_cities WHERE cityName = :cityName)")
    fun isFavorite(cityName: String): Flow<Boolean>
}
