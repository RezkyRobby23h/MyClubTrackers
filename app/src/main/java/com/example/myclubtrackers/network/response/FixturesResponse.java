package com.example.myclubtrackers.network.response;

import com.example.myclubtrackers.model.Match;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FixturesResponse {
    @SerializedName("response")
    private List<FixtureData> fixtures;

    public List<FixtureData> getFixtures() {
        return fixtures;
    }

    public static class FixtureData {
        @SerializedName("fixture")
        private FixtureInfo fixtureInfo;

        @SerializedName("league")
        private LeagueInfo league;

        @SerializedName("teams")
        private TeamsInfo teams;

        @SerializedName("goals")
        private GoalsInfo goals;

        @SerializedName("score")
        private ScoreInfo score;

        @SerializedName("events")
        private List<EventInfo> events; // Tambahan untuk events (pencetak gol)

        public Match toMatch() {
            Match match = new Match();
            match.setId(fixtureInfo.id);
            match.setDate(fixtureInfo.date.split("T")[0]);
            match.setTime(fixtureInfo.date.split("T")[1].substring(0, 5));
            match.setStatus(fixtureInfo.status.shortStatus);
            match.setVenue(fixtureInfo.venue.name);
            match.setLeague(league.name);
            match.setHomeTeam(teams.home.name);
            match.setAwayTeam(teams.away.name);
            match.setHomeScore(goals.home != null ? goals.home : 0);
            match.setAwayScore(goals.away != null ? goals.away : 0);
            match.setHomeLogo(teams.home.logo);
            match.setAwayLogo(teams.away.logo);

            // Parsing goal scorers
            List<String> scorers = new ArrayList<>();
            if (events != null) {
                for (EventInfo event : events) {
                    if ("Goal".equalsIgnoreCase(event.type) && event.player != null) {
                        scorers.add(event.player.name);
                    }
                }
            }
            match.setGoalScorers(scorers);

            return match;
        }
    }

    public static class FixtureInfo {
        @SerializedName("id")
        private int id;

        @SerializedName("date")
        private String date;

        @SerializedName("status")
        private StatusInfo status;

        @SerializedName("venue")
        private VenueInfo venue;
    }

    public static class StatusInfo {
        @SerializedName("short")
        private String shortStatus;
    }

    public static class VenueInfo {
        @SerializedName("name")
        private String name;
    }

    public static class LeagueInfo {
        @SerializedName("name")
        private String name;
    }

    public static class TeamsInfo {
        @SerializedName("home")
        private TeamInfo home;

        @SerializedName("away")
        private TeamInfo away;
    }

    public static class TeamInfo {
        @SerializedName("name")
        private String name;

        @SerializedName("logo")
        private String logo;
    }

    public static class GoalsInfo {
        @SerializedName("home")
        private Integer home;

        @SerializedName("away")
        private Integer away;
    }

    public static class ScoreInfo {
        // Add fields if you need more detailed scoring info
    }

    // Tambahan untuk parsing events (goal scorers)
    public static class EventInfo {
        @SerializedName("type")
        private String type;

        @SerializedName("player")
        private PlayerInfo player;
    }

    public static class PlayerInfo {
        @SerializedName("name")
        private String name;
    }
}