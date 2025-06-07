package com.example.myclubtrackers.network.response;

import com.example.myclubtrackers.model.PlayerStats;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlayersResponse {
    @SerializedName("response")
    private List<PlayerData> players;

    public List<PlayerData> getPlayers() {
        return players;
    }

    public static class PlayerData {
        @SerializedName("player")
        private PlayerInfo player;

        @SerializedName("statistics")
        private List<StatisticInfo> statistics;

        public PlayerStats toPlayerStats(boolean isTopScorer) {
            PlayerStats stats = new PlayerStats();
            stats.setId(player.id);
            stats.setName(player.name);
            stats.setImageUrl(player.photo);

            if (statistics != null && !statistics.isEmpty()) {
                StatisticInfo stat = statistics.get(0);
                stats.setTeam(stat.team.name);
                stats.setPosition(stat.games.position);
                stats.setGoals(stat.goals.total != null ? stat.goals.total : 0);
                stats.setAssists(stat.goals.assists != null ? stat.goals.assists : 0);
                stats.setTopScorer(isTopScorer);
            }

            return stats;
        }
    }

    public static class PlayerInfo {
        @SerializedName("id")
        private int id;

        @SerializedName("name")
        private String name;

        @SerializedName("photo")
        private String photo;
    }

    public static class StatisticInfo {
        @SerializedName("team")
        private TeamInfo team;

        @SerializedName("games")
        private GamesInfo games;

        @SerializedName("goals")
        private GoalsInfo goals;
    }

    public static class TeamInfo {
        @SerializedName("name")
        private String name;
    }

    public static class GamesInfo {
        @SerializedName("position")
        private String position;
    }

    public static class GoalsInfo {
        @SerializedName("total")
        private Integer total;

        @SerializedName("assists")
        private Integer assists;
    }
}