package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.Models.UserRequirement;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class RequirementsAdapter extends RecyclerView.Adapter<RequirementsAdapter.RequirementsHolder>{
    private final Context context;
    private final ArrayList<UserRequirement> list;
    private final ArrayList<UserRequirement> listAll;
    private final SharedPreferences sharedPreferences;

    /* ID FOR REFERENCE IN UPDATE|DELETE CERTIFICATE */
    private int id;
    private int selectedPosition;

    private ProgressDialog progressDialog;
    private Dialog dialog;

    private JSONObject errorObj = null;

    public RequirementsAdapter(Context context, ArrayList<UserRequirement> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public RequirementsAdapter.RequirementsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_requirement, parent, false);
        return new RequirementsHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RequirementsAdapter.RequirementsHolder holder, @SuppressLint("RecyclerView") int position) {
        UserRequirement userRequirement = list.get(position);
        holder.txtRequirement.setText(userRequirement.getRequirement().getName());
        holder.txtSubmittedAt.setText(userRequirement.getCreatedAt());

        Picasso.get().load(Api.STORAGE + userRequirement.getFilePath()).fit().error(R.drawable.no_picture).into(holder.ivRequirement);

        holder.layoutRequirement.setOnClickListener(v -> {
            Intent intent= new Intent(context, ViewImageActivity.class);
            intent.putExtra("image_url", Api.STORAGE + userRequirement.getFilePath());
            context.startActivity(intent);
        });

        holder.ivDelete.setOnClickListener(v -> {
            selectedPosition = position;
            id = userRequirement.getId();
            openDeleteDialog();
        });


    }

    @SuppressLint("SetTextI18n")
    private void openDeleteDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        /* FOR DELETE CONFIRMATION */
        TextView dialogTitle = dialog.findViewById(R.id.txtDialogConfirmationTitle);
        Button delete = dialog.findViewById(R.id.btnDelete);
        Button cancel = dialog.findViewById(R.id.btnCancel);

        dialogTitle.setText("DELETE REQUIREMENT");


        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> deleteData());

        dialog.show();

    }

    private void deleteData() {
        progressDialog.setMessage("Deleting requirement.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.DELETE, Api.USER_REQUIREMENTS + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                String message = object.getString("message");

                list.remove(selectedPosition);
                notifyItemRemoved(selectedPosition);
                notifyDataSetChanged();
                listAll.clear();
                listAll.addAll(list);

                dialog.dismiss();

                Toasty.success(context, message, Toast.LENGTH_SHORT, true).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        },error -> {
            progressDialog.dismiss();
            if (errorObj.has("errors")) {
                try {
                    JSONObject errors = errorObj.getJSONObject("errors");
                    ((HomeActivity)context).showErrorMessage(errors);
                } catch (JSONException ignored) {
                }
            } else if (errorObj.has("message")) {
                try {
                    Toasty.error(context, errorObj.getString("message"), Toast.LENGTH_LONG, true).show();
                } catch (JSONException ignored) {
                }
            } else {
                Toasty.error(context, "Request Timeout", Toast.LENGTH_SHORT, true).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString(Pref.TOKEN,"");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                String json;

                if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
                    try {
                        json = new String(volleyError.networkResponse.data,
                                HttpHeaderParser.parseCharset(volleyError.networkResponse.headers));

                        errorObj = new JSONObject(json);
                    } catch (UnsupportedEncodingException | JSONException e) {
                        return new VolleyError(e.getMessage());
                    }

                    return new VolleyError(json);
                }
                return volleyError;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<UserRequirement> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (UserRequirement userRequirement  : listAll){
                    if(userRequirement.getRequirement().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(userRequirement.getId()).contains(constraint.toString().toLowerCase())
                            || String.valueOf(userRequirement.getRequirement().getId()).contains(constraint.toString().toLowerCase())){
                        filteredList.add(userRequirement);
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
            list.addAll((Collection<? extends UserRequirement>) results.values);
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

    public static class RequirementsHolder extends RecyclerView.ViewHolder {
        private final TextView txtRequirement, txtSubmittedAt;
        private final ImageView ivRequirement, ivDelete;
        private final ConstraintLayout layoutRequirement;
        public RequirementsHolder(@NonNull View itemView) {
            super(itemView);
            txtRequirement = itemView.findViewById(R.id.txtRequirement);
            txtSubmittedAt = itemView.findViewById(R.id.txtSubmittedAt);
            ivRequirement = itemView.findViewById(R.id.ivRequirement);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            layoutRequirement = itemView.findViewById(R.id.layoutRequirement);
        }
    }
}
