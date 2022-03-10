package com.example.eserbisyo.AccountActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputLayout layoutCurrentPassword, layoutNewPassword, layoutConfirmNewPassword;
    private TextInputEditText inputTxtCurrentPassword, inputTxtNewPassword, inputTxtConfirmNewPassword;
    private Button btnSave;

    private SharedPreferences userPref;
    private ProgressDialog loadingDialog;

    private JSONObject errorObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init();
    }

    private void init() {
        layoutCurrentPassword = findViewById(R.id.txtLayoutCurrentPassword);
        layoutNewPassword = findViewById(R.id.txtLayoutNewPassword);
        layoutConfirmNewPassword = findViewById(R.id.txtLayoutConfirmNewPassword);

        inputTxtCurrentPassword = findViewById(R.id.inputTxtCurrentPassword);
        inputTxtNewPassword = findViewById(R.id.inputTxtNewPassword);
        inputTxtConfirmNewPassword = findViewById(R.id.inputTxtConfirmNewPassword);

        btnSave = findViewById(R.id.btnChangePassword);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        initListeners();
    }

    private void initListeners() {
        btnSave.setOnClickListener(v->{
            // validate fields
            if(validate()){
                changePassword();
            }
        });

        inputTxtCurrentPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtCurrentPassword.getText()).toString().length()>7){
                    layoutCurrentPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtNewPassword.getText()).toString().length()>7){
                    layoutNewPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtConfirmNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtConfirmNewPassword.getText()).toString().equals(Objects.requireNonNull(inputTxtNewPassword.getText()).toString())){
                    layoutConfirmNewPassword.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {

        if (Objects.requireNonNull(inputTxtCurrentPassword.getText()).toString().length()<8){
            layoutCurrentPassword.setErrorEnabled(true);
            layoutCurrentPassword.setError("Required at least 8 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtNewPassword.getText()).toString().length()<8){
            layoutNewPassword.setErrorEnabled(true);
            layoutNewPassword.setError("Required at least 8 characters");
            return false;
        }

        if (!Objects.requireNonNull(inputTxtConfirmNewPassword.getText()).toString().equals(inputTxtNewPassword.getText().toString())){
            layoutConfirmNewPassword.setErrorEnabled(true);
            layoutConfirmNewPassword.setError("Password does not match");
            return false;
        }

        return true;
    }

    private void changePassword() {
        loadingDialog.setMessage("Saving");
        loadingDialog.show();

        String currentPassword = Objects.requireNonNull(inputTxtCurrentPassword.getText()).toString().trim();
        String newPassword = Objects.requireNonNull(inputTxtNewPassword.getText()).toString().trim();
        String confirmNewPassword = Objects.requireNonNull(inputTxtConfirmNewPassword.getText()).toString().trim();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.CHANGE_PASSWORD, response->{

            SharedPreferences.Editor editor = userPref.edit();
            editor.putString(Pref.PASSWORD, newPassword);
            editor.apply();

            Toasty.success(this, "Password has been updated", Toast.LENGTH_LONG, true).show();

            finish();

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

            //add params
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("current_password", currentPassword);
                map.put("new_password", newPassword);
                map.put("new_confirm_password", confirmNewPassword);
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

        RequestQueue queue = Volley.newRequestQueue(ChangePasswordActivity.this);
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

    public void cancelEdit(View view) {
        finish();
    }
}