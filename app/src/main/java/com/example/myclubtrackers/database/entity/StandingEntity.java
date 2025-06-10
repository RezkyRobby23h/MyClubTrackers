package com.example.myclubtrackers.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "standings")
public class StandingEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int leagueId;
    public int season;
    public int rank;
    public String teamName;
    public String logoUrl;
    public int points;
    public int played;
    public int win;
    public int draw;
    public int lose;
}