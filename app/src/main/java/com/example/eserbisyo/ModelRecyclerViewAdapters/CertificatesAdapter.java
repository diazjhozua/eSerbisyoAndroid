package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Models.Certificate;
import com.example.eserbisyo.Models.Feedback;
import com.example.eserbisyo.Models.Requirement;
import com.example.eserbisyo.R;

import java.util.ArrayList;
import java.util.Collection;

public class CertificatesAdapter extends RecyclerView.Adapter<CertificatesAdapter.CertificatesHolder>{
    private final Context context;
    private final ArrayList<Certificate> list;
    private final ArrayList<Certificate> listAll;

    private CertificateRequirementsAdapter certificateRequirementsAdapter;

    public CertificatesAdapter(Context context, ArrayList<Certificate> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public CertificatesAdapter.CertificatesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_certificate, parent, false);
        return new CertificatesAdapter.CertificatesHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CertificatesAdapter.CertificatesHolder holder, int position) {
        Certificate mCertificate = list.get(position);
        holder.txtName.setText(mCertificate.getName());
        holder.txtPrice.setText("Price: ₱ " + mCertificate.getPrice());
        holder.txtDeliveryOpt.setText("Delivery Option: " + mCertificate.getDeliveryOption());
        if (!mCertificate.getStatus().equals("Available")) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.firebrick));
            holder.txtStatus.setText(mCertificate.getStatus());
        }

        holder.txtReqCount.setText("Requirements: (" + mCertificate.getRequirementsCount() + " Total)");

        if (mCertificate.getRequirementArrayList().size() <= 0) {
            holder.recyclerView.setVisibility(View.GONE);
        } else {
            holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
            certificateRequirementsAdapter = new CertificateRequirementsAdapter(context, mCertificate.getRequirementArrayList());
            holder.recyclerView.setAdapter(certificateRequirementsAdapter);
        }
    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Certificate> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Certificate mCertificate : listAll){
                    if(mCertificate.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(mCertificate.getPrice()).contains(constraint.toString().toLowerCase())
                            || mCertificate.getStatus().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mCertificate.getDeliveryOption().toLowerCase().contains(constraint.toString().toLowerCase())
                    ){
                        filteredList.add(mCertificate);
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
            list.addAll((Collection<? extends Certificate>) results.values);
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


    public class CertificatesHolder extends RecyclerView.ViewHolder {
        private final TextView txtName, txtPrice, txtStatus, txtDeliveryOpt, txtReqCount;
        private final RecyclerView recyclerView;

        public CertificatesHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtCertificate);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtDeliveryOpt = itemView.findViewById(R.id.txtDeliveryOpt);
            txtReqCount = itemView.findViewById(R.id.txtRequirementsCount);
            recyclerView = itemView.findViewById(R.id.recyclerView);
        }
    }
}
