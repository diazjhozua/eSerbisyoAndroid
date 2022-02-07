package com.example.eserbisyo.OrderActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.ModelActivities.ComplaintEditActivity;
import com.example.eserbisyo.ModelActivities.ComplaintViewActivity;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ComplainantsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.FormsAdapter;
import com.example.eserbisyo.Models.Complainant;
import com.example.eserbisyo.Models.Form;
import com.example.eserbisyo.Models.MissingPerson;
import com.example.eserbisyo.Models.Order;
import com.example.eserbisyo.Models.User;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class OrderViewActivity extends AppCompatActivity {
    private TextView txtOverallStatus, txtId, txtContactName, txtContactAddress, txtContactPhoneNo, txtContactEmail,
            txtContactOrderType, txtStatus, txtAdminMessage, txtUpdatedAt, txtTotalCertPrice, txtDeliveryFee, txtTotalFee,
            txtBikerName, txtBikerPhoneNo, txtBikerEmail, txtBikerBikeName, txtBikerBikeSize, txtBikerBikeColor;

    private LinearLayout layoutBiker;
    private CircleImageView circIvBiker;
    private RecyclerView recyclerView;
    private Button btnReport;

    private JSONObject jsonObject;
    private Order mOrder;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    public static FormsAdapter formsAdapter;
    public static ArrayList<Form> formArrayList;
    private JSONObject errorObj = null;

    //var for report dialog
    private Dialog reportDialog;
    private TextInputLayout txtLayoutMessage;
    private TextInputEditText inputTxtMessage;
    private Button btnCancel, btnSubmit;

    private boolean isReported = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_view);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            try {
                jsonObject= new JSONObject(extras.getString(Extra.JSON_OBJECT));

                User mBiker = null;

                if (!jsonObject.isNull("biker")) {
                    JSONObject bikerJSONObject = jsonObject.getJSONObject("biker");

                    mBiker = new User(
                            bikerJSONObject.getInt("id"), bikerJSONObject.getString("first_name") + bikerJSONObject.getString("middle_name") + bikerJSONObject.getString("last_name"),
                            bikerJSONObject.getString("phone_no"), bikerJSONObject.getString("email"),
                            bikerJSONObject.getString("picture_name"), bikerJSONObject.getString("file_path"), bikerJSONObject.getString("bike_type"),
                            bikerJSONObject.getString("bike_color"),    bikerJSONObject.getString("bike_size")
                    );
                }


                mOrder = new Order(
                        jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("email"), jsonObject.getString("phone_no"),
                        jsonObject.getString("location_address"), jsonObject.getString("created_at"), jsonObject.getString("pick_up_type"),
                        jsonObject.getString("order_status"), jsonObject.getString("pickup_date"), jsonObject.getString("received_at"), jsonObject.getDouble("total_price"),
                        jsonObject.getDouble("delivery_fee"), jsonObject.getString("application_status"), mBiker,
                        jsonObject.getString("admin_message"), jsonObject.getString("updated_at")
                );

                JSONArray formJSONArray = new JSONArray(jsonObject.getString("certificate_forms"));
                formArrayList = new ArrayList<>();
                if(formJSONArray.length() > 0){
                    for (int i = 0; i < formJSONArray.length(); i++) {
                        JSONObject formJSONObject = formJSONArray.getJSONObject(i);

                        Form mForm = new Form(
                                formJSONObject.getInt("id"),
                                formJSONObject.getInt("certificate_id"),
                                formJSONObject.getJSONObject("certificate").getString("name"),
                                formJSONObject.getDouble("price_filled"),
                                false
                        );
                        formArrayList.add(mForm);
                    }
                }

                JSONArray reportJSONArray = new JSONArray(jsonObject.getString("order_reports"));
                if(reportJSONArray.length() > 0){
                    for (int i = 0; i < reportJSONArray.length(); i++) {
                        JSONObject reportJSONObject = reportJSONArray.getJSONObject(i);
                        if (userPref.getInt(Pref.ID, 0) == reportJSONObject.getInt("user_id")) {
                            isReported = true;
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        init();
    }
    private void init() {
        txtOverallStatus = findViewById(R.id.txtOverallStatus);
        txtId = findViewById(R.id.txtId);
        txtContactName = findViewById(R.id.txtContactName);
        txtContactAddress = findViewById(R.id.txtContactAddress);
        txtContactPhoneNo = findViewById(R.id.txtContactPhoneNo);
        txtContactEmail = findViewById(R.id.txtContactEmail);
        txtContactOrderType = findViewById(R.id.txtContactOrderType);
        txtStatus = findViewById(R.id.txtStatus);
        txtAdminMessage = findViewById(R.id.txtAdminMessage);
        txtUpdatedAt = findViewById(R.id.txtUpdatedAt);
        txtTotalCertPrice = findViewById(R.id.txtTotalCertPrice);
        txtDeliveryFee = findViewById(R.id.txtDeliveryFee);
        txtTotalFee = findViewById(R.id.txtTotalFee);
        txtBikerName = findViewById(R.id.txtBikerName);
        txtBikerPhoneNo = findViewById(R.id.txtBikerPhoneNo);
        txtBikerEmail = findViewById(R.id.txtBikerEmail);
        txtBikerPhoneNo = findViewById(R.id.txtBikerPhoneNo);
        txtBikerEmail = findViewById(R.id.txtBikerEmail);
        txtBikerBikeName = findViewById(R.id.txtBikerBikeName);
        txtBikerBikeSize = findViewById(R.id.txtBikerBikeSize);
        txtBikerBikeColor = findViewById(R.id.txtBikerBikeColor);

        layoutBiker = findViewById(R.id.layoutBiker);
        circIvBiker = findViewById(R.id.circIvBiker);
        recyclerView = findViewById(R.id.recyclerView);
        btnReport = findViewById(R.id.btnReport);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(OrderViewActivity.this));



        setData();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {

        setOverallStatus();

        txtId.setText("Order #" + mOrder.getId());
        txtContactName.setText(mOrder.getName());
        txtContactAddress.setText(mOrder.getLocationAddress());
        txtContactPhoneNo.setText(mOrder.getPhoneNo());
        txtContactEmail.setText(mOrder.getEmail());
        txtContactOrderType.setText(mOrder.getOrderType());


        switch (mOrder.getApplicationStatus()) {
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
            case "Cancelled":
                txtStatus.setTextColor(getResources().getColor(R.color.teal_700));
                break;
        }

        txtStatus.setText(mOrder.getApplicationStatus());
        txtAdminMessage.setText("Admin Message: " + mOrder.getAdminMessage());
        txtUpdatedAt.setText("Responded At: " + mOrder.getUpdatedAt());

        txtTotalCertPrice.setText("Total Certificate Price: ₱ " + mOrder.getTotalPrice());

        if (mOrder.getOrderType().equals("Pickup") || mOrder.getmBiker() == null) {
            layoutBiker.setVisibility(View.GONE);
        } else {
            Picasso.get().load(Api.STORAGE + mOrder.getmBiker().getFilePath()).fit().error(R.drawable.user).into(circIvBiker);
            txtBikerName.setText(mOrder.getmBiker().getName());
            txtBikerBikeName.setText(mOrder.getmBiker().getName());
            txtBikerPhoneNo.setText(mOrder.getmBiker().getPhoneNo());
            txtBikerEmail.setText(mOrder.getmBiker().getEmail());
            txtBikerBikeName.setText("Bike: " + mOrder.getmBiker().getBikeType());
            txtBikerBikeSize.setText("Size: " + mOrder.getmBiker().getBikeType());
            txtBikerBikeColor.setText("Color: " + mOrder.getmBiker().getBikeColor());
        }

        formsAdapter = new FormsAdapter(OrderViewActivity.this, formArrayList);
        recyclerView.setAdapter(formsAdapter);

        txtTotalCertPrice.setText("Total Certificate Price: ₱ " + mOrder.getTotalPrice());
        txtDeliveryFee.setText("Delivery Fee: ₱ " + mOrder.getDeliveryFee());
        txtTotalFee.setText("Total Fee: ₱ " + (mOrder.getTotalPrice() + mOrder.getDeliveryFee()));

        if ((mOrder.getApplicationStatus().equals("Approved") || mOrder.getOrderStatus().equals("Received")) && mOrder.getOrderType().equals("Delivery")) {

            if (isReported) {
                btnReport.setEnabled(false);
                btnReport.setText("You already submitted a report");
                btnReport.setBackgroundColor(getResources().getColor(R.color.infoColor));
            }
            btnReport.setVisibility(View.VISIBLE);
        }

        setListeners();
    }

    private void setListeners() {
        btnReport.setOnClickListener(view -> openReportDialog());
    }

    private void openReportDialog() {
        reportDialog = new Dialog(this);
        reportDialog.setContentView(R.layout.dialog_order_report);
        reportDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reportDialog.setCancelable(true);

        txtLayoutMessage = reportDialog.findViewById(R.id.txtLayoutMessage);
        inputTxtMessage = reportDialog.findViewById(R.id.inputTxtMessage);
        btnCancel = reportDialog.findViewById(R.id.btnCancel);
        btnSubmit = reportDialog.findViewById(R.id.btnSubmit);

        btnCancel.setOnClickListener(v -> reportDialog.dismiss());

        // listener for text input message
        inputTxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()>=5 && Objects.requireNonNull(inputTxtMessage.getText()).toString().length()<=200){
                    txtLayoutMessage.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (validateReport()) {
                submitReport();
            }
        });

        reportDialog.show();
    }

    private boolean validateReport() {
        if (Objects.requireNonNull(inputTxtMessage.getText()).toString().isEmpty()) {
            txtLayoutMessage.setErrorEnabled(true);
            txtLayoutMessage.setError("Message name is required");
            return false;
        }

        if (inputTxtMessage.getText().length() < 5) {
            txtLayoutMessage.setErrorEnabled(true);
            txtLayoutMessage.setError("Required at least 5");
            return false;
        }

        if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()> 200){
            txtLayoutMessage.setErrorEnabled(true);
            txtLayoutMessage.setError("Required no more than  200 characters");
            return false;
        }
        return true;
    }


    private void submitReport() {
        progressDialog.setMessage("Submitting data please wait.....");
        progressDialog.show();

        String body = Objects.requireNonNull(inputTxtMessage.getText()).toString().trim();
        StringRequest request = new StringRequest(Request.Method.POST, Api.ORDER_SUBMIT_REPORT + mOrder.getId(), response->{
            try {
                JSONObject object = new JSONObject(response);

                btnReport.setEnabled(false);
                btnReport.setText("You already submitted a report");
                btnReport.setBackgroundColor(getResources().getColor(R.color.infoColor));

                Toasty.success(this, "Your report has been submitted successfully", Toast.LENGTH_LONG, true).show();

                progressDialog.hide();
                reportDialog.hide();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        },error ->{
            progressDialog.dismiss();
            if (errorObj.has("errors")) {
                try {
                    JSONObject errors = errorObj.getJSONObject("errors");
                    showErrorMessage(errors);
                } catch (JSONException ignored) {
                }
            } else if (errorObj.has("message")) {
                try {
                    Toasty.error(this, errorObj.getString("message"), Toast.LENGTH_LONG, true).show();
                } catch (JSONException ignored) {
                }
            } else {
                Toasty.error(this, "Request Timeout", Toast.LENGTH_LONG, true).show();
            }
        } ){

            //add token to headers
            @Override
            public Map<String, String> getHeaders() {
                String token = userPref.getString(Pref.TOKEN,"");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            //add params
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("body", body);
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

        RequestQueue queue = Volley.newRequestQueue(OrderViewActivity.this);
        queue.add(request);

    }

    public void showErrorMessage (Object message) {
        for(Iterator<String> iter = ((JSONObject) message).keys(); iter.hasNext();) {
            String key = iter.next();
            try {
                Object value = ((JSONObject) message).get(key);
                Toasty.error(this, value.toString().replaceAll("\\p{P}", ""), Toast.LENGTH_LONG, true).show();
            } catch (JSONException ignored) {}
        }
    }


    @SuppressLint("SetTextI18n")
    private void setOverallStatus() {
        if (mOrder.getApplicationStatus().equals("Denied")) {
            txtOverallStatus.setText("DENIED");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.firebrick));
        } else if (mOrder.getApplicationStatus().equals("Approved")) {
            if (mOrder.getOrderStatus().equals("DNR")) {
                txtOverallStatus.setText("You did not receive this certificate (DNR)");
                txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.firebrick));
            } else if (mOrder.getOrderStatus().equals("Received")) {
                txtOverallStatus.setText("You have receive this document (Received Date: " + mOrder.getReceivedAt() + ")");
                txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.teal_700));
            } else if (mOrder.getOrderType().equals("Pickup")) {
                txtOverallStatus.setText("APPROVED \n (RECEIVED THE ITEM AT THE BARANGAY AFTER OR ON THE SPECIFIED DATE:  " + mOrder.getPickupAt() + ")");
                txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.teal_700));
            } else if (mOrder.getOrderType().equals("Delivery")) {
                if(mOrder.getmBiker() == null || mOrder.getOrderStatus().equals("Waiting")) {
                    txtOverallStatus.setText("APPROVED \n (WAITING FOR ANY BIKER TO PICKUP YOUR ORDER). (YOU WILL RECEIVE THE ITEM AFTER OR ON THE SPECIFIED DATE: " + mOrder.getPickupAt() + ")" );
                    txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.teal_700));
                } else if (mOrder.getOrderStatus().equals("Accepted")){
                    txtOverallStatus.setText("ORDER ACCEPTED TO DELIVER BY THE BIKER (YOU WILL RECEIVE THE ITEM AFTER OR ON THE SPECIFIED DATE: " + mOrder.getPickupAt() + ")" );
                } else if (mOrder.getOrderStatus().equals("On-Going")) {
                    txtOverallStatus.setText("BIKER IS DELIVERING YOUR ORDER (YOU WILL RECEIVE THE ITEM AFTER OR ON THE SPECIFIED DATE: "+ mOrder.getPickupAt() + ")" );
                    txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.infoColor));
                }
            }
        }
    }




    public void cancelEdit(View view) {
        finish();
    }
}