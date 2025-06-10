package com.example.myclubtrackers.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myclubtrackers.R;
import com.example.myclubtrackers.adapter.MatchAdapter;
import com.example.myclubtrackers.databinding.FragmentHomeBinding;
import com.example.myclubtrackers.database.AppDatabase;
import com.example.myclubtrackers.database.entity.MatchEntity;
import com.example.myclubtrackers.model.Match;
import com.example.myclubtrackers.network.ApiService;
import com.example.myclubtrackers.network.RetrofitClient;
import com.example.myclubtrackers.network.response.FixturesResponse;
import com.example.myclubtrackers.utils.NetworkUtils;
import com.example.myclubtrackers.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MatchAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private AppDatabase database;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getInstance(requireContext());
        adapter = new MatchAdapter(requireContext());

        binding.recyclerMatches.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMatches.setAdapter(adapter);

        binding.swipeRefresh.setOnRefreshListener(this::loadMatches);
        binding.btnRefresh.setOnClickListener(v -> loadMatches());

        loadMatches();
    }

    private void loadMatches() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            loadPastAndUpcomingMatches();
        } else {
            loadMatchesFromDb();
            showNoNetworkView(true);
        }
    }

    private void loadPastAndUpcomingMatches() {
        binding.swipeRefresh.setRefreshing(true);
        showNoNetworkView(false);

        int leagueId = SharedPrefManager.getInstance(requireContext()).getFavoriteLeague();
        int currentYear = SharedPrefManager.getInstance(requireContext()).getSeasonYear();

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        // Call for next 5 matches (akan datang)
        Call<FixturesResponse> nextMatchesCall = apiService.getNextFixtures(leagueId, currentYear, 5);
        // Call for last 10 matches (sudah berlalu)
        Call<FixturesResponse> lastMatchesCall = apiService.getFixtures(leagueId, currentYear, 10);

        nextMatchesCall.enqueue(new Callback<FixturesResponse>() {
            @Override
            public void onResponse(@NonNull Call<FixturesResponse> call, @NonNull Response<FixturesResponse> response) {
                List<Match> matches = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    for (FixturesResponse.FixtureData fixtureData : response.body().getFixtures()) {
                        matches.add(fixtureData.toMatch());
                    }
                }
                // Setelah dapat match yang akan datang, lanjut ambil match yang sudah berlalu
                lastMatchesCall.enqueue(new Callback<FixturesResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<FixturesResponse> call, @NonNull Response<FixturesResponse> response) {
                        binding.swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null) {
                            for (FixturesResponse.FixtureData fixtureData : response.body().getFixtures()) {
                                matches.add(fixtureData.toMatch());
                            }
                        }
                        adapter.setMatches(matches);
                        showEmptyView(matches.isEmpty());

                        // Tambahkan proses simpan ke database setelah data dari API berhasil digabungkan
                        List<MatchEntity> matchEntities = new ArrayList<>();
                        for (Match match : matches) {
                            matchEntities.add(MatchEntity.fromMatch(match));
                        }
                        executor.execute(() -> {
                            database.matchDao().deleteAll();
                            database.matchDao().insertAll(matchEntities);
                        });
                    }

                    @Override
                    public void onFailure(@NonNull Call<FixturesResponse> call, @NonNull Throwable t) {
                        binding.swipeRefresh.setRefreshing(false);
                        adapter.setMatches(matches); // Tampilkan yang sudah didapat
                        showEmptyView(matches.isEmpty());
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<FixturesResponse> call, @NonNull Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                showError(t.getMessage());
                loadMatchesFromDb();
            }
        });
    }

    private void loadMatchesFromDb() {
        executor.execute(() -> {
            final List<MatchEntity> matchEntities = database.matchDao().getAllMatches();
            final List<Match> matches = new ArrayList<>();

            for (MatchEntity entity : matchEntities) {
                matches.add(entity.toMatch());
            }

            requireActivity().runOnUiThread(() -> {
                binding.swipeRefresh.setRefreshing(false);
                adapter.setMatches(matches);
                showEmptyView(matches.isEmpty());
            });
        });
    }

    private void showError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        showEmptyView(true);
    }

    private void showEmptyView(boolean show) {
        binding.emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.recyclerMatches.setVisibility(show ? View.GONE : View.VISIBLE);
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