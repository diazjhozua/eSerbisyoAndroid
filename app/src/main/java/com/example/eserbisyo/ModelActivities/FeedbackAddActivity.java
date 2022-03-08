package com.example.eserbisyo.ModelActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Adapters.DDTypeAdapter;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.FeedbackFragment;
import com.example.eserbisyo.Models.Feedback;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class FeedbackAddActivity extends AppCompatActivity {

    private TextInputLayout layoutCustomType, layoutPolarity, layoutMessage;
    private TextInputEditText inputTxtCustomType, inputTxtMessage;
    private AutoCompleteTextView autoCompleteTxtPolarity;
    private AppCompatCheckBox chkCustomType, chkAnonymous;

    private Button btnSubmit;
    private LinearLayoutCompat linearLayoutSelectType;
    private Spinner spnType;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private boolean isCustomTypeInput, isAnonymousChecked;
    private int selectedTypeId;
    private ArrayList<Type> typeArrayList;
    private JSONObject errorObj = null;

    private final String[] polarity = new String[]{
            "1", "2", "3", "4", "5"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_add);

        init();
    }

    private void init() {
        layoutCustomType = findViewById(R.id.txtLayoutCustomType);
        layoutPolarity = findViewById(R.id.txtLayoutPolarity);
        layoutMessage = findViewById(R.id.txtLayoutMessage);

        inputTxtCustomType = findViewById(R.id.inputTxtCustomType);
        inputTxtMessage = findViewById(R.id.inputTxtMessage);

        autoCompleteTxtPolarity = findViewById(R.id.autoCompletePolarity);

        chkCustomType = findViewById(R.id.chkCustomType);
        chkAnonymous = findViewById(R.id.chkAnonymous);

        btnSubmit = findViewById(R.id.btnSubmit);

        linearLayoutSelectType = findViewById(R.id.layoutSelectType);

        spnType = findViewById(R.id.spinnerType);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);


        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                FeedbackAddActivity.this,
                R.layout.list_item,
                polarity
        );

        autoCompleteTxtPolarity.setAdapter(adapterType);

        /* INITIALIZE ALL CLICK LISTENERS */
        initListeners();

        /* LOAD TYPES IN SPINNER */
        populateSpinner();
    }

    private void initListeners() {
        // Listener for btnSubmit
        btnSubmit.setOnClickListener(v -> {
            // store feedback
            if (validate()) {
                submitFeedback();
            }
        });

        chkCustomType.setOnClickListener(view -> {
            if(((CompoundButton) view).isChecked()){
                linearLayoutSelectType.setVisibility(View.GONE);
                layoutCustomType.setVisibility(View.VISIBLE);
                isCustomTypeInput = true;
            } else {
                linearLayoutSelectType.setVisibility(View.VISIBLE);
                layoutCustomType.setVisibility(View.GONE);
                isCustomTypeInput = false;
            }
        });

        chkAnonymous.setOnClickListener(view -> isAnonymousChecked = ((CompoundButton) view).isChecked());

        autoCompleteTxtPolarity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(autoCompleteTxtPolarity.getText()).toString().isEmpty()){
                    layoutPolarity.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        inputTxtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()>4){
                    layoutMessage.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtCustomType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isCustomTypeInput) {
                    if (Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()>3){
                        layoutCustomType.setErrorEnabled(false);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean validate() {

        if (isCustomTypeInput){
            if (Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()<4){
                layoutCustomType.setErrorEnabled(true);
                layoutCustomType.setError("Required at least 4 characters");
                return false;
            }
        }

        if (autoCompleteTxtPolarity.getText().toString().isEmpty()){
            layoutPolarity.setErrorEnabled(true);
            layoutPolarity.setError("Rating is required");
            return false;
        }

        if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()<5){
            layoutMessage.setErrorEnabled(true);
            layoutMessage.setError("Required at least 5 characters");
            return false;
        }

        return true;
    }

    private void submitFeedback() {
        progressDialog.setMessage("Submitting please wait.....");
        progressDialog.show();

        int typeID = selectedTypeId;
        String customType = Objects.requireNonNull(inputTxtCustomType.getText()).toString().trim();
        String rating = autoCompleteTxtPolarity.getText().toString().trim();
        String message = Objects.requireNonNull(inputTxtMessage.getText()).toString().trim();
        int isAnonymous = (isAnonymousChecked) ? 1 : 0;
        StringRequest request = new StringRequest(Request.Method.POST, Api.FEEDBACKS, response->{

            try {

                JSONObject object = new JSONObject(response);

                JSONObject feedbackObject = object.getJSONObject("data");

                Feedback feedback = new Feedback(
                        feedbackObject.getInt("id"), feedbackObject.getString("submitted_by"), new Type(feedbackObject.getInt("type_id"), feedbackObject.getString("type")),
                        feedbackObject.getString("custom_type"), feedbackObject.getInt("rating"), feedbackObject.getString("message") , feedbackObject.getString("admin_respond"),
                        feedbackObject.getString("status"), feedbackObject.getString("created_at"), feedbackObject.getString("updated_at"));

                FeedbackFragment.arrayList.add(0,feedback);
                Objects.requireNonNull(FeedbackFragment.recyclerView.getAdapter()).notifyItemInserted(0);
                FeedbackFragment.recyclerView.getAdapter().notifyDataSetChanged();
                Toasty.success(this, "Your feedback has been submitted successfully, please wait for the administrator to respond to your feedback", Toast.LENGTH_LONG, true).show();
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

                if (isCustomTypeInput) {
                    map.put("custom_type", customType);
                } else {
                    map.put("type_id", String.valueOf(typeID));
                }
                map.put("rating",rating);
                map.put("message", message);
                map.put("is_anonymous", String.valueOf(isAnonymous));

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

        RequestQueue queue = Volley.newRequestQueue(FeedbackAddActivity.this);
        queue.add(request);

    }


    private void populateSpinner() {
        progressDialog.setMessage("Loading assets.....");
        progressDialog.show();
        typeArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Api.FEEDBACKS_CREATE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONArray typeArray = new JSONArray(object.getString("types"));
                for (int i = 0; i < typeArray.length(); i++) {
                    JSONObject typeObject = typeArray.getJSONObject(i);
                    Type type = new Type(
                            typeObject.getInt("id"),
                            typeObject.getString("name")
                    );
                    typeArrayList.add(type);
                }

                /* SET Adapters */
                setAdapters();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        },error -> {
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
        }){

            // provide token in header
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

        RequestQueue queue = Volley.newRequestQueue(FeedbackAddActivity.this);
        queue.add(request);
    }

    private void setAdapters() {
        DDTypeAdapter ddTypeAdapter = new DDTypeAdapter(this, typeArrayList);

        spnType.setAdapter(ddTypeAdapter);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Type type = (Type) parent.getSelectedItem();
                selectedTypeId = type.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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