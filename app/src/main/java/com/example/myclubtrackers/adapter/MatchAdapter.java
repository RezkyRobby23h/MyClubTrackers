package com.example.myclubtrackers.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myclubtrackers.MatchDetailActivity;
import com.example.myclubtrackers.R;
import com.example.myclubtrackers.model.Match;
import com.example.myclubtrackers.utils.DateFormatter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private final Context context;
    private final List<Match> matchList;

    public MatchAdapter(Context context) {
        this.context = context;
        this.matchList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_match, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        Match match = matchList.get(position);

        holder.tvHomeTeam.setText(match.getHomeTeam());
        holder.tvAwayTeam.setText(match.getAwayTeam());

        Glide.with(context)
        .load(match.getHomeLogo())
        .placeholder(R.drawable.club_placeholder)
        .error(R.drawable.club_placeholder)
        .into(holder.ivHomeLogo);

        Glide.with(context)
        .load(match.getAwayLogo())
        .placeholder(R.drawable.club_placeholder)
        .error(R.drawable.club_placeholder)
        .into(holder.ivAwayLogo);

        // Jika pertandingan belum dimulai, tampilkan kosong saja di skor
        if ("NS".equalsIgnoreCase(match.getStatus()) || "TBD".equalsIgnoreCase(match.getStatus())) {
            holder.tvScore.setText(""); // atau bisa pakai "-" jika ingin ada strip
        } else {
            holder.tvScore.setText(String.format("%d - %d", match.getHomeScore(), match.getAwayScore()));
        }

        holder.tvLeague.setText(match.getLeague());
        holder.tvDate.setText(DateFormatter.formatApiDate(match.getDate()));
        holder.tvTime.setText(match.getTime());
        holder.tvStatus.setText(match.getStatus());

        holder.cardMatch.setOnClickListener(v -> {
            Intent intent = new Intent(context, MatchDetailActivity.class);
            intent.putExtra("match", match);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return matchList.size();
    }

    public void setMatches(List<Match> matches) {
        matchList.clear();
        if (matches != null) {
            matchList.addAll(matches);
        }
        notifyDataSetChanged();
    }

    static class MatchViewHolder extends RecyclerView.ViewHolder {
    CardView cardMatch;
    TextView tvHomeTeam, tvAwayTeam, tvScore, tvLeague, tvDate, tvTime, tvStatus;
    ImageView ivHomeLogo, ivAwayLogo;

    MatchViewHolder(@NonNull View itemView) {
        super(itemView);
        cardMatch = itemView.findViewById(R.id.card_match);
        tvHomeTeam = itemView.findViewById(R.id.tv_home_team);
        tvAwayTeam = itemView.findViewById(R.id.tv_away_team);
        tvScore = itemView.findViewById(R.id.tv_score);
        tvLeague = itemView.findViewById(R.id.tv_league);
        tvDate = itemView.findViewById(R.id.tv_date);
        tvTime = itemView.findViewById(R.id.tv_time);
        tvStatus = itemView.findViewById(R.id.tv_status);
        ivHomeLogo = itemView.findViewById(R.id.iv_home_logo);
        ivAwayLogo = itemView.findViewById(R.id.iv_away_logo);
    }
}
}