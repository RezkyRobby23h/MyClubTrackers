package com.example.myclubtrackers.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.myclubtrackers.model.PlayerStats;

@Entity(tableName = "player_stats")
public class PlayerStatsEntity {
    @PrimaryKey
    private int id;
    private String name;
    private String position;
    private String team;
    private int goals;
    private int assists;
    private String imageUrl;
    private boolean isTopScorer;

    public static PlayerStatsEntity fromPlayerStats(PlayerStats stats) {
        PlayerStatsEntity entity = new PlayerStatsEntity();
        entity.id = stats.getId();
        entity.name = stats.getName();
        entity.position = stats.getPosition();
        entity.team = stats.getTeam();
        entity.goals = stats.getGoals();
        entity.assists = stats.getAssists();
        entity.imageUrl = stats.getImageUrl();
        entity.isTopScorer = stats.isTopScorer();
        return entity;
    }

    public PlayerStats toPlayerStats() {
        return new PlayerStats(id, name, position, team, goals, assists, imageUrl, isTopScorer);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getAssists() {
        return assists;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isTopScorer() {
        return isTopScorer;
    }

    public void setTopScorer(boolean topScorer) {
        isTopScorer = topScorer;
    }
}