package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class FeedbackTypesAdapter extends RecyclerView.Adapter<FeedbackTypesAdapter.FeedbackTypesHolder> {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private final Context context;
    private final ArrayList<Type> list;
    private final ArrayList<Type> listAll;

    public FeedbackTypesAdapter(Context context, ArrayList<Type> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public FeedbackTypesAdapter.FeedbackTypesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feedback_type, parent, false);
        return new FeedbackTypesAdapter.FeedbackTypesHolder(view);
    }
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FeedbackTypesAdapter.FeedbackTypesHolder holder, int position) {
        Type mType = list.get(position);
        df.setRoundingMode(RoundingMode.UP);
        holder.txtName.setText(mType.getName());
        holder.txtCount.setText(mType.getCount());
        holder.ratingBar.setRating((float) mType.getRating());
        holder.txtRating.setText("Rating: " + df.format(mType.getRating()) + "/5" );
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FeedbackTypesHolder extends RecyclerView.ViewHolder {
        private final TextView txtName, txtCount, txtRating;
        private final AppCompatRatingBar ratingBar;

        public FeedbackTypesHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtCount = itemView.findViewById(R.id.txtCount);
            txtRating = itemView.findViewById(R.id.txtRating);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
