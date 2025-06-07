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
import com.example.myclubtrackers.model.Club;

import java.util.ArrayList;
import java.util.List;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ClubViewHolder> {

    private final Context context;
    private final List<Club> clubList;
    private OnClubClickListener listener;

    public interface OnClubClickListener {
        void onClubClick(Club club);
    }

    public ClubAdapter(Context context) {
        this.context = context;
        this.clubList = new ArrayList<>();
    }

    public void setOnClubClickListener(OnClubClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ClubViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_club, parent, false);
        return new ClubViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClubViewHolder holder, int position) {
        Club club = clubList.get(position);

        holder.tvClubName.setText(club.getName());
        holder.tvClubCountry.setText(club.getCountry());

        Glide.with(context)
                .load(club.getLogo())
                .placeholder(R.drawable.club_placeholder)
                .error(R.drawable.club_placeholder)
                .into(holder.ivClubLogo);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClubClick(club);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clubList.size();
    }

    public void setClubs(List<Club> clubs) {
        clubList.clear();
        if (clubs != null) {
            clubList.addAll(clubs);
        }
        notifyDataSetChanged();
    }

    static class ClubViewHolder extends RecyclerView.ViewHolder {
        ImageView ivClubLogo;
        TextView tvClubName, tvClubCountry;

        ClubViewHolder(@NonNull View itemView) {
            super(itemView);
            ivClubLogo = itemView.findViewById(R.id.iv_club_logo);
            tvClubName = itemView.findViewById(R.id.tv_club_name);
            tvClubCountry = itemView.findViewById(R.id.tv_club_country);
        }
    }
}