package com.example.myclubtrackers.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myclubtrackers.model.Match;
import com.example.myclubtrackers.utils.StringListConverter;

import java.util.List;

@Entity(tableName = "matches")
public class MatchEntity {
    @PrimaryKey
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

    @TypeConverters(StringListConverter.class)
    private List<String> goalScorers; // Tambahan untuk pencetak gol

    public static MatchEntity fromMatch(Match match) {
        MatchEntity entity = new MatchEntity();
        entity.id = match.getId();
        entity.league = match.getLeague();
        entity.homeTeam = match.getHomeTeam();
        entity.awayTeam = match.getAwayTeam();
        entity.homeScore = match.getHomeScore();
        entity.awayScore = match.getAwayScore();
        entity.date = match.getDate();
        entity.time = match.getTime();
        entity.status = match.getStatus();
        entity.venue = match.getVenue();
        entity.homeLogo = match.getHomeLogo();
        entity.awayLogo = match.getAwayLogo();
        entity.goalScorers = match.getGoalScorers();
        return entity;
    }

    public Match toMatch() {
        return new Match(id, league, homeTeam, awayTeam, homeScore, awayScore, date, time, status, venue, homeLogo, awayLogo, goalScorers);
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