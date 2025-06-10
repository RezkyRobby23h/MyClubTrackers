package com.example.myclubtrackers.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myclubtrackers.R;
import com.example.myclubtrackers.network.response.StandingsResponse;

import java.util.ArrayList;
import java.util.List;

public class StandingsAdapter extends RecyclerView.Adapter<StandingsAdapter.StandingsViewHolder> {

    private final Context context;
    private final List<StandingsResponse.Standing> standings = new ArrayList<>();

    public StandingsAdapter(Context context) {
        this.context = context;
    }

    public void setStandings(List<StandingsResponse.Standing> standings) {
        this.standings.clear();
        if (standings != null) this.standings.addAll(standings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StandingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_standing, parent, false);
        return new StandingsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StandingsViewHolder holder, int position) {
        StandingsResponse.Standing standing = standings.get(position);
        holder.tvRank.setText(String.valueOf(standing.rank));
        holder.tvTeam.setText(standing.team.name);
        holder.tvPlayed.setText(String.valueOf(standing.all.played));
        holder.tvWin.setText(String.valueOf(standing.all.win));
        holder.tvDraw.setText(String.valueOf(standing.all.draw));
        holder.tvLose.setText(String.valueOf(standing.all.lose));
        holder.tvPoints.setText(String.valueOf(standing.points));
        Glide.with(context)
                .load(standing.team.logo)
                .placeholder(R.drawable.club_placeholder)
                .error(R.drawable.club_placeholder)
                .into(holder.ivLogo);
    }

    @Override
    public int getItemCount() {
        return standings.size();
    }

    static class StandingsViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvTeam, tvPlayed, tvWin, tvDraw, tvLose, tvPoints;
        ImageView ivLogo;

        StandingsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tv_rank);
            tvTeam = itemView.findViewById(R.id.tv_team);
            tvPlayed = itemView.findViewById(R.id.tv_played);
            tvWin = itemView.findViewById(R.id.tv_win);
            tvDraw = itemView.findViewById(R.id.tv_draw);
            tvLose = itemView.findViewById(R.id.tv_lose);
            tvPoints = itemView.findViewById(R.id.tv_points);
            ivLogo = itemView.findViewById(R.id.iv_team_logo);
        }
    }
}