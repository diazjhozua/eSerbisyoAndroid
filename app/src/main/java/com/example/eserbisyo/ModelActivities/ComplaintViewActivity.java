package com.example.eserbisyo.ModelActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ComplainantsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.DefendantsAdapter;
import com.example.eserbisyo.Models.Complainant;
import com.example.eserbisyo.Models.Complaint;
import com.example.eserbisyo.Models.Defendant;
import com.example.eserbisyo.Models.MissingPerson;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ComplaintViewActivity extends AppCompatActivity {

    private TextView txtID, txtCreatedAt, txtType, txtReason, txtAction,
            txtComplainantCount, txtDefendantCount, txtStatus, txtAdminMessage, txtUpdatedAt;

    public static RecyclerView rvComplainant, rvDefendant;

    public static ArrayList<Complainant> complainantArrayList;
    public static ArrayList<Defendant> defendantArrayList;
    private ComplainantsAdapter complainantsAdapter;
    private DefendantsAdapter defendantsAdapter;

    private SharedPreferences userPref;
    private ProgressDialog progressDialog;
    public JSONObject errorObj = null;

    private Complaint mComplaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_view);

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            try {
                JSONObject complaintJSONObject= new JSONObject(extras.getString(Extra.JSON_OBJECT));

                 mComplaint = new Complaint(
                        complaintJSONObject.getInt("id"), complaintJSONObject.getInt("contact_id"), complaintJSONObject.getString("contact_name"),
                        new Type(complaintJSONObject.getInt("type_id"), complaintJSONObject.getString("complaint_type")), complaintJSONObject.getString("custom_type"),
                        complaintJSONObject.getString("reason"), complaintJSONObject.getString("action"), complaintJSONObject.getString("email"),
                        complaintJSONObject.getString("phone_no"), complaintJSONObject.getString("status"), complaintJSONObject.getString("admin_message"),
                        complaintJSONObject.getString("created_at"), complaintJSONObject.getString("updated_at")
                );

                complainantArrayList = new ArrayList<>();
                JSONArray complainantJSONArray = new JSONArray(complaintJSONObject.getString("complainants"));
                if(complainantJSONArray.length() > 0){
                    for (int i = 0; i < complainantJSONArray.length(); i++) {
                        JSONObject complainantJSONObject = complainantJSONArray.getJSONObject(i);

                        Complainant mComplainant = new Complainant(
                                complainantJSONObject.getInt("id"),
                                complainantJSONObject.getInt("complaint_id"),
                                false, false,
                                complainantJSONObject.getString("name"),
                                complainantJSONObject.getString("signature_picture"),
                                complainantJSONObject.getString("file_path")
                        );
                        complainantArrayList.add(mComplainant);
                    }
                }

                defendantArrayList = new ArrayList<>();
                JSONArray defendantJSONArray = new JSONArray(complaintJSONObject.getString("defendants"));
                if(defendantJSONArray.length() > 0){
                    for (int i = 0; i < defendantJSONArray.length(); i++) {
                        JSONObject defendantJSONObject = defendantJSONArray.getJSONObject(i);

                        Defendant mDefendant = new Defendant(
                                defendantJSONObject.getInt("id"),
                                defendantJSONObject.getInt("complaint_id"),
                                false, false,
                                defendantJSONObject.getString("name")
                        );
                        defendantArrayList.add(mDefendant);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        init();

    }

    private void init() {
        txtID = findViewById(R.id.txtID);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtType = findViewById(R.id.txtType);
        txtReason = findViewById(R.id.txtReason);
        txtAction = findViewById(R.id.txtAction);
        txtComplainantCount = findViewById(R.id.txtComplainantCount);
        txtDefendantCount = findViewById(R.id.txtDefendantCount);
        txtStatus = findViewById(R.id.txtStatus);
        txtAdminMessage = findViewById(R.id.txtAdminMessage);
        txtUpdatedAt = findViewById(R.id.txtUpdatedAt);

        userPref = ComplaintViewActivity.this.getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        rvComplainant = findViewById(R.id.recyclerViewComplainant);
        rvDefendant = findViewById(R.id.recyclerViewDefendant);

        rvDefendant.setHasFixedSize(true);
        rvDefendant.setLayoutManager(new LinearLayoutManager(ComplaintViewActivity.this));

        rvComplainant.setHasFixedSize(true);
        rvComplainant.setLayoutManager(new LinearLayoutManager(ComplaintViewActivity.this));

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        setData();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        complainantsAdapter = new ComplainantsAdapter(ComplaintViewActivity.this, complainantArrayList);
        rvComplainant.setAdapter(complainantsAdapter);

        defendantsAdapter = new DefendantsAdapter(ComplaintViewActivity.this, defendantArrayList);
        rvDefendant.setAdapter(defendantsAdapter);

        txtID.setText("Complaint # " +mComplaint.getId());
        txtCreatedAt.setText(mComplaint.getCreatedAt());

        if (mComplaint.getType().getId() == 0) {
            txtType.setText("Complaint type: " + mComplaint.getCustomType());
        } else {
            txtType.setText("Complaint type: " + mComplaint.getType().getName());
        }

        txtReason.setText("Reason: " + mComplaint.getReason());
        txtAction.setText("Action: " + mComplaint.getAction());

        switch (mComplaint.getStatus()) {
            case "Pending":
                txtStatus.setTextColor(getResources().getColor(R.color.warningColor));
                txtAdminMessage.setVisibility(View.GONE);
                txtUpdatedAt.setVisibility(View.GONE);
                break;
            case "Denied":
                txtStatus.setTextColor(getResources().getColor(R.color.firebrick));
                break;
            case "Approved":
                txtStatus.setTextColor(getResources().getColor(R.color.primaryColor));
                break;
            case "Resolved":
                txtStatus.setTextColor(getResources().getColor(R.color.teal_700));
                break;
        }

        txtStatus.setText(mComplaint.getStatus());
        txtAdminMessage.setText("Admin Message: " + mComplaint.getAdminMessage());
        txtUpdatedAt.setText("Responded At: " + mComplaint.getUpdatedAt());

        txtDefendantCount.setText("Defendant: " + defendantsAdapter.getItemCount() + " (Total)");
        txtComplainantCount.setText("Complainant: " + complainantsAdapter.getItemCount() + " (Total)");
    }

    public void cancelEdit(View view) {
        finish();
    }
}