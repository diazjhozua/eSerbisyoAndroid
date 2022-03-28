package com.example.eserbisyo.Biker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.AccountActivities.UserVerificationActivity;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class BikerViewRegistrationActivity extends AppCompatActivity {
    private TextView txtOverallStatus, txtBikeType, txtBikeSize, txtBikeColor, txtReason, txtStatus, txtAdminMessage, txtRespondedAt, txtPhoneNo;
    private ImageView ivCredentialPicture;
    private Button btnResubmit;

    private String status, type, size, color, reason, imgPath, adminMessage, respondedAt, phoneNo;

    private JSONObject userVerification;
    private JSONObject errorObj = null;
    private ProgressDialog loadingDialog;
    private SharedPreferences userPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biker_view_registration);

//        Bundle extras  = getIntent().getExtras();
//        if (extras != null) {
//            try {
//                JSONObject jsonObject = new JSONObject(extras.getString(Extra.JSON_OBJECT));
//
//                status = jsonObject.getString("status");
//                phoneNo = jsonObject.getString("phone_no");
//                type = jsonObject.getString("bike_type");
//                size = jsonObject.getString("bike_size");
//                color = jsonObject.getString("bike_color");
//                reason = jsonObject.getString("reason");
//                imgPath = jsonObject.getString("credential_file_path");
//                adminMessage = jsonObject.getString("admin_message");
//                respondedAt = jsonObject.getString("updated_at");
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
        init();
    }

    private void init() {
        txtOverallStatus = findViewById(R.id.txtOverallStatus);
        txtPhoneNo = findViewById(R.id.txtPhoneNo);
        txtBikeType = findViewById(R.id.txtBikeType);
        txtBikeSize = findViewById(R.id.txtBikeSize);
        txtBikeColor = findViewById(R.id.txtBikeColor);
        txtReason = findViewById(R.id.txtReason);
        txtStatus = findViewById(R.id.txtStatus);
        txtAdminMessage = findViewById(R.id.txtAdminMessage);
        txtRespondedAt = findViewById(R.id.txtUpdatedAt);
        ivCredentialPicture = findViewById(R.id.ivCredentialPicture);
        btnResubmit = findViewById(R.id.btnResubmit);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        getData();
    }

    private void getData() {
        loadingDialog.setMessage("Checking");
        loadingDialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, Api.MY_VERIFICATION_REQUEST, response->{

            try {
                JSONObject jsonObject = new JSONObject(response);

                status = jsonObject.getString("status");
                phoneNo = jsonObject.getString("phone_no");
                type = jsonObject.getString("bike_type");
                size = jsonObject.getString("bike_size");
                color = jsonObject.getString("bike_color");
                reason = jsonObject.getString("reason");
                imgPath = jsonObject.getString("credential_file_path");
                adminMessage = jsonObject.getString("admin_message");
                respondedAt = jsonObject.getString("updated_at");

                setData();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            loadingDialog.dismiss();

        },error ->{
            loadingDialog.dismiss();
            try {
                if (errorObj.has("errors")) {
                    try {
                        JSONObject errors = errorObj.getJSONObject("errors");
                        showErrorMessage(errors);
                    } catch (JSONException ignored) {
                    }
                } else if (errorObj.has("message")) {
                    try {
                        Toasty.error(this, errorObj.getString("message"), Toast.LENGTH_SHORT, true).show();
                    } catch (JSONException ignored) {
                    }
                } else {
                    Toasty.error(this, "Request Timeout", Toast.LENGTH_SHORT, true).show();
                }
            } catch (Exception ignored) {
                Toasty.error(this, "No internet/data connection detected", Toast.LENGTH_SHORT, true).show();
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

        RequestQueue queue = Volley.newRequestQueue(BikerViewRegistrationActivity.this);
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
    private void setData() {
        if (status.equals("Pending")) {
            txtAdminMessage.setVisibility(View.GONE);
            txtRespondedAt.setVisibility(View.GONE);
            txtStatus.setTextColor(getResources().getColor(R.color.primaryColor));
        } else if (status.equals("Denied")) {
            txtOverallStatus.setText("DENIED");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.firebrick));
            btnResubmit.setVisibility(View.VISIBLE);
            txtStatus.setTextColor(getResources().getColor(R.color.firebrick));
        } else {
            txtOverallStatus.setText("APPROVED. PLEASE RE-OPEN THE PAGE TO REFRESH YOUR CREDENTIAL");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.teal_700));
            txtStatus.setTextColor(getResources().getColor(R.color.teal_700));
        }

        txtPhoneNo.setText("Phone No: " + phoneNo);
        txtBikeType.setText("Bike Type: " + type);
        txtBikeSize.setText("Bike Size: " + size);
        txtBikeColor.setText("Bike Color: " + color);
        txtReason.setText(reason);
        txtStatus.setText(status);
        txtAdminMessage.setText("Admin Message: " + adminMessage);
        txtRespondedAt.setText(respondedAt);
        Picasso.get().load(imgPath).fit().error(R.drawable.no_picture).into(ivCredentialPicture);

        initListener();
    }

    private void initListener() {
        btnResubmit.setOnClickListener(view -> {
            startActivity(new Intent(BikerViewRegistrationActivity.this, BikerRegisterActivity.class));
            finish();
        });

        ivCredentialPicture.setOnClickListener(view -> {
            Intent intent= new Intent(BikerViewRegistrationActivity.this, ViewImageActivity.class);
            intent.putExtra("image_url", imgPath);
            startActivity(intent);
        });
    }

    public void cancelEdit(View view) {
        finish();
    }
}