package com.example.myclubtrackers.network.response;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class SeasonsResponse {
    @SerializedName("response")
    private List<Integer> seasons;

    public List<Integer> getSeasons() {
        return seasons != null ? seasons : new ArrayList<>();
    }
}