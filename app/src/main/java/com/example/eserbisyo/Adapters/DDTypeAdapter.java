package com.example.eserbisyo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;

import java.util.ArrayList;

public class DDTypeAdapter extends ArrayAdapter<Type> {

    public DDTypeAdapter(@NonNull Context context, ArrayList<Type> typesList) {
        super(context, 0, typesList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }

        Type type = getItem(position);
        ImageView spinnerIV = convertView.findViewById(R.id.ivSpinnerLayout);
        TextView spinnerTV = convertView.findViewById(R.id.txtSpinnerLayout);
        if (type != null) {
            spinnerIV.setImageResource(R.drawable.type);
            spinnerTV.setText(type.getName());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dropdown_layout, parent, false);
        }
        Type type = getItem(position);
        TextView dropDownTV = convertView.findViewById(R.id.txtDropDownLayout);
        if (type != null) {
            dropDownTV.setText(type.getName());
        }
        return convertView;
    }

}
