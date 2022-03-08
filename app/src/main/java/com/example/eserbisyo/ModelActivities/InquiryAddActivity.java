package com.example.eserbisyo.ModelActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.FeedbackFragment;
import com.example.eserbisyo.HomeFragments.InquiryFragment;
import com.example.eserbisyo.Models.Feedback;
import com.example.eserbisyo.Models.Inquiry;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class InquiryAddActivity extends AppCompatActivity {

    private TextInputLayout layoutAbout, layoutMessage;
    private TextInputEditText inputTxtAbout, inputTxtMessage;

    private Button btnSubmit;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private JSONObject errorObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inquiry_add);
        init();
    }

    private void init() {
        layoutAbout = findViewById(R.id.txtLayoutAbout);
        layoutMessage = findViewById(R.id.txtLayoutMessage);

        inputTxtMessage = findViewById(R.id.inputTxtMessage);
        inputTxtAbout = findViewById(R.id.inputTxtAbout);

        btnSubmit = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        /* INITIALIZE ALL CLICK LISTENERS */
        initListeners();

    }

    private void initListeners() {
        btnSubmit.setOnClickListener(v -> {
            // store feedback
            if (validate()) {
                submitData();
            }
        });

        inputTxtAbout.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtAbout.getText()).toString().length()>4 && Objects.requireNonNull(inputTxtAbout.getText()).toString().length() < 81){
                    layoutAbout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()>4 && Objects.requireNonNull(inputTxtMessage.getText()).toString().length() < 501){
                    layoutMessage.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }



    private boolean validate() {

        if (Objects.requireNonNull(inputTxtAbout.getText()).toString().length()<5){
            layoutAbout.setErrorEnabled(true);
            layoutAbout.setError("Required at least 5 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtAbout.getText()).toString().length() > 80){
            layoutAbout.setErrorEnabled(true);
            layoutAbout.setError("Required no more than 80 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()<5){
            layoutMessage.setErrorEnabled(true);
            layoutMessage.setError("Required at least 5 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length() > 500){
            layoutMessage.setErrorEnabled(true);
            layoutMessage.setError("Required no more than 500 characters");
            return false;
        }

        return true;
    }

    private void submitData() {
        progressDialog.setMessage("Submitting please wait.....");
        progressDialog.show();

        String about = Objects.requireNonNull(inputTxtAbout.getText()).toString().trim();
        String message = Objects.requireNonNull(inputTxtMessage.getText()).toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, Api.INQUIRIES, response->{

            try {

                JSONObject object = new JSONObject(response);

                JSONObject inquiryObject = object.getJSONObject("data");

                Inquiry inquiry = new Inquiry(
                        inquiryObject.getInt("id"), inquiryObject.getString("about"), inquiryObject.getString("message") , inquiryObject.getString("admin_message"),
                        inquiryObject.getString("status"), inquiryObject.getString("created_at"), inquiryObject.getString("updated_at"));

                InquiryFragment.arrayList.add(0,inquiry);
                Objects.requireNonNull(InquiryFragment.recyclerView.getAdapter()).notifyItemInserted(0);
                InquiryFragment.recyclerView.getAdapter().notifyDataSetChanged();
                Toasty.success(this, "Your inquiry has been submitted successfully, please wait for the administrator to respond to your inquiry", Toast.LENGTH_LONG, true).show();
                finish();

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

                map.put("about",about);
                map.put("message", message);

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

        RequestQueue queue = Volley.newRequestQueue(InquiryAddActivity.this);
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