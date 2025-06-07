package com.example.myclubtrackers.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myclubtrackers.database.entity.ClubEntity;

import java.util.List;

@Dao
public interface ClubDao {
    @Query("SELECT * FROM clubs")
    List<ClubEntity> getAllClubs();

    @Query("SELECT * FROM clubs WHERE id = :id")
    ClubEntity getClubById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ClubEntity> clubs);

    @Query("DELETE FROM clubs")
    void deleteAll();
}