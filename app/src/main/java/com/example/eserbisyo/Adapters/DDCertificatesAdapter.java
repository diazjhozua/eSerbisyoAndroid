package com.example.eserbisyo.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.eserbisyo.Models.Certificate;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;

import java.util.ArrayList;

public class DDCertificatesAdapter extends ArrayAdapter<Certificate> {

    public DDCertificatesAdapter(@NonNull Context context, ArrayList<Certificate> certificateArrayList) {
        super(context, 0, certificateArrayList);
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_layout, parent, false);
        }

        Certificate mCertificate = getItem(position);
        ImageView spinnerIV = convertView.findViewById(R.id.ivSpinnerLayout);
        TextView spinnerTV = convertView.findViewById(R.id.txtSpinnerLayout);
        if (mCertificate != null) {
            spinnerIV.setImageResource(R.drawable.type);
            spinnerTV.setText(mCertificate.getName() + "\n ( ₱ " + mCertificate.getPrice()+" )");
        }
        return convertView;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_dropdown_layout, parent, false);
        }
        Certificate mCertificate = getItem(position);
        TextView dropDownTV = convertView.findViewById(R.id.txtDropDownLayout);
        if (mCertificate != null) {
            dropDownTV.setText(mCertificate.getName() + "\n ( ₱ " + mCertificate.getPrice()+" )");
        }
        return convertView;
    }
}
