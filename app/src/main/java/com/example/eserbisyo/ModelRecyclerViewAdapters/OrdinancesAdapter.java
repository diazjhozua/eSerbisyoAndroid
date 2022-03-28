package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.ModelActivities.Profile.AnnouncementActivity;
import com.example.eserbisyo.ModelActivities.Profile.DocumentActivity;
import com.example.eserbisyo.ModelActivities.Profile.OrdinanceActivity;
import com.example.eserbisyo.Models.Document;
import com.example.eserbisyo.Models.Ordinance;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewPDFActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class OrdinancesAdapter extends RecyclerView.Adapter<OrdinancesAdapter.OrdinancesHolder>{

    private final Context context;
    private final ArrayList<Ordinance> list;
    private final ArrayList<Ordinance> listAll;
    private final SharedPreferences sharedPreferences;
    private int id;

    private JSONObject errorObj = null;
    private ProgressDialog progressDialog;

    public OrdinancesAdapter(Context context, ArrayList<Ordinance> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public OrdinancesAdapter.OrdinancesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_ordinance, parent, false);
        return new OrdinancesAdapter.OrdinancesHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull OrdinancesAdapter.OrdinancesHolder holder, int position) {

        Ordinance ordinance = list.get(position);
        if (ordinance.getType().getId() == 0) {
            holder.txtType.setText("Type: " + ordinance.getCustomType());
        } else {
            holder.txtType.setText("Type: " + ordinance.getType().getName());
        }

        holder.txtTitle.setText('"' + ordinance.getTitle() + '"');
        holder.txtNo.setText("Ordinance No: " + ordinance.getOrdinanceNo());
        holder.txtDateApp.setText("Date Approved: " + ordinance.getDateApproved());
        holder.txtCreatedAt.setText(ordinance.getCreatedAt());

        holder.txtNo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent= new Intent(context, ViewPDFActivity.class);
//                intent.putExtra("pdf_path", ordinance.getFilePath());
//                context.startActivity(intent);

                Intent intent = new Intent(context, OrdinanceActivity.class);
                intent.putExtra(Extra.MODEL_ID, ordinance.getId());
                context.startActivity(intent);
            }
        });
    }


    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Ordinance> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Ordinance ordinance : listAll){
                    if(ordinance.getType().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || ordinance.getCustomType().toLowerCase().contains(constraint.toString().toLowerCase())
                            || ordinance.getDateApproved().toLowerCase().contains(constraint.toString().toLowerCase())
                            || ordinance.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(ordinance.getId()).contains(constraint.toString().toLowerCase())){
                        filteredList.add(ordinance);
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
            list.addAll((Collection<? extends Ordinance>) results.values);
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

    public class OrdinancesHolder extends RecyclerView.ViewHolder {
        private final TextView txtNo, txtTitle, txtDateApp, txtType, txtCreatedAt;
        public OrdinancesHolder(@NonNull View itemView) {
            super(itemView);

            txtNo = itemView.findViewById(R.id.txtOrdinanceNo);
            txtTitle = itemView.findViewById(R.id.txtOrdinanceTitle);
            txtDateApp = itemView.findViewById(R.id.txtOrdinanceDateApproved);
            txtType = itemView.findViewById(R.id.txtOrdinanceType);
            txtCreatedAt = itemView.findViewById(R.id.txtOrdinanceCreatedAt);
        }
    }
}
