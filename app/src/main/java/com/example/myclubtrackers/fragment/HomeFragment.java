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
            loadMatchesFromApi();
        } else {
            loadMatchesFromDb();
            showNoNetworkView(true);
        }
    }

    private void loadMatchesFromApi() {
        binding.swipeRefresh.setRefreshing(true);
        showNoNetworkView(false);

        int leagueId = SharedPrefManager.getInstance(requireContext()).getFavoriteLeague();
        int currentYear = 2024; // You might want to calculate this dynamically

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<FixturesResponse> call = apiService.getFixtures(leagueId, currentYear, 10); // Last 10 matches

        call.enqueue(new Callback<FixturesResponse>() {
            @Override
            public void onResponse(@NonNull Call<FixturesResponse> call, @NonNull Response<FixturesResponse> response) {
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    List<Match> matches = new ArrayList<>();
                    List<MatchEntity> matchEntities = new ArrayList<>();

                    for (FixturesResponse.FixtureData fixtureData : response.body().getFixtures()) {
                        Match match = fixtureData.toMatch();
                        matches.add(match);
                        matchEntities.add(MatchEntity.fromMatch(match));
                    }

                    adapter.setMatches(matches);

                    // Save to database in background
                    executor.execute(() -> {
                        database.matchDao().deleteAll();
                        database.matchDao().insertAll(matchEntities);

                        SharedPrefManager.getInstance(requireContext()).setLastUpdateTime(System.currentTimeMillis());
                    });

                    showEmptyView(matches.isEmpty());
                } else {
                    showError(getString(R.string.error_loading_matches));
                }
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