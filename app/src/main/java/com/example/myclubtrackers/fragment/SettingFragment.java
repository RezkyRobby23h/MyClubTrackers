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
import com.example.myclubtrackers.utils.SharedPrefManager;
import com.example.myclubtrackers.utils.ThemeUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        loadCurrentSettings();

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
        // Map league names to their API IDs
        leagueMap.put("Premier League (England)", 39);
        leagueMap.put("La Liga (Spain)", 140);
        leagueMap.put("Bundesliga (Germany)", 78);
        leagueMap.put("Serie A (Italy)", 135);
        leagueMap.put("Ligue 1 (France)", 61);
        leagueMap.put("K-League (Jepang)", 98);

        String[] leagueNames = leagueMap.keySet().toArray(new String[0]);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                leagueNames
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerLeague.setAdapter(adapter);
    }

    private void loadCurrentSettings() {
        // Set the dark mode switch
        binding.switchTheme.setChecked(SharedPrefManager.getInstance(requireContext()).isDarkMode());

        // Set the selected league
        int currentLeagueId = SharedPrefManager.getInstance(requireContext()).getFavoriteLeague();
        for (Map.Entry<String, Integer> entry : leagueMap.entrySet()) {
            if (entry.getValue() == currentLeagueId) {
                int position = ((ArrayAdapter) binding.spinnerLeague.getAdapter()).getPosition(entry.getKey());
                binding.spinnerLeague.setSelection(position);
                break;
            }
        }
    }

    private void saveSettings() {
        // Save theme setting
        boolean isDarkMode = binding.switchTheme.isChecked();
        SharedPrefManager.getInstance(requireContext()).setDarkMode(isDarkMode);

        // Save league preference
        String selectedLeague = binding.spinnerLeague.getSelectedItem().toString();
        Integer leagueId = leagueMap.get(selectedLeague);
        if (leagueId != null) {
            SharedPrefManager.getInstance(requireContext()).setFavoriteLeague(leagueId);
        }

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