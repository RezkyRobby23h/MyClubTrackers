package com.example.myclubtrackers.model;

import java.io.Serializable;

public class PlayerStats implements Serializable {
    private int id;
    private String name;
    private String position;
    private String team;
    private int goals;
    private int assists;
    private String imageUrl;
    private boolean isTopScorer; // To differentiate between top scorers and assisters

    public PlayerStats() {
    }

    public PlayerStats(int id, String name, String position, String team, int goals, int assists,
                       String imageUrl, boolean isTopScorer) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.team = team;
        this.goals = goals;
        this.assists = assists;
        this.imageUrl = imageUrl;
        this.isTopScorer = isTopScorer;
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