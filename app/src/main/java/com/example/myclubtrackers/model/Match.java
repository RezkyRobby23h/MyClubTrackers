package com.example.myclubtrackers.model;

import java.io.Serializable;
import java.util.List;

public class Match implements Serializable {
    private int id;
    private String league;
    private String homeTeam;
    private String awayTeam;
    private int homeScore;
    private int awayScore;
    private String date;
    private String time;
    private String status;
    private String venue;
    private String homeLogo;
    private String awayLogo;
    private List<String> goalScorers; // Tambahan untuk pencetak gol

    public Match() {}

    public Match(int id, String league, String homeTeam, String awayTeam, int homeScore, int awayScore,
                 String date, String time, String status, String venue, String homeLogo, String awayLogo, List<String> goalScorers) {
        this.id = id;
        this.league = league;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.date = date;
        this.time = time;
        this.status = status;
        this.venue = venue;
        this.homeLogo = homeLogo;
        this.awayLogo = awayLogo;
        this.goalScorers = goalScorers;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLeague() { return league; }
    public void setLeague(String league) { this.league = league; }

    public String getHomeTeam() { return homeTeam; }
    public void setHomeTeam(String homeTeam) { this.homeTeam = homeTeam; }

    public String getAwayTeam() { return awayTeam; }
    public void setAwayTeam(String awayTeam) { this.awayTeam = awayTeam; }

    public int getHomeScore() { return homeScore; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }

    public int getAwayScore() { return awayScore; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }

    public String getHomeLogo() { return homeLogo; }
    public void setHomeLogo(String homeLogo) { this.homeLogo = homeLogo; }

    public String getAwayLogo() { return awayLogo; }
    public void setAwayLogo(String awayLogo) { this.awayLogo = awayLogo; }

    public List<String> getGoalScorers() { return goalScorers; }
    public void setGoalScorers(List<String> goalScorers) { this.goalScorers = goalScorers; }
}