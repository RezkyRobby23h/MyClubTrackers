package com.example.myclubtrackers.network.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class StandingsResponse {
    @SerializedName("response")
    private List<LeagueStandings> response;

    public List<LeagueStandings> getResponse() {
        return response;
    }

    public static class LeagueStandings {
        @SerializedName("league")
        public League league;
    }

    public static class League {
        @SerializedName("standings")
        public List<List<Standing>> standings;
    }

    public static class Standing {
        @SerializedName("rank")
        public int rank;
        @SerializedName("team")
        public Team team;
        @SerializedName("points")
        public int points;
        @SerializedName("all")
        public AllStats all;
    }

    public static class Team {
        @SerializedName("id")
        public int id;
        @SerializedName("name")
        public String name;
        @SerializedName("logo")
        public String logo;
    }

    public static class AllStats {
        @SerializedName("played")
        public int played;
        @SerializedName("win")
        public int win;
        @SerializedName("draw")
        public int draw;
        @SerializedName("lose")
        public int lose;
    }
}