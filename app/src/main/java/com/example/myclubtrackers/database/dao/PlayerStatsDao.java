package com.example.myclubtrackers.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myclubtrackers.database.entity.PlayerStatsEntity;

import java.util.List;

@Dao
public interface PlayerStatsDao {
    @Query("SELECT * FROM player_stats WHERE isTopScorer = 1 ORDER BY goals DESC")
    List<PlayerStatsEntity> getTopScorers();

    @Query("SELECT * FROM player_stats WHERE isTopScorer = 0 ORDER BY assists DESC")
    List<PlayerStatsEntity> getTopAssists();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<PlayerStatsEntity> players);

    @Query("DELETE FROM player_stats WHERE isTopScorer = :isTopScorer")
    void deleteByType(boolean isTopScorer);
}