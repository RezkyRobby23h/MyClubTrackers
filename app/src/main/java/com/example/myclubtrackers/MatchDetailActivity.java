package com.example.myclubtrackers;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myclubtrackers.databinding.ActivityMatchDetailBinding;
import com.example.myclubtrackers.model.Match;
import com.example.myclubtrackers.utils.DateFormatter;
import com.example.myclubtrackers.utils.ThemeUtils;

public class MatchDetailActivity extends AppCompatActivity {

    private ActivityMatchDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applyTheme(this);
        super.onCreate(savedInstanceState);

        binding = ActivityMatchDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.match_details);
        }

        if (getIntent().hasExtra("match")) {
            Match match = (Match) getIntent().getSerializableExtra("match");
            if (match != null) {
                displayMatchDetails(match);
            }
        }
    }

    private void displayMatchDetails(Match match) {
        binding.tvHomeTeam.setText(match.getHomeTeam());
        binding.tvAwayTeam.setText(match.getAwayTeam());
        binding.tvScore.setText(String.format("%d - %d", match.getHomeScore(), match.getAwayScore()));
        binding.tvLeague.setText(match.getLeague());
        binding.tvDateTime.setText(String.format("%s %s",
                DateFormatter.formatApiDate(match.getDate()), match.getTime()));
        binding.tvVenue.setText(match.getVenue());
        binding.tvStatus.setText(match.getStatus());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}