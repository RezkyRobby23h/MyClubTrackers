package com.example.myclubtrackers.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myclubtrackers.R;
import com.example.myclubtrackers.adapter.StandingsAdapter;
import com.example.myclubtrackers.databinding.FragmentClubBinding;
import com.example.myclubtrackers.database.AppDatabase;
import com.example.myclubtrackers.database.dao.StandingDao;
import com.example.myclubtrackers.database.entity.StandingEntity;
import com.example.myclubtrackers.network.ApiService;
import com.example.myclubtrackers.network.RetrofitClient;
import com.example.myclubtrackers.network.response.StandingsResponse;
import com.example.myclubtrackers.utils.NetworkUtils;
import com.example.myclubtrackers.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubFragment extends Fragment {

    private FragmentClubBinding binding;
    private StandingsAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClubBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new StandingsAdapter(requireContext());
        binding.recyclerClubs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerClubs.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadStandings);
        binding.btnRefresh.setOnClickListener(v -> loadStandings());

        loadStandings();
    }

    private void loadStandings() {
        binding.swipeRefresh.setRefreshing(true);
        showNoNetworkView(false);

        int leagueId = SharedPrefManager.getInstance(requireContext()).getFavoriteLeague();
        int currentYear = SharedPrefManager.getInstance(requireContext()).getSeasonYear();

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            loadStandingsOffline(leagueId, currentYear);
            return;
        }

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<StandingsResponse> call = apiService.getStandings(leagueId, currentYear);

        call.enqueue(new Callback<StandingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<StandingsResponse> call, @NonNull Response<StandingsResponse> response) {
                binding.swipeRefresh.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && !response.body().getResponse().isEmpty()) {
                    List<StandingsResponse.Standing> standings = response.body().getResponse().get(0).league.standings.get(0);
                    adapter.setStandings(standings);
                    showEmptyView(standings.isEmpty());
                    saveStandingsToDb(leagueId, currentYear, standings);
                } else {
                    showError(getString(R.string.error_loading_clubs));
                }
            }

            @Override
            public void onFailure(@NonNull Call<StandingsResponse> call, @NonNull Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                showError(t.getMessage());
                loadStandingsOffline(leagueId, currentYear);
            }
        });
    }

    private void saveStandingsToDb(int leagueId, int season, List<StandingsResponse.Standing> standings) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            StandingDao dao = db.standingDao();
            dao.deleteByLeagueAndSeason(leagueId, season);

            List<StandingEntity> entities = new ArrayList<>();
            for (StandingsResponse.Standing s : standings) {
                StandingEntity e = new StandingEntity();
                e.leagueId = leagueId;
                e.season = season;
                e.rank = s.rank;
                e.teamName = s.team.name;
                e.logoUrl = s.team.logo;
                e.points = s.points;
                e.played = s.all.played;
                e.win = s.all.win;
                e.draw = s.all.draw;
                e.lose = s.all.lose;
                entities.add(e);
            }
            dao.insertAll(entities);
        });
    }

    private void loadStandingsOffline(int leagueId, int season) {
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            StandingDao dao = db.standingDao();
            List<StandingEntity> entities = dao.getStandings(leagueId, season);

            List<StandingsResponse.Standing> standings = new ArrayList<>();
            for (StandingEntity e : entities) {
                StandingsResponse.Standing s = new StandingsResponse.Standing();
                s.rank = e.rank;
                s.team = new StandingsResponse.Team();
                s.team.name = e.teamName;
                s.team.logo = e.logoUrl;
                s.points = e.points;
                s.all = new StandingsResponse.AllStats();
                s.all.played = e.played;
                s.all.win = e.win;
                s.all.draw = e.draw;
                s.all.lose = e.lose;
                standings.add(s);
            }

            new Handler(Looper.getMainLooper()).post(() -> {
                binding.swipeRefresh.setRefreshing(false);
                adapter.setStandings(standings);
                showEmptyView(standings.isEmpty());
                if (entities.isEmpty()) {
                    showNoNetworkView(true);
                }
            });
        });
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        showEmptyView(true);
    }

    private void showEmptyView(boolean show) {
        binding.emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.recyclerClubs.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void showNoNetworkView(boolean show) {
        binding.noNetworkView.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.btnRefresh.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}