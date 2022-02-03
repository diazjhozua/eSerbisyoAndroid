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
import androidx.cardview.widget.CardView;
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
import com.example.eserbisyo.Models.Report;
import com.example.eserbisyo.Models.Type;
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

public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ReportsHolder> {
    private final Context context;
    private final ArrayList<Report> list;
    private final ArrayList<Report> listAll;
    private final SharedPreferences sharedPreferences;

    /* LOADING */
    private ProgressDialog progressDialog;
    private Dialog dialog;

    /* FOR PROFILE */
    private Report reportProfile;

    private JSONObject errorObj = null;

    public ReportsAdapter(Context context, ArrayList<Report> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ReportsAdapter.ReportsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_report, parent, false);
        return new ReportsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReportsAdapter.ReportsHolder holder, int position) {
        Report report = list.get(position);
        holder.txtReportID.setText(context.getString(R.string.report_no) + report.getId());
        holder.txtSubmittedBy.setText(report.getSubmittedAs());
        if (report.getType().getId() == 0) {
            holder.txtType.setText("Report type: " + report.getCustomType());

        } else {
            holder.txtType.setText("Report type: " + report.getType().getName());
        }

        holder.txtUrgentClassification.setText(report.getUrgentClassification());
        holder.txtCreatedAt.setText(report.getCreatedAt());
        holder.txtStatus.setText(report.getStatus());
        holder.txtMessage.setText("Description: " + report.getDescription());
        if (report.getUrgentClassification().equals("Nonurgent")){
            holder.txtUrgentClassification.setTextColor(context.getResources().getColor(R.color.primaryColor));
        } else if (report.getUrgentClassification().equals("Urgent")) {
            holder.txtUrgentClassification.setTextColor(context.getResources().getColor(R.color.firebrick));
        }

        switch (report.getStatus()) {
            case "Pending":
                holder.txtStatus.setTextColor(context.getResources().getColor(R.color.primaryColor));
                break;
            case "Invalid":
            case "Ignored":
                holder.txtStatus.setTextColor(context.getResources().getColor(R.color.firebrick));
                break;
            case "Noted":
                holder.txtStatus.setTextColor(context.getResources().getColor(R.color.teal_700));
                break;
        }

        if (report.getStatus().equals("Invalid") || report.getStatus().equals("Ignored")) {
            holder.txtRespondedAt.setText("Responded at: " + report.getRespondedAt());
            holder.txtRespondedAt.setVisibility(View.VISIBLE);
        }

        if (report.getStatus().equals("Pending") || report.getStatus().equals("Ignored")) {
            holder.txtAdminMessage.setVisibility(View.GONE);
            holder.txtRespondedAt.setVisibility(View.GONE);
        } else {
            holder.txtAdminMessage.setText("Admin Message: " + report.getAdminMessage());
            holder.txtRespondedAt.setText("Responded at: " + report.getRespondedAt());
            holder.txtAdminMessage.setVisibility(View.VISIBLE);
            holder.txtRespondedAt.setVisibility(View.VISIBLE);
        }



        holder.cardReport.setOnClickListener(v -> getReportProfile(report.getId()));

    }

    /* GET THE ADDITIONAL INFO OF A REPORT */
    private void getReportProfile(int id) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the report data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.REPORTS + id, response->{

            try {
                JSONObject object = new JSONObject(response);
                JSONObject reportObj = object.getJSONObject("data");

                reportProfile = new Report(
                        reportObj.getInt("id"), reportObj.getString("submitted_by"), new Type(reportObj.getInt("type_id"), reportObj.getString("report_type")),
                        reportObj.getString("custom_type"), reportObj.getString("location_address"), reportObj.getString("landmark") , reportObj.getString("description"),
                        reportObj.getString("urgency_classification"), reportObj.getString("picture_name"), reportObj.getString("file_path"), reportObj.getString("admin_message"),
                        reportObj.getString("status"), reportObj.getString("created_at"), reportObj.getString("updated_at")
                );

                openProfileDialog();


            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        },error ->{
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
        } ){

            //add token to headers
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

    @SuppressLint("SetTextI18n")
    private void openProfileDialog() {
        TextView txtReportId, txtSubmittedBy, txtType, txtUrgentClass,
                txtLandmark, txtLocation, txtDescription, txtSubmittedAt,
                txtStatus, txtAdminMessage, txtAdminMessageLabel,  txtRptDlgRespondedAt, txtRptDlgRespondedAtLabel;
        ImageView ivPicture;
        Button btnBack;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_report_profile);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        btnBack = dialog.findViewById(R.id.btnRptDlgBack);
        ivPicture = dialog.findViewById(R.id.ivRptDlgPicture);

        txtReportId = dialog.findViewById(R.id.txtRptDlgID);
        txtSubmittedBy = dialog.findViewById(R.id.txtRptDlgSubmittedBy);
        txtType = dialog.findViewById(R.id.txtRptDlgType);
        txtUrgentClass = dialog.findViewById(R.id.txtRptDlgUrgentClass);
        txtLandmark = dialog.findViewById(R.id.txtRptDlgLandmark);
        txtLocation = dialog.findViewById(R.id.txtRptDlgLocation);
        txtDescription = dialog.findViewById(R.id.txtRptDlgDescription);
        txtSubmittedAt = dialog.findViewById(R.id.txtRptDlgSubmittedAt);
        txtStatus = dialog.findViewById(R.id.txtRptDlgStatus);
        txtAdminMessage = dialog.findViewById(R.id.txtRptDlgAdminMessage);
        txtAdminMessageLabel = dialog.findViewById(R.id.txtRptDlgAdminMessageLabel);
        txtRptDlgRespondedAt = dialog.findViewById(R.id.txtRptDlgRespondedAt);
        txtRptDlgRespondedAtLabel = dialog.findViewById(R.id.txtRptDlgRespondedAtLabel);

        Picasso.get().load(Api.STORAGE + reportProfile.getFilePath()).fit().error(R.drawable.no_picture).into(ivPicture);

        txtReportId.setText(context.getString(R.string.report_no) + reportProfile.getId());
        txtSubmittedBy.setText(reportProfile.getSubmittedAs());

        ivPicture.setOnClickListener(v -> {
            Intent intent= new Intent(context, ViewImageActivity.class);
            intent.putExtra("image_url", Api.STORAGE + reportProfile.getFilePath());
            context.startActivity(intent);
        });

        if (reportProfile.getType().getId() == 0) {
            txtType.setText(reportProfile.getCustomType());
        } else {
            txtType.setText(reportProfile.getType().getName());
        }
        txtUrgentClass.setText(reportProfile.getUrgentClassification());

        if (reportProfile.getUrgentClassification().equals("Nonurgent")){
            txtUrgentClass.setTextColor(context.getResources().getColor(R.color.teal_200));
        } else if (reportProfile.getUrgentClassification().equals("Urgent")) {
            txtUrgentClass.setTextColor(context.getResources().getColor(R.color.firebrick));
        }

        txtLandmark.setText(reportProfile.getLandmark());
        txtLocation.setText(reportProfile.getLocationAddress());
        txtDescription.setText(reportProfile.getDescription());
        txtSubmittedAt.setText(reportProfile.getCreatedAt());
        txtStatus.setText(reportProfile.getStatus());

        switch (reportProfile.getStatus()) {
            case "Pending":
                txtStatus.setTextColor(context.getResources().getColor(R.color.warningColor));
                break;
            case "Invalid":
            case "Ignored":
                txtStatus.setTextColor(context.getResources().getColor(R.color.firebrick));
                break;
            case "Noted":
                txtStatus.setTextColor(context.getResources().getColor(R.color.teal_700));
                break;
        }

        switch (reportProfile.getStatus()) {
            case "Pending":
            case "Ignored":
                txtAdminMessage.setVisibility(View.GONE);
                txtAdminMessageLabel.setVisibility(View.GONE);
                txtRptDlgRespondedAt.setVisibility(View.GONE);
                txtRptDlgRespondedAtLabel.setVisibility(View.GONE);
                break;
            case "Invalid":
            case "Noted":
                txtAdminMessage.setText("Admin Message:" + reportProfile.getAdminMessage());
                txtRptDlgRespondedAt.setText(":" +reportProfile.getRespondedAt());
                break;
        }

        btnBack.setOnClickListener(v -> dialog.dismiss());

        dialog.show();


    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Report> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Report report : listAll){
                    if(report.getType().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || report.getStatus().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(report.getId()).contains(constraint.toString().toLowerCase())
                            || report.getAdminMessage().toLowerCase().contains(constraint.toString().toLowerCase())
                            || report.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(report);
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
            list.addAll((Collection<? extends Report>) results.values);
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

    public static class ReportsHolder extends RecyclerView.ViewHolder {
        private final CardView cardReport;
        private final TextView txtReportID, txtSubmittedBy, txtType, txtUrgentClassification, txtMessage, txtStatus, txtCreatedAt, txtRespondedAt, txtAdminMessage;
        public ReportsHolder(@NonNull View itemView) {
            super(itemView);
            cardReport = itemView.findViewById(R.id.cardReport);
            txtReportID = itemView.findViewById(R.id.txtID);
            txtSubmittedBy = itemView.findViewById(R.id.txtSubmittedAs);
            txtType = itemView.findViewById(R.id.txtType);
            txtUrgentClassification = itemView.findViewById(R.id.txtUrgentClassification);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtRespondedAt = itemView.findViewById(R.id.txtUpdatedAt);
            txtAdminMessage = itemView.findViewById(R.id.txtAdminMessage);
        }
    }
}
