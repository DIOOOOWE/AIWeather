
package com.ai.weather.repository;

import com.ai.weather.entity.SearchHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistoryEntity, Long> {

    @Query("SELECT s FROM SearchHistoryEntity s ORDER BY s.timestamp DESC LIMIT 20")
    List<SearchHistoryEntity> findTop20OrderByTimestampDesc();

    @Modifying
    @Query("DELETE FROM SearchHistoryEntity s WHERE s.cityName = :cityName")
    void deleteByCityName(String cityName);

    @Modifying
    @Query("DELETE FROM SearchHistoryEntity")
    void clearAll();
}
