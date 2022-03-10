package com.example.eserbisyo.AccountActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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

public class ChangeEmailActivity extends AppCompatActivity {

    private TextInputLayout layoutCurrentEmail, layoutNewEmail, layoutConfirmNewEmail;
    private TextInputEditText inputTxtCurrentEmail, inputTxtNewEmail, inputTxtConfirmNewEmail;
    private Button btnSave;

    private SharedPreferences userPref;
    private ProgressDialog loadingDialog;

    private JSONObject errorObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);
        init();
    }

    private void init() {
        layoutCurrentEmail = findViewById(R.id.txtLayoutCurrentEmail);
        layoutNewEmail = findViewById(R.id.txtLayoutNewEmail);
        layoutConfirmNewEmail = findViewById(R.id.txtLayoutConfirmNewEmail);

        inputTxtCurrentEmail = findViewById(R.id.inputTxtCurrentEmail);
        inputTxtNewEmail = findViewById(R.id.inputTxtNewEmail);
        inputTxtConfirmNewEmail = findViewById(R.id.inputTxtConfirmNewEmail);

        btnSave = findViewById(R.id.btnChangeEmail);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        initListeners();
    }

    private void initListeners() {
        btnSave.setOnClickListener(v->{
            // validate fields
            if(validate()){
                changeEmail();
            }
        });

        inputTxtCurrentEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputTxtCurrentEmail.getText()).toString().trim().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(inputTxtCurrentEmail.getText().toString().trim()).matches()){
                    layoutCurrentEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtNewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputTxtNewEmail.getText()).toString().trim().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(inputTxtNewEmail.getText().toString().trim()).matches()){
                    layoutNewEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtConfirmNewEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtConfirmNewEmail.getText()).toString().equals(Objects.requireNonNull(inputTxtNewEmail.getText()).toString())){
                    layoutConfirmNewEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {

        if (Objects.requireNonNull(inputTxtCurrentEmail.getText()).toString().trim().isEmpty()){
            layoutCurrentEmail.setErrorEnabled(true);
            layoutCurrentEmail.setError("Current email is Required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(inputTxtCurrentEmail.getText().toString().trim()).matches()){
            layoutCurrentEmail.setErrorEnabled(true);
            layoutCurrentEmail.setError("Invalid email address");
            return false;
        }

        if (Objects.requireNonNull(inputTxtNewEmail.getText()).toString().isEmpty()){
            layoutNewEmail.setErrorEnabled(true);
            layoutNewEmail.setError("New email is Required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(inputTxtNewEmail.getText().toString().trim()).matches()){
            layoutNewEmail.setErrorEnabled(true);
            layoutNewEmail.setError("Invalid email address");
            return false;
        }

        if (!Objects.requireNonNull(inputTxtConfirmNewEmail.getText()).toString().equals(inputTxtNewEmail.getText().toString().trim())){
            layoutConfirmNewEmail.setErrorEnabled(true);
            layoutConfirmNewEmail.setError("New email does not match");
            return false;
        }

        return true;
    }

    private void changeEmail() {
        loadingDialog.setMessage("Saving");
        loadingDialog.show();

        String currentEmail = Objects.requireNonNull(inputTxtCurrentEmail.getText()).toString().trim();
        String newEmail = Objects.requireNonNull(inputTxtNewEmail.getText()).toString().trim();
        String confirmNewEmail = Objects.requireNonNull(inputTxtConfirmNewEmail.getText()).toString().trim();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.CHANGE_EMAIL, response->{

            try {
                JSONObject object = new JSONObject(response);

                SharedPreferences.Editor editor = userPref.edit();
                editor.putString(Pref.EMAIL, object.getString("email"));
                editor.apply();

                Toasty.success(this, "Email has been updated", Toast.LENGTH_LONG, true).show();

                finish();

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

            //add params
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("current_email", currentEmail);
                map.put("new_email", newEmail);
                map.put("new_confirm_email", confirmNewEmail);
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

        RequestQueue queue = Volley.newRequestQueue(ChangeEmailActivity.this);
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