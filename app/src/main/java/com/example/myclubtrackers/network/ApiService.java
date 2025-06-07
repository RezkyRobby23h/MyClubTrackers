package com.example.myclubtrackers.network;

import com.example.myclubtrackers.network.response.ClubResponse;
import com.example.myclubtrackers.network.response.FixturesResponse;
import com.example.myclubtrackers.network.response.PlayersResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("fixtures")
    Call<FixturesResponse> getFixtures(
            @Query("league") int leagueId,
            @Query("season") int season,
            @Query("last") int lastMatches
    );

    @GET("fixtures")
    Call<FixturesResponse> getTeamFixtures(
            @Query("team") int teamId,
            @Query("season") int season,
            @Query("last") int lastMatches
    );

    @GET("players/topscorers")
    Call<PlayersResponse> getTopScorers(
            @Query("league") int leagueId,
            @Query("season") int season
    );

    @GET("players/topassists")
    Call<PlayersResponse> getTopAssists(
            @Query("league") int leagueId,
            @Query("season") int season
    );

    @GET("teams")
    Call<ClubResponse> getClubInfo(
            @Query("id") int teamId
    );
}