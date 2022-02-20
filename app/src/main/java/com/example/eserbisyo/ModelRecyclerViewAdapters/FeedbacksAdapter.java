package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Models.Feedback;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewPDFActivity;

import java.util.ArrayList;
import java.util.Collection;

public class FeedbacksAdapter extends RecyclerView.Adapter<FeedbacksAdapter.FeedbacksHolder> {
    private final Context context;
    private final ArrayList<Feedback> list;
    private final ArrayList<Feedback> listAll;

    public FeedbacksAdapter(Context context, ArrayList<Feedback> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public FeedbacksAdapter.FeedbacksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_feedback, parent, false);
        return new FeedbacksHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FeedbacksAdapter.FeedbacksHolder holder, int position) {
        Feedback feedback = list.get(position);
        holder.txtFeedbackID.setText(context.getString(R.string.feedback_no) + feedback.getId());
        holder.txtSubmittedAs.setText("Submitted as: " + feedback.getSubmittedAs());
        if (feedback.getType().getId() == 0) {
            holder.txtType.setText("Type: " + feedback.getCustomType());
        } else {
            holder.txtType.setText("Type: " + feedback.getType().getName());
        }
        holder.ratingBar.setRating(feedback.getRating());
        holder.txtPolarity.setText("Rating: " + feedback.getRating() + "/5");
        holder.txtMessage.setText("Context: " + feedback.getMessage());
        holder.txtCreatedAt.setText(feedback.getCreatedAt());
        holder.txtStatus.setText(feedback.getStatus());
        switch (feedback.getStatus()) {
            case "Pending":
                holder.txtStatus.setTextColor(context.getResources().getColor(R.color.primaryColor));
                break;
            case "Ignored":
                holder.txtStatus.setTextColor(context.getResources().getColor(R.color.firebrick));
                break;
            case "Noted":
                holder.txtStatus.setTextColor(context.getResources().getColor(R.color.teal_700));
                break;
        }


        if (feedback.getStatus().equals("Pending") || feedback.getStatus().equals("Ignored")) {
            holder.txtAdminMessage.setVisibility(View.GONE);
            holder.txtRespondedAt.setVisibility(View.GONE);
        } else {
            holder.txtAdminMessage.setText("Admin Message: " + feedback.getAdminRespond());
            holder.txtRespondedAt.setText("Responded at: " + feedback.getRespondedAt());
            holder.txtAdminMessage.setVisibility(View.VISIBLE);
            holder.txtRespondedAt.setVisibility(View.VISIBLE);
        }


    }
    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Feedback> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Feedback feedback : listAll){
                    if(feedback.getType().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || feedback.getStatus().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(feedback.getId()).contains(constraint.toString().toLowerCase())
                            || feedback.getAdminRespond().toLowerCase().contains(constraint.toString().toLowerCase())
                            || feedback.getMessage().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(feedback);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Feedback>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class FeedbacksHolder extends RecyclerView.ViewHolder {
        private final TextView txtFeedbackID, txtSubmittedAs, txtType, txtPolarity, txtMessage, txtStatus, txtAdminMessage, txtCreatedAt, txtRespondedAt;
        private final AppCompatRatingBar ratingBar;

        public FeedbacksHolder(@NonNull View itemView) {
            super(itemView);
            txtFeedbackID = itemView.findViewById(R.id.txtID);
            txtSubmittedAs = itemView.findViewById(R.id.txtSubmittedAs);
            txtType = itemView.findViewById(R.id.txtType);
            txtPolarity = itemView.findViewById(R.id.txtPolarity);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAdminMessage = itemView.findViewById(R.id.txtAdminMessage);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtRespondedAt = itemView.findViewById(R.id.txtUpdatedAt);

            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }


}
