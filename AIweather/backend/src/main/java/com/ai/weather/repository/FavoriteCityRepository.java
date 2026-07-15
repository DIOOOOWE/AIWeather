
package com.ai.weather.repository;

import com.ai.weather.entity.FavoriteCityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteCityRepository extends JpaRepository<FavoriteCityEntity, String> {

    @Query("SELECT f FROM FavoriteCityEntity f ORDER BY f.addedAt DESC")
    List<FavoriteCityEntity> findAllOrderByAddedAtDesc();

    @Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END FROM FavoriteCityEntity f WHERE f.cityName = :cityName")
    boolean existsByCityName(String cityName);

    @Modifying
    @Query("DELETE FROM FavoriteCityEntity f WHERE f.cityName = :cityName")
    void deleteByCityName(String cityName);
}
