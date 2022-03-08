package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Models.Feedback;
import com.example.eserbisyo.Models.Inquiry;
import com.example.eserbisyo.R;

import java.util.ArrayList;
import java.util.Collection;

public class InquiriesAdapter  extends RecyclerView.Adapter<InquiriesAdapter.InquiriesHolder> {

    private final Context context;
    private final ArrayList<Inquiry> list;
    private final ArrayList<Inquiry> listAll;

    public InquiriesAdapter(Context context, ArrayList<Inquiry> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public InquiriesAdapter.InquiriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_inquiry, parent, false);
        return new InquiriesAdapter.InquiriesHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InquiriesAdapter.InquiriesHolder holder, int position) {
        Inquiry inquiry = list.get(position);
        holder.txtID.setText("Inquiry #" + inquiry.getId());
        holder.txtAbout.setText("About: " + inquiry.getAbout());
        holder.txtMessage.setText("Context: " + inquiry.getMessage());

        holder.txtCreatedAt.setText(inquiry.getCreatedAt());
        holder.txtStatus.setText(inquiry.getStatus());
        switch (inquiry.getStatus()) {
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


        if (inquiry.getStatus().equals("Pending") || inquiry.getStatus().equals("Ignored")) {
            holder.txtAdminMessage.setVisibility(View.GONE);
            holder.txtRespondedAt.setVisibility(View.GONE);
        } else {
            holder.txtAdminMessage.setText("Admin Message: " + inquiry.getAdminRespond());
            holder.txtRespondedAt.setText("Responded at: " + inquiry.getRespondedAt());
            holder.txtAdminMessage.setVisibility(View.VISIBLE);
            holder.txtRespondedAt.setVisibility(View.VISIBLE);
        }
    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Inquiry> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Inquiry inquiry : listAll){
                    if(inquiry.getAbout().toLowerCase().contains(constraint.toString().toLowerCase())
                            || inquiry.getStatus().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(inquiry.getId()).contains(constraint.toString().toLowerCase())
                            || inquiry.getAdminRespond().toLowerCase().contains(constraint.toString().toLowerCase())
                            || inquiry.getMessage().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(inquiry);
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
            list.addAll((Collection<? extends Inquiry>) results.values);
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

    public class InquiriesHolder extends RecyclerView.ViewHolder {
        private final TextView txtID, txtAbout,txtMessage, txtStatus, txtAdminMessage, txtCreatedAt, txtRespondedAt;

        public InquiriesHolder(@NonNull View itemView) {
            super(itemView);

            txtID = itemView.findViewById(R.id.txtID);
            txtAbout = itemView.findViewById(R.id.txtAbout);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAdminMessage = itemView.findViewById(R.id.txtAdminMessage);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtRespondedAt = itemView.findViewById(R.id.txtUpdatedAt);
        }
    }
}
