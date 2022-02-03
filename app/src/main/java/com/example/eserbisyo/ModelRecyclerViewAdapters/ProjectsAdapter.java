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
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Models.Ordinance;
import com.example.eserbisyo.Models.Project;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewPDFActivity;

import java.util.ArrayList;
import java.util.Collection;

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsHolder>{

    private final Context context;
    private final ArrayList<Project> list;
    private final ArrayList<Project> listAll;

    public ProjectsAdapter(Context context, ArrayList<Project> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ProjectsAdapter.ProjectsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_project, parent, false);
        return new ProjectsAdapter.ProjectsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ProjectsHolder holder, int position) {
        Project project = list.get(position);
        if (project.getType().getId() == 0) {
            holder.txtType.setText("Type: " + project.getCustomType());
        } else {
            holder.txtType.setText("Type: " + project.getType().getName());
        }

        holder.txtType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent= new Intent(context, ViewPDFActivity.class);
                intent.putExtra("pdf_url", Api.STORAGE + project.getFilePath());
                context.startActivity(intent);
            }
        });

        holder.txtName.setText("Project: " + project.getName());
        holder.txtCost.setText("Budget Cost: ₱" + project.getCost());
        holder.txtStart.setText("Project Start: " + project.getProjectStart());
        holder.txtEnd.setText("Project End: " + project.getProjectEnd());
        holder.txtLoc.setText("Project Location: " + project.getLocation());
        holder.txtDesc.setText(project.getDescription());
        holder.txtCreatedAt.setText(project.getCreatedAt());

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent= new Intent(context, ViewPDFActivity.class);
                intent.putExtra("pdf_path", project.getFilePath());
                context.startActivity(intent);
            }
        });
    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Project> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Project project : listAll){
                    if(project.getType().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getCustomType().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getProjectStart().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getProjectEnd().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getLocation().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(project.getId()).contains(constraint.toString().toLowerCase())
                            || String.valueOf(project.getCost()).contains(constraint.toString().toLowerCase())){
                        filteredList.add(project);
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
            list.addAll((Collection<? extends Project>) results.values);
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

    public class ProjectsHolder extends RecyclerView.ViewHolder {
        private final TextView txtName, txtType, txtCost, txtStart, txtEnd, txtLoc, txtDesc, txtCreatedAt;

        public ProjectsHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtProjectName);
            txtType = itemView.findViewById(R.id.txtProjectType);
            txtCost = itemView.findViewById(R.id.txtProjectCost);
            txtStart = itemView.findViewById(R.id.txtProjectStart);
            txtEnd = itemView.findViewById(R.id.txtProjectEnd);
            txtLoc = itemView.findViewById(R.id.txtProjectLocation);
            txtDesc = itemView.findViewById(R.id.txtProjectDesc);
            txtCreatedAt = itemView.findViewById(R.id.txtProjectCreatedAt);
        }
    }
}
