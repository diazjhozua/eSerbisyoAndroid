package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Models.Feedback;
import com.example.eserbisyo.Models.Requirement;
import com.example.eserbisyo.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CertificateRequirementsAdapter extends RecyclerView.Adapter<CertificateRequirementsAdapter.CertificateRequirementsHolder>{
    private final Context context;
    private final ArrayList<Requirement> list;
    private final ArrayList<Requirement> listAll;

    public CertificateRequirementsAdapter(Context context, ArrayList<Requirement> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public CertificateRequirementsAdapter.CertificateRequirementsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_certificate_requirement, parent, false);
        return new CertificateRequirementsAdapter.CertificateRequirementsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateRequirementsAdapter.CertificateRequirementsHolder holder, int position) {
        Requirement mRequirement = list.get(position);
        holder.txtName.setText(mRequirement.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class CertificateRequirementsHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        public CertificateRequirementsHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
        }
    }
}
