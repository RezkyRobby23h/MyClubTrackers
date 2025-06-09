package com.example.myclubtrackers.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myclubtrackers.R;
import com.example.myclubtrackers.databinding.FragmentSettingBinding;
import com.example.myclubtrackers.network.ApiService;
import com.example.myclubtrackers.network.RetrofitClient;
import com.example.myclubtrackers.network.response.LeaguesResponse;
import com.example.myclubtrackers.utils.SharedPrefManager;
import com.example.myclubtrackers.utils.ThemeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private final Map<String, Integer> leagueMap = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupLeagueSpinner();
        setupSeasonYearDropdown();

        binding.switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // The actual theme change will be applied when Save is clicked
        });

        binding.btnSaveSettings.setOnClickListener(v -> saveSettings());

        // Show last update time
        long lastUpdateTime = SharedPrefManager.getInstance(requireContext()).getLastUpdateTime();
        if (lastUpdateTime > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
            binding.tvLastUpdate.setText(getString(R.string.last_updated, sdf.format(new Date(lastUpdateTime))));
        } else {
            binding.tvLastUpdate.setVisibility(View.GONE);
        }
    }

    private void setupLeagueSpinner() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        apiService.getLeagues().enqueue(new Callback<LeaguesResponse>() {
            @Override
            public void onResponse(@NonNull Call<LeaguesResponse> call, @NonNull Response<LeaguesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LeaguesResponse.LeagueData> leagues = response.body().getLeagues();
                    List<String> leagueNames = new ArrayList<>();
                    leagueMap.clear();
                    for (LeaguesResponse.LeagueData league : leagues) {
                        leagueNames.add(league.getName());
                        leagueMap.put(league.getName(), league.getId());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            leagueNames
                    );
                    binding.autoLeague.setAdapter(adapter);
                    loadCurrentSettings(); // Set selection after loading
                } else {
                    Toast.makeText(requireContext(), "Gagal memuat daftar liga", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LeaguesResponse> call, @NonNull Throwable t) {
                Toast.makeText(requireContext(), "Gagal memuat daftar liga", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSeasonYearDropdown() {
        List<String> years = new ArrayList<>();
        for (int y = 2020; y <= 2025; y++) {
            years.add(String.valueOf(y));
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                years
        );
        binding.autoSeasonYear.setAdapter(yearAdapter);

        // Set tahun yang tersimpan
        int savedYear = SharedPrefManager.getInstance(requireContext()).getSeasonYear();
        binding.autoSeasonYear.setText(String.valueOf(savedYear), false);
    }

    private void loadCurrentSettings() {
        // Set the dark mode switch
        binding.switchTheme.setChecked(SharedPrefManager.getInstance(requireContext()).isDarkMode());

        // Set the selected league
        int currentLeagueId = SharedPrefManager.getInstance(requireContext()).getFavoriteLeague();
        for (Map.Entry<String, Integer> entry : leagueMap.entrySet()) {
            if (entry.getValue() == currentLeagueId) {
                binding.autoLeague.setText(entry.getKey(), false);
                break;
            }
        }

        // Set the selected season year
        int savedYear = SharedPrefManager.getInstance(requireContext()).getSeasonYear();
        binding.autoSeasonYear.setText(String.valueOf(savedYear), false);
    }

    private void saveSettings() {
        // Save theme setting
        boolean isDarkMode = binding.switchTheme.isChecked();
        SharedPrefManager.getInstance(requireContext()).setDarkMode(isDarkMode);

        // Save league preference
        String selectedLeague = binding.autoLeague.getText().toString();
        Integer leagueId = leagueMap.get(selectedLeague);
        if (leagueId != null) {
            SharedPrefManager.getInstance(requireContext()).setFavoriteLeague(leagueId);
        }

        // Save season year
        String selectedYear = binding.autoSeasonYear.getText().toString();
        try {
            int year = Integer.parseInt(selectedYear);
            SharedPrefManager.getInstance(requireContext()).setSeasonYear(year);
        } catch (NumberFormatException ignored) {}

        Toast.makeText(requireContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();

        // Apply theme if it changed
        if (isDarkMode != ThemeUtils.isNightModeActive(requireContext())) {
            ThemeUtils.applyTheme(requireContext());
            requireActivity().recreate();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}