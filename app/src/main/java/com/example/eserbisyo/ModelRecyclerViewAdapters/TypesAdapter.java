package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Models.Document;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;

import java.util.ArrayList;

public class TypesAdapter extends RecyclerView.Adapter<TypesAdapter.TypesHolder> {

    private final Context context;
    private final ArrayList<Type> list;
    private final ArrayList<Type> listAll;

    public TypesAdapter(Context context, ArrayList<Type> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }
    @NonNull
    @Override
    public TypesAdapter.TypesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_type, parent, false);
        return new TypesAdapter.TypesHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TypesAdapter.TypesHolder holder, int position) {
        Type mType = list.get(position);

        holder.txtName.setText(mType.getName());
        holder.txtCount.setText(mType.getCount());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class TypesHolder extends RecyclerView.ViewHolder {
        private final TextView txtName, txtCount;
        public TypesHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtCount = itemView.findViewById(R.id.txtCount);
        }
    }
}
