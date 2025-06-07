package com.example.myclubtrackers.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.myclubtrackers.model.Club;

@Entity(tableName = "clubs")
public class ClubEntity {
    @PrimaryKey
    private int id;
    private String name;
    private String logo;
    private String country;
    private String league;
    private String stadium;
    private int founded;

    public static ClubEntity fromClub(Club club) {
        ClubEntity entity = new ClubEntity();
        entity.id = club.getId();
        entity.name = club.getName();
        entity.logo = club.getLogo();
        entity.country = club.getCountry();
        entity.league = club.getLeague();
        entity.stadium = club.getStadium();
        entity.founded = club.getFounded();
        return entity;
    }

    public Club toClub() {
        return new Club(id, name, logo, country, league, stadium, founded);
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLeague() {
        return league;
    }

    public void setLeague(String league) {
        this.league = league;
    }

    public String getStadium() {
        return stadium;
    }

    public void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public int getFounded() {
        return founded;
    }

    public void setFounded(int founded) {
        this.founded = founded;
    }
}