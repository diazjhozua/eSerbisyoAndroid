package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Models.Employee;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;

public class EmployeesAdapter extends RecyclerView.Adapter<EmployeesAdapter.EmployeesHolder>{
    private final Context context;
    private final ArrayList<Employee> list;
    private final ArrayList<Employee> listAll;

    public EmployeesAdapter(Context context, ArrayList<Employee> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public EmployeesAdapter.EmployeesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_employee, parent, false);
        return new EmployeesAdapter.EmployeesHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull EmployeesAdapter.EmployeesHolder holder, int position) {
        Employee employee = list.get(position);

        Picasso.get().load(employee.getFilePath()).fit().error(R.drawable.user).into(holder.ivPicture);
        holder.txtName.setText(employee.getName());
        holder.txtDesc.setText(employee.getDescription());

        if (employee.getPosId() == 0) {
            holder.txtPosition.setText("Position: " + employee.getCustomPosition());
        } else {
            holder.txtPosition.setText("Position: " + employee.getPosition());
        }

        if (employee.getTermId() == 0) {
            holder.txtTerm.setText("Term: " + employee.getCustomTerm());
        } else {
            holder.txtTerm.setText("Term: " + employee.getTerm());
        }

        holder.ivPicture.setOnClickListener(v -> {
            Intent intent= new Intent(context, ViewImageActivity.class);
            intent.putExtra("image_url", employee.getFilePath());
            context.startActivity(intent);
        });
    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Employee> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Employee employee : listAll){
                    if(employee.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || employee.getTerm().toLowerCase().contains(constraint.toString().toLowerCase())
                            || employee.getPosition().toLowerCase().contains(constraint.toString().toLowerCase())
                            || employee.getCustomTerm().toLowerCase().contains(constraint.toString().toLowerCase())
                            || employee.getCustomPosition().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(employee);
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
            list.addAll((Collection<? extends Employee>) results.values);
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

    public class EmployeesHolder extends RecyclerView.ViewHolder {
        private final ImageView ivPicture;
        private final TextView txtName, txtPosition, txtTerm, txtDesc;
        public EmployeesHolder(@NonNull View itemView) {
            super(itemView);

            ivPicture = itemView.findViewById(R.id.ivEmpPicture);
            txtName = itemView.findViewById(R.id.txtEmpName);
            txtPosition = itemView.findViewById(R.id.txtEmpPos);
            txtTerm = itemView.findViewById(R.id.txtEmpTerm);
            txtDesc = itemView.findViewById(R.id.txtEmpDesc);
        }
    }
}
