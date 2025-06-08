package com.example.myclubtrackers.network.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class LeaguesResponse {
    @SerializedName("response")
    private List<LeagueData> leagues;

    public List<LeagueData> getLeagues() {
        return leagues;
    }

    public static class LeagueData {
        @SerializedName("league")
        private League league;

        @SerializedName("country")
        private Country country;

        public int getId() {
            return league != null ? league.id : -1;
        }

        public String getName() {
            // Gabungkan nama liga dan negara jika ingin tampil lebih informatif
            if (league != null && country != null) {
                return league.name + " (" + country.name + ")";
            } else if (league != null) {
                return league.name;
            } else {
                return "";
            }
        }

        public static class League {
            @SerializedName("id")
            public int id;
            @SerializedName("name")
            public String name;
            @SerializedName("logo")
            public String logo;
        }

        public static class Country {
            @SerializedName("name")
            public String name;
        }
    }
}