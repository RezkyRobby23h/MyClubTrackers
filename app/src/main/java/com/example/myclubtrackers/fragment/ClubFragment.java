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
        int currentYear = SharedPrefManager.getInstance(requireContext()).getSeasonYear();

        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        Call<ClubResponse> call = apiService.getClubsByLeague(leagueId, currentYear);

        call.enqueue(new Callback<ClubResponse>() {
            @Override
            public void onResponse(@NonNull Call<ClubResponse> call, @NonNull Response<ClubResponse> response) {
                binding.swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null && !response.body().getTeams().isEmpty()) {
                    List<Club> clubs = new ArrayList<>();
                    List<ClubEntity> clubEntities = new ArrayList<>();

                    for (ClubResponse.TeamData teamData : response.body().getTeams()) {
                        Club club = teamData.toClub();
                        clubs.add(club);
                        clubEntities.add(ClubEntity.fromClub(club));
                    }

                    adapter.setClubs(clubs);
                    showEmptyView(clubs.isEmpty());

                    executor.execute(() -> {
                        database.clubDao().deleteAll();
                        database.clubDao().insertAll(clubEntities);
                    });
                } else {
                    showError(getString(R.string.error_loading_clubs));
                    loadClubsFromDb();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ClubResponse> call, @NonNull Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                showError(t.getMessage());
                loadClubsFromDb();
            }
        });
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