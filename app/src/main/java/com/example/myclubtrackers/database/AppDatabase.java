package com.example.myclubtrackers.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myclubtrackers.database.dao.ClubDao;
import com.example.myclubtrackers.database.dao.MatchDao;
import com.example.myclubtrackers.database.dao.PlayerStatsDao;
import com.example.myclubtrackers.database.entity.ClubEntity;
import com.example.myclubtrackers.database.entity.MatchEntity;
import com.example.myclubtrackers.database.entity.PlayerStatsEntity;
import com.example.myclubtrackers.database.entity.StandingEntity;
import com.example.myclubtrackers.database.dao.StandingDao;

@Database(entities = {MatchEntity.class, PlayerStatsEntity.class, ClubEntity.class, StandingEntity.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "football_db";
    private static AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract MatchDao matchDao();
    public abstract PlayerStatsDao playerStatsDao();
    public abstract ClubDao clubDao();
    public abstract StandingDao standingDao();
}