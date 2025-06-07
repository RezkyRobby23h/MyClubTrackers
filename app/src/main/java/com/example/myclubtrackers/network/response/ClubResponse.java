package com.example.myclubtrackers.network.response;

import com.example.myclubtrackers.model.Club;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClubResponse {
    @SerializedName("response")
    private List<TeamData> teams;

    public List<TeamData> getTeams() {
        return teams;
    }

    public static class TeamData {
        @SerializedName("team")
        private TeamInfo team;

        @SerializedName("venue")
        private VenueInfo venue;

        public Club toClub() {
            Club club = new Club();
            club.setId(team.id);
            club.setName(team.name);
            club.setLogo(team.logo);
            club.setCountry(team.country);
            club.setFounded(team.founded);

            if (venue != null) {
                club.setStadium(venue.name);
            }

            return club;
        }
    }

    public static class TeamInfo {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("country")
        private String country;

        @SerializedName("founded")
        private int founded;

        @SerializedName("logo")
        private String logo;
    }

    public static class VenueInfo {
        @SerializedName("name")
        private String name;
    }
}