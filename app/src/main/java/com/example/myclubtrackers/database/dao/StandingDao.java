package com.example.myclubtrackers.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myclubtrackers.database.entity.StandingEntity;

import java.util.List;

@Dao
public interface StandingDao {
    @Query("SELECT * FROM standings WHERE leagueId = :leagueId AND season = :season ORDER BY rank ASC")
    List<StandingEntity> getStandings(int leagueId, int season);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<StandingEntity> standings);

    @Query("DELETE FROM standings WHERE leagueId = :leagueId AND season = :season")
    void deleteByLeagueAndSeason(int leagueId, int season);
}