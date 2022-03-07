package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.Models.Like;
import com.example.eserbisyo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikesAdapter extends RecyclerView.Adapter<LikesAdapter.LikesHolder> {
    private final ArrayList<Like> list;
    private final SharedPreferences sharedPreferences;

    public LikesAdapter(Context context, ArrayList<Like> list) {
        this.list = list;
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public LikesAdapter.LikesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_like,parent,false);
        return new LikesHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull LikesAdapter.LikesHolder holder, int position) {
        Like like = list.get(position);

        if (!like.getUser().getFilePath().isEmpty()) {
            Picasso.get().load(like.getUser().getFilePath()).fit().error(R.drawable.user).into(holder.imgProfile);
        }

        if (sharedPreferences.getInt(Pref.ID,0)!=like.getUser().getId()){
            holder.txtName.setText(like.getUser().getName());
        } else {
            holder.txtName.setText(like.getUser().getName() + " (You)");
        }

        holder.txtDate.setText(like.getCreatedAt());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class LikesHolder extends RecyclerView.ViewHolder {
        private final CircleImageView imgProfile;
        private final TextView txtName,txtDate;

        public LikesHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgLikeProfile);
            txtName = itemView.findViewById(R.id.txtLikeName);
            txtDate = itemView.findViewById(R.id.txtLikeDate);
        }
    }
}