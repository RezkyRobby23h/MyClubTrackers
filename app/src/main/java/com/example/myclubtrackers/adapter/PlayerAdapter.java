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
import com.bumptech.glide.request.RequestOptions;
import com.example.myclubtrackers.R;
import com.example.myclubtrackers.model.PlayerStats;

import java.util.ArrayList;
import java.util.List;

public class PlayerAdapter extends RecyclerView.Adapter<PlayerAdapter.PlayerViewHolder> {

    private final Context context;
    private final List<PlayerStats> playerList;
    private final boolean isTopScorer;

    public PlayerAdapter(Context context, boolean isTopScorer) {
        this.context = context;
        this.playerList = new ArrayList<>();
        this.isTopScorer = isTopScorer;
    }

    @NonNull
    @Override
    public PlayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_player, parent, false);
        return new PlayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerViewHolder holder, int position) {
        PlayerStats player = playerList.get(position);

        holder.tvPlayerName.setText(player.getName());
        holder.tvPlayerTeam.setText(player.getTeam());
        holder.tvPlayerPosition.setText(player.getPosition());

        if (isTopScorer) {
            holder.tvStatLabel.setText(R.string.goals);
            holder.tvStatValue.setText(String.valueOf(player.getGoals()));
        } else {
            holder.tvStatLabel.setText(R.string.assists);
            holder.tvStatValue.setText(String.valueOf(player.getAssists()));
        }

        Glide.with(context)
                .load(player.getImageUrl())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.player_placeholder)
                        .error(R.drawable.player_placeholder)
                        .circleCrop())
                .into(holder.ivPlayerImage);
    }

    @Override
    public int getItemCount() {
        return playerList.size();
    }

    public void setPlayers(List<PlayerStats> players) {
        playerList.clear();
        if (players != null) {
            playerList.addAll(players);
        }
        notifyDataSetChanged();
    }

    static class PlayerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPlayerImage;
        TextView tvPlayerName, tvPlayerTeam, tvPlayerPosition, tvStatLabel, tvStatValue;

        PlayerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPlayerImage = itemView.findViewById(R.id.iv_player_image);
            tvPlayerName = itemView.findViewById(R.id.tv_player_name);
            tvPlayerTeam = itemView.findViewById(R.id.tv_player_team);
            tvPlayerPosition = itemView.findViewById(R.id.tv_player_position);
            tvStatLabel = itemView.findViewById(R.id.tv_stat_label);
            tvStatValue = itemView.findViewById(R.id.tv_stat_value);
        }
    }
}