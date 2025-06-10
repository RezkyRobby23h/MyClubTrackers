package com.example.myclubtrackers.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.myclubtrackers.R;
import com.example.myclubtrackers.adapter.PlayerAdapter;
import com.example.myclubtrackers.database.AppDatabase;
import com.example.myclubtrackers.database.entity.PlayerStatsEntity;
import com.example.myclubtrackers.databinding.FragmentPlayerListBinding;
import com.example.myclubtrackers.databinding.FragmentTopPlayerBinding;
import com.example.myclubtrackers.model.PlayerStats;
import com.example.myclubtrackers.network.ApiService;
import com.example.myclubtrackers.network.RetrofitClient;
import com.example.myclubtrackers.network.response.PlayersResponse;
import com.example.myclubtrackers.utils.NetworkUtils;
import com.example.myclubtrackers.utils.SharedPrefManager;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopPlayerFragment extends Fragment {

    private FragmentTopPlayerBinding binding;
    private TopPlayerPagerAdapter pagerAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTopPlayerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();

        if (!NetworkUtils.isNetworkAvailable(requireContext())) {
            showNoNetworkView(true);
        } else {
            showNoNetworkView(false);
        }

        binding.btnRefresh.setOnClickListener(v -> {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                showNoNetworkView(false);
                refreshFragments();
            } else {
                Toast.makeText(requireContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupViewPager() {
        pagerAdapter = new TopPlayerPagerAdapter(requireActivity());
        binding.viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.top_scorers);
                    break;
                case 1:
                    tab.setText(R.string.top_assists);
                    break;
            }
        }).attach();
    }

    private void refreshFragments() {
        int currentItem = binding.viewPager.getCurrentItem();
        pagerAdapter = new TopPlayerPagerAdapter(requireActivity());
        binding.viewPager.setAdapter(pagerAdapter);
        binding.viewPager.setCurrentItem(currentItem);
    }

    private void showNoNetworkView(boolean show) {
        binding.noNetworkView.setVisibility(show ? View.VISIBLE : View.GONE);
        // binding.viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
        // binding.tabLayout.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private static class TopPlayerPagerAdapter extends FragmentStateAdapter {

        public TopPlayerPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new TopScorerFragment();
            } else {
                return new TopAssistFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }

    // TopScorerFragment implementation
    public static class TopScorerFragment extends Fragment {
        private FragmentPlayerListBinding binding;
        private PlayerAdapter adapter;
        private final Executor executor = Executors.newSingleThreadExecutor();
        private AppDatabase database;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = FragmentPlayerListBinding.inflate(inflater, container, false);
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            database = AppDatabase.getInstance(requireContext());
            adapter = new PlayerAdapter(requireContext(), true); // true for top scorers

            binding.recyclerPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.recyclerPlayers.setAdapter(adapter);

            binding.swipeRefresh.setOnRefreshListener(this::loadTopScorers);
            binding.btnRefresh.setOnClickListener(v -> loadTopScorers());

            loadTopScorers();
        }

        private void loadTopScorers() {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                loadTopScorersFromApi();
            } else {
                loadTopScorersFromDb();
                showNoNetworkView(true);
            }
        }

        private void loadTopScorersFromApi() {
            binding.swipeRefresh.setRefreshing(true);
            showNoNetworkView(false);

            int leagueId = SharedPrefManager.getInstance(requireContext()).getFavoriteLeague();
            int currentYear = 2024; // You might want to calculate this dynamically

            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<PlayersResponse> call = apiService.getTopScorers(leagueId, currentYear);

            call.enqueue(new Callback<PlayersResponse>() {
                @Override
                public void onResponse(@NonNull Call<PlayersResponse> call, @NonNull Response<PlayersResponse> response) {
                    binding.swipeRefresh.setRefreshing(false);

                    if (response.isSuccessful() && response.body() != null) {
                        List<PlayerStats> players = new ArrayList<>();
                        List<PlayerStatsEntity> playerEntities = new ArrayList<>();

                        for (PlayersResponse.PlayerData playerData : response.body().getPlayers()) {
                            PlayerStats player = playerData.toPlayerStats(true); // true for top scorer
                            players.add(player);
                            playerEntities.add(PlayerStatsEntity.fromPlayerStats(player));
                        }

                        adapter.setPlayers(players);

                        // Save to database in background
                        executor.execute(() -> {
                            database.playerStatsDao().deleteByType(true); // true for top scorers
                            database.playerStatsDao().insertAll(playerEntities);

                            SharedPrefManager.getInstance(requireContext()).setLastUpdateTime(System.currentTimeMillis());
                        });

                        showEmptyView(players.isEmpty());
                    } else {
                        showError(getString(R.string.error_loading_players));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PlayersResponse> call, @NonNull Throwable t) {
                    binding.swipeRefresh.setRefreshing(false);
                    showError(t.getMessage());
                    loadTopScorersFromDb();
                }
            });
        }

        private void loadTopScorersFromDb() {
            executor.execute(() -> {
                final List<PlayerStatsEntity> playerEntities = database.playerStatsDao().getTopScorers();
                final List<PlayerStats> players = new ArrayList<>();

                for (PlayerStatsEntity entity : playerEntities) {
                    players.add(entity.toPlayerStats());
                }

                requireActivity().runOnUiThread(() -> {
                    binding.swipeRefresh.setRefreshing(false);
                    adapter.setPlayers(players);
                    showEmptyView(players.isEmpty());
                });
            });
        }

        private void showError(String message) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            showEmptyView(true);
        }

        private void showEmptyView(boolean show) {
            binding.emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.recyclerPlayers.setVisibility(show ? View.GONE : View.VISIBLE);
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

    // TopAssistFragment implementation - similar structure but for assists
    public static class TopAssistFragment extends Fragment {
        private FragmentPlayerListBinding binding;
        private PlayerAdapter adapter;
        private final Executor executor = Executors.newSingleThreadExecutor();
        private AppDatabase database;

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            binding = FragmentPlayerListBinding.inflate(inflater, container, false);
            return binding.getRoot();
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            database = AppDatabase.getInstance(requireContext());
            adapter = new PlayerAdapter(requireContext(), false); // false for top assists

            binding.recyclerPlayers.setLayoutManager(new LinearLayoutManager(requireContext()));
            binding.recyclerPlayers.setAdapter(adapter);

            binding.swipeRefresh.setOnRefreshListener(this::loadTopAssists);
            binding.btnRefresh.setOnClickListener(v -> loadTopAssists());

            loadTopAssists();
        }

        private void loadTopAssists() {
            if (NetworkUtils.isNetworkAvailable(requireContext())) {
                loadTopAssistsFromApi();
            } else {
                loadTopAssistsFromDb();
                showNoNetworkView(true);
            }
        }

        private void loadTopAssistsFromApi() {
            binding.swipeRefresh.setRefreshing(true);
            showNoNetworkView(false);

            int leagueId = SharedPrefManager.getInstance(requireContext()).getFavoriteLeague();
            int currentYear = 2024; // You might want to calculate this dynamically

            ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
            Call<PlayersResponse> call = apiService.getTopAssists(leagueId, currentYear);

            call.enqueue(new Callback<PlayersResponse>() {
                @Override
                public void onResponse(@NonNull Call<PlayersResponse> call, @NonNull Response<PlayersResponse> response) {
                    binding.swipeRefresh.setRefreshing(false);

                    if (response.isSuccessful() && response.body() != null) {
                        List<PlayerStats> players = new ArrayList<>();
                        List<PlayerStatsEntity> playerEntities = new ArrayList<>();

                        for (PlayersResponse.PlayerData playerData : response.body().getPlayers()) {
                            PlayerStats player = playerData.toPlayerStats(false); // false for top assist
                            players.add(player);
                            playerEntities.add(PlayerStatsEntity.fromPlayerStats(player));
                        }

                        adapter.setPlayers(players);

                        // Save to database in background
                        executor.execute(() -> {
                            database.playerStatsDao().deleteByType(false); // false for top assists
                            database.playerStatsDao().insertAll(playerEntities);

                            SharedPrefManager.getInstance(requireContext()).setLastUpdateTime(System.currentTimeMillis());
                        });

                        showEmptyView(players.isEmpty());
                    } else {
                        showError(getString(R.string.error_loading_players));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<PlayersResponse> call, @NonNull Throwable t) {
                    binding.swipeRefresh.setRefreshing(false);
                    showError(t.getMessage());
                    loadTopAssistsFromDb();
                }
            });
        }

        private void loadTopAssistsFromDb() {
            executor.execute(() -> {
                final List<PlayerStatsEntity> playerEntities = database.playerStatsDao().getTopAssists();
                final List<PlayerStats> players = new ArrayList<>();

                for (PlayerStatsEntity entity : playerEntities) {
                    players.add(entity.toPlayerStats());
                }

                requireActivity().runOnUiThread(() -> {
                    binding.swipeRefresh.setRefreshing(false);
                    adapter.setPlayers(players);
                    showEmptyView(players.isEmpty());
                });
            });
        }

        private void showError(String message) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            showEmptyView(true);
        }

        private void showEmptyView(boolean show) {
            binding.emptyView.setVisibility(show ? View.VISIBLE : View.GONE);
            binding.recyclerPlayers.setVisibility(show ? View.GONE : View.VISIBLE);
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
}