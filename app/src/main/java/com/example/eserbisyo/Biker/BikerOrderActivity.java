package com.example.eserbisyo.Biker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.eserbisyo.ModelRecyclerViewAdapters.FormsAdapter;
import com.example.eserbisyo.Models.Form;
import com.example.eserbisyo.Models.Order;
import com.example.eserbisyo.Models.User;
import com.example.eserbisyo.OrderActivity.OrderViewActivity;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class BikerOrderActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    private TextView txtOverallStatus, txtId, txtContactName, txtContactAddress, txtContactPhoneNo, txtContactEmail,
            txtContactOrderType, txtTotalCertPrice, txtDeliveryFee, txtTotalFee;

    private RecyclerView recyclerView;
    private Button btnStartRiding, btnMarkedAsReceive, btnMarkedAsDNR, btnReport;

    private JSONObject jsonObject;
    private Order mOrder;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    public static FormsAdapter formsAdapter;
    public static ArrayList<Form> formArrayList;
    private JSONObject errorObj = null;

    private boolean isReported = false;

    private Double longitude, latitude;

    /*Confirmation Dialog*/
    private Dialog confirmDialog;
    private TextView confirmDialogTxtTitle, confirmDialogTxtDesc;
    private Button confirmDialogBtnCancel, confirmDialogBtnConfirm;

    /* Receive Dialog */
    private Dialog receiveDialog;
    private TextView receiveDialogTxtExamplePicture;
    private ImageView receiveDialogIvCredential;

    private boolean isStarting = false;
    private boolean isMarkingDNR = false;

    private Dialog reportDialog;
    private TextInputLayout txtLayoutMessage;
    private TextInputEditText inputTxtMessage;
    private Button btnCancel, btnSubmit;

    private Bitmap bitmap = null;

    // Getting of images from the gallery
    ActivityResultLauncher<Intent> getImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        assert data != null;
                        Uri imgUri = data.getData();
                        receiveDialogIvCredential.setImageURI(imgUri);
                        receiveDialogTxtExamplePicture.setVisibility(View.GONE);
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

    // Getting of images from the gallery
    ActivityResultLauncher<Intent> getCaptureResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;

                        bitmap = (Bitmap) data.getExtras().get("data");
                        //                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                        //set the image into imageview
                        receiveDialogIvCredential.setImageBitmap(bitmap);
                        receiveDialogTxtExamplePicture.setVisibility(View.GONE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biker_order);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            try {
                jsonObject= new JSONObject(extras.getString(Extra.JSON_OBJECT));

                mOrder = new Order(
                        jsonObject.getInt("id"), jsonObject.getString("name"), jsonObject.getString("email"), jsonObject.getString("phone_no"),
                        jsonObject.getString("location_address"), jsonObject.getString("pick_up_type"), jsonObject.getString("order_status"),
                        jsonObject.getString("pickup_date"), jsonObject.getString("received_at"), jsonObject.getDouble("total_price"),
                        jsonObject.getDouble("delivery_fee"), jsonObject.getString("delivery_payment_status"), true,
                        jsonObject.getString("is_returned")
                );

                longitude = jsonObject.getDouble("user_long");
                latitude = jsonObject.getDouble("user_lat");

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
        txtTotalCertPrice = findViewById(R.id.txtTotalCertPrice);
        txtDeliveryFee = findViewById(R.id.txtDeliveryFee);
        txtTotalFee = findViewById(R.id.txtTotalFee);

        btnStartRiding = findViewById(R.id.btnStartRiding);
        btnMarkedAsReceive = findViewById(R.id.btnMarkedAsReceive);
        btnMarkedAsDNR = findViewById(R.id.btnMarkedAsDNR);
        btnReport = findViewById(R.id.btnReport);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(BikerOrderActivity.this));

        confirmDialog = new Dialog(BikerOrderActivity.this);
        confirmDialog.setContentView(R.layout.dialog_confirmation);
        confirmDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmDialog.setCancelable(true);

        confirmDialogTxtTitle = confirmDialog.findViewById(R.id.txtDialogConfirmationTitle);
        confirmDialogTxtDesc = confirmDialog.findViewById(R.id.txtDialogConfirmationDesc);

        confirmDialogBtnConfirm = confirmDialog.findViewById(R.id.btnDelete);
        confirmDialogBtnCancel = confirmDialog.findViewById(R.id.btnCancel);

        /*Receive Dialog */
        receiveDialog = new Dialog(BikerOrderActivity.this);
        receiveDialog.setContentView(R.layout.dialog_confirm_receive);
        receiveDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        receiveDialog.setCancelable(true);

        receiveDialogIvCredential = receiveDialog.findViewById(R.id.imgCredential);
        receiveDialogTxtExamplePicture = receiveDialog.findViewById(R.id.txtExamplePicture);

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

        txtTotalCertPrice.setText("Total Certificate Price: ₱ " + mOrder.getTotalPrice());

        formsAdapter = new FormsAdapter(BikerOrderActivity.this, formArrayList);
        recyclerView.setAdapter(formsAdapter);

        txtTotalCertPrice.setText("Total Certificate Price: ₱ " + mOrder.getTotalPrice());
        txtDeliveryFee.setText("Delivery Fee: ₱ " + mOrder.getDeliveryFee());
        txtTotalFee.setText("Total Fee: ₱ " + (mOrder.getTotalPrice() + mOrder.getDeliveryFee()));

        if (mOrder.getOrderStatus().equals("Received") || mOrder.getOrderStatus().equals("DNR")) {
            if (isReported) {
                btnReport.setEnabled(false);
                btnReport.setText("You already submitted a report");
            }
            btnReport.setVisibility(View.VISIBLE);
            btnStartRiding.setVisibility(View.GONE);
            btnMarkedAsReceive.setVisibility(View.GONE);
            btnMarkedAsDNR.setVisibility(View.GONE);
        } else if (mOrder.getOrderStatus().equals("On-Going")) {
            btnStartRiding.setVisibility(View.VISIBLE);
        }

        setListeners();
    }

    @SuppressLint("SetTextI18n")
    private void setOverallStatus() {
        if (mOrder.getOrderStatus().equals("Received") && mOrder.getPaymentStatus().equals("Pending")) {
            txtOverallStatus.setText("DELIVERED BUT NOT YET PROCESSED. PLEASE GO TO THE BARANGAY TO PROCESSED THE PAYMENT");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.infoColor));
        } else if (mOrder.getOrderStatus().equals("On-Going")) {
            txtOverallStatus.setText("THE ADMINISTRATOR MARKED THIS ORDER AS ON-GOING. DELIVER THIS ORDER IMMEDIATELY");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.infoColor));
        } else if (mOrder.getOrderStatus().equals("Received") && mOrder.getPaymentStatus().equals("Received")) {
            txtOverallStatus.setText("ORDER TRANSACTION COMPLETE");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.teal_700));
        } else if (mOrder.getOrderStatus().equals("DNR")) {

            if (mOrder.getReturnedStatus().equals("No")) {
                txtOverallStatus.setText("THE RESIDENT DID NOT RECEIVE THE DOCUMENT (RETURN THE ORDER TO THE BARANGAY)");
            } else {
                txtOverallStatus.setText("THE RESIDENT DID NOT RECEIVE THE DOCUMENT (THE ORDER IS RETURNED TO THE BARANGAY OFFICE)");
            }
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.firebrick));
        }
    }

    private void setListeners() {
        String fullName = userPref.getString(Pref.FIRST_NAME, "") + " " + userPref.getString(Pref.LAST_NAME, "");
        txtContactPhoneNo.setOnClickListener(view -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", mOrder.getPhoneNo(), "Hello my name " + fullName + " and I am assigned to deliver your Order #" + mOrder.getId())));
        });

        txtContactEmail.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL , new String[]{mOrder.getEmail()});
            i.putExtra(Intent.EXTRA_SUBJECT, "Order #" + mOrder.getId() + " Biker Message");
            i.putExtra(Intent.EXTRA_TEXT , "Hello my name " + fullName + " and I am assigned to deliver your Order #" + mOrder.getId());
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toasty.error(BikerOrderActivity.this, "No email client configured. Please check.", Toasty.LENGTH_LONG, true).show();
            }
        });

        btnStartRiding.setOnClickListener(view -> {
            isStarting = true;
            openConfirmationDialog("Start Riding Confirmation", "Do you really want to start the delivering your order? Once it is submitted, " +
                    "it will notify the user who orders that you are on the way to the location ", "Start");

        });

        btnMarkedAsReceive.setOnClickListener(view -> {
            openReceiveDialog();
        });

        btnMarkedAsDNR.setOnClickListener(view -> {
            isMarkingDNR = true;
            openConfirmationDialog("Start Riding Confirmation", "Do you really want to marked this delivery as DNR (DID NOT RECEIVE BY THE RESIDENT)? Once it is submitted, " +
                    "it will notify the resident informing that they did not received the specific order", "Confirm");
        });

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

    @SuppressLint("SetTextI18n")
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

        RequestQueue queue = Volley.newRequestQueue(BikerOrderActivity.this);
        queue.add(request);

    }

    private void openReceiveDialog() {

        confirmDialogTxtTitle = confirmDialog.findViewById(R.id.txtDialogConfirmationTitle);
        confirmDialogTxtDesc = confirmDialog.findViewById(R.id.txtDialogConfirmationDesc);

        TextView receiveDialogTxtSelectPicture = receiveDialog.findViewById(R.id.txtSelectPhoto);
        TextView receiveDialogTxtCapturePhoto = receiveDialog.findViewById(R.id.txtCapturePhoto);
        Button receiveDialogBtnCancel = receiveDialog.findViewById(R.id.btnCancel);
        Button receiveDialogBtnConfirm = receiveDialog.findViewById(R.id.btnConfirm);

        //pick photo from gallery
        receiveDialogTxtSelectPicture.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            getImageResultLauncher.launch(i);
        });

        receiveDialogTxtCapturePhoto.setOnClickListener(v->{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            }else {
                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);

                getCaptureResultLauncher.launch(camera_intent);
            }
        });

        receiveDialogBtnCancel.setOnClickListener(view -> confirmDialog.dismiss());

        receiveDialogBtnConfirm.setOnClickListener(view -> {
            if (bitmap != null) {
                markedAsReceive();
            } else {
                Toasty.error(this, "Please attached image correspond to the requirement", Toast.LENGTH_LONG, true).show();
            }
        });


        receiveDialog.show();

    }

    private void openConfirmationDialog(String title, String desc, String action) {

        confirmDialogTxtTitle.setText(title);
        confirmDialogTxtDesc.setText(desc);
        confirmDialogBtnConfirm.setText(action);

        confirmDialogBtnCancel.setOnClickListener(view -> confirmDialog.dismiss());

        confirmDialogBtnConfirm.setOnClickListener(view -> {
            if (isStarting) {
                isStarting = false;
                notifyUserOrder();
            } else if (isMarkingDNR) {
                isMarkingDNR = false;
                markedAsDNR();
            }
        });
        confirmDialog.show();

    }

    @SuppressLint("SetTextI18n")
    private void markedAsDNR() {
        progressDialog.setMessage("Marking as receive please wait.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.BIKER_DNR_ORDER + mOrder.getId(), response -> {
            progressDialog.dismiss();
            confirmDialog.dismiss();

            btnStartRiding.setVisibility(View.GONE);
            btnMarkedAsReceive.setVisibility(View.GONE);
            btnMarkedAsDNR.setVisibility(View.GONE);
            btnReport.setVisibility(View.VISIBLE);

            txtOverallStatus.setText("THE RESIDENT DID NOT RECEIVE THE DOCUMENT (RETURN THE ORDER TO THE BARANGAY)");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.firebrick));

            Toasty.success(this, "The order is marked as DNR. The resident also notified about the changes in their order", Toast.LENGTH_LONG, true).show();

        },error -> {

            error.printStackTrace();
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
                Toasty.error(this, "Request Timeout", Toast.LENGTH_SHORT, true).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                String token = userPref.getString(Pref.TOKEN,"");
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

        RequestQueue queue = Volley.newRequestQueue(BikerOrderActivity.this);
        queue.add(request);

    }

    @SuppressLint("SetTextI18n")
    private void markedAsReceive() {
        progressDialog.setMessage("Marking as receive please wait.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.BIKER_RECEIVE_ORDER + mOrder.getId(), response -> {
            progressDialog.dismiss();
            receiveDialog.dismiss();

            btnStartRiding.setVisibility(View.GONE);
            btnMarkedAsReceive.setVisibility(View.GONE);
            btnMarkedAsDNR.setVisibility(View.GONE);
            btnReport.setVisibility(View.VISIBLE);

            txtOverallStatus.setText("DELIVERED BUT NOT YET PROCESSED. PLEASE GO TO THE BARANGAY TO PROCESSED THE PAYMENT");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.infoColor));

            Toasty.success(this, "The order is marked as received. The resident also notified about the changes in their order", Toast.LENGTH_LONG, true).show();

        },error -> {

            error.printStackTrace();
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
                Toasty.error(this, "Request Timeout", Toast.LENGTH_SHORT, true).show();
            }
        }){
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

                map.put("picture",bitmapToString(bitmap));
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

        RequestQueue queue = Volley.newRequestQueue(BikerOrderActivity.this);
        queue.add(request);
    }

    /*Notify the user*/
    private void notifyUserOrder() {
        progressDialog.setMessage("Booking the order please wait.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.BIKER_START_RIDING + mOrder.getId(), response -> {
            progressDialog.dismiss();
            confirmDialog.dismiss();
            Toasty.success(this, "User notified about your progress in delivery", Toast.LENGTH_LONG, true).show();

            btnStartRiding.setVisibility(View.GONE);
            btnMarkedAsReceive.setVisibility(View.VISIBLE);
            btnMarkedAsDNR.setVisibility(View.VISIBLE);

            Uri gmmIntentUri = Uri.parse("google.navigation:q="+ latitude +","+ longitude);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            startActivity(mapIntent);

        },error -> {
            error.printStackTrace();
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
                Toasty.error(this, "Request Timeout", Toast.LENGTH_SHORT, true).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() {
                String token = userPref.getString(Pref.TOKEN,"");
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

        RequestQueue queue = Volley.newRequestQueue(BikerOrderActivity.this);
        queue.add(request);
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }

        return "";
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

    public void cancelEdit(View view) {
        finish();
    }
}