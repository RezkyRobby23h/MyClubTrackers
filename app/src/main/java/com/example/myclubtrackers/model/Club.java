package com.example.myclubtrackers.model;

import java.io.Serializable;

public class Club implements Serializable {
    private int id;
    private String name;
    private String logo;
    private String country;
    private String league;
    private String stadium;
    private int founded;

    public Club() {
    }

    public Club(int id, String name, String logo, String country, String league, String stadium, int founded) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.country = country;
        this.league = league;
        this.stadium = stadium;
        this.founded = founded;
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