package com.example.myclubtrackers.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.myclubtrackers.R;
import com.example.myclubtrackers.adapter.ClubAdapter;
import com.example.myclubtrackers.databinding.FragmentClubBinding;
import com.example.myclubtrackers.database.AppDatabase;
import com.example.myclubtrackers.database.entity.ClubEntity;
import com.example.myclubtrackers.model.Club;
import com.example.myclubtrackers.network.ApiService;
import com.example.myclubtrackers.network.RetrofitClient;
import com.example.myclubtrackers.network.response.ClubResponse;
import com.example.myclubtrackers.utils.NetworkUtils;
import com.example.myclubtrackers.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ClubFragment extends Fragment {

    private FragmentClubBinding binding;
    private ClubAdapter adapter;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private AppDatabase database;
    private Club selectedClub;

    // List of top club IDs per league
    private final int[][] clubIdsByLeague = {
            // Premier League (England)
            {33, 40, 42, 39, 49, 47, 51, 36},
            // La Liga (Spain)
            {529, 530, 541, 532, 798, 546, 536, 533},
            // Bundesliga (Germany)
            {157, 165, 159, 167, 160, 168, 161, 169},
            // Serie A (Italy)
            {489, 496, 488, 487, 497, 505, 499, 492},
            // Ligue 1 (France)
            {85, 91, 77, 79, 81, 84, 95, 93}
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentClubBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = AppDatabase.getInstance(requireContext());
        adapter = new ClubAdapter(requireContext());

        binding.recyclerClubs.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.recyclerClubs.setAdapter(adapter);

        adapter.setOnClubClickListener(club -> {
            selectedClub = club;
            showClubDetails();
        });

        binding.swipeRefresh.setOnRefreshListener(this::loadClubs);
        binding.btnRefresh.setOnClickListener(v -> loadClubs());
        binding.btnCloseDetails.setOnClickListener(v -> hideClubDetails());

        loadClubs();
    }

    private void loadClubs() {
        if (NetworkUtils.isNetworkAvailable(requireContext())) {
            loadClubsFromApi();
        } else {
            loadClubsFromDb();
            showNoNetworkView(true);
        }
    }

    private void loadClubsFromApi() {
        binding.swipeRefresh.setRefreshing(true);
        showNoNetworkView(false);

        int leagueId = SharedPrefManager.getInstance(requireContext()).getFavoriteLeague();

        // Determine the index of club IDs to use based on the league
        int index;
        if (leagueId == 39) index = 0;      // Premier League
        else if (leagueId == 140) index = 1; // La Liga
        else if (leagueId == 78) index = 2;  // Bundesliga
        else if (leagueId == 135) index = 3; // Serie A
        else if (leagueId == 61) index = 4;  // Ligue 1
        else index = 0; // Default to Premier League

        // Get the IDs of top clubs in the selected league
        int[] clubIds = clubIdsByLeague[index];

        // Create a list to store all club data
        final List<Club> allClubs = new ArrayList<>();
        final List<ClubEntity> allClubEntities = new ArrayList<>();

        // Counter to track API requests
        final int[] requestsCompleted = {0};

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);

        for (int clubId : clubIds) {
            Call<ClubResponse> call = apiService.getClubInfo(clubId);

            call.enqueue(new Callback<ClubResponse>() {
                @Override
                public void onResponse(@NonNull Call<ClubResponse> call, @NonNull Response<ClubResponse> response) {
                    requestsCompleted[0]++;

                    if (response.isSuccessful() && response.body() != null &&
                            !response.body().getTeams().isEmpty()) {

                        Club club = response.body().getTeams().get(0).toClub();
                        allClubs.add(club);
                        allClubEntities.add(ClubEntity.fromClub(club));
                    }

                    // If this is the last request, update UI and save to database
                    if (requestsCompleted[0] == clubIds.length) {
                        binding.swipeRefresh.setRefreshing(false);
                        adapter.setClubs(allClubs);
                        showEmptyView(allClubs.isEmpty());

                        // Save to database in background
                        executor.execute(() -> {
                            database.clubDao().deleteAll();
                            database.clubDao().insertAll(allClubEntities);
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ClubResponse> call, @NonNull Throwable t) {
                    requestsCompleted[0]++;

                    // If this is the last request, update UI
                    if (requestsCompleted[0] == clubIds.length) {
                        binding.swipeRefresh.setRefreshing(false);

                        if (allClubs.isEmpty()) {
                            showError(t.getMessage());
                            loadClubsFromDb();
                        } else {
                            adapter.setClubs(allClubs);
                            showEmptyView(false);

                            // Save any clubs we did manage to get
                            executor.execute(() -> {
                                database.clubDao().deleteAll();
                                database.clubDao().insertAll(allClubEntities);
                            });
                        }
                    }
                }
            });
        }
    }

    private void loadClubsFromDb() {
        executor.execute(() -> {
            final List<ClubEntity> clubEntities = database.clubDao().getAllClubs();
            final List<Club> clubs = new ArrayList<>();

            for (ClubEntity entity : clubEntities) {
                clubs.add(entity.toClub());
            }

            requireActivity().runOnUiThread(() -> {
                binding.swipeRefresh.setRefreshing(false);
                adapter.setClubs(clubs);
                showEmptyView(clubs.isEmpty());
            });
        });
    }

    private void showClubDetails() {
        if (selectedClub == null) return;

        binding.tvSelectedClubName.setText(selectedClub.getName());

        String clubInfo = getString(R.string.stadium) + ": " + selectedClub.getStadium() + " | " +
                getString(R.string.founded) + ": " + selectedClub.getFounded();
        binding.tvSelectedClubInfo.setText(clubInfo);

        Glide.with(requireContext())
                .load(selectedClub.getLogo())
                .placeholder(R.drawable.club_placeholder)
                .error(R.drawable.club_placeholder)
                .into(binding.ivSelectedClubLogo);

        binding.clubDetailCard.setVisibility(View.VISIBLE);
    }

    private void hideClubDetails() {
        binding.clubDetailCard.setVisibility(View.GONE);
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