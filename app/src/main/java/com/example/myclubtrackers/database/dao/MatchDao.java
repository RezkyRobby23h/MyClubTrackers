package com.example.myclubtrackers.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myclubtrackers.database.entity.MatchEntity;

import java.util.List;

@Dao
public interface MatchDao {
    @Query("SELECT * FROM matches ORDER BY date DESC, time DESC")
    List<MatchEntity> getAllMatches();

    @Query("SELECT * FROM matches WHERE id = :id")
    MatchEntity getMatchById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<MatchEntity> matches);

    @Query("DELETE FROM matches")
    void deleteAll();
}