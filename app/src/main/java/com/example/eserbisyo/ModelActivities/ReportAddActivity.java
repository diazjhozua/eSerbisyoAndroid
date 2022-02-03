package com.example.eserbisyo.ModelActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.example.eserbisyo.HomeFragments.ReportFragment;
import com.example.eserbisyo.Models.Report;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

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

public class ReportAddActivity extends AppCompatActivity {

    private TextInputLayout layoutCustomType, layoutUrgencyClass, layoutLandmark, layoutLocation, layoutMessage;
    private TextInputEditText inputTxtCustomType, inputTxtLandmark, inputTxtLocation, inputTxtMessage;
    private AutoCompleteTextView autoCompleteUrgencyClass;
    private AppCompatCheckBox chkCustomType, chkAnonymous;
    private TextView txtSelectPhoto;
    private ImageView ivReportPicture;

    private Button btnSubmit;
    private LinearLayoutCompat linearLayoutSelectType;
    private Spinner spnType;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private boolean isCustomTypeInput, isAnonymousChecked;
    private int selectedTypeId;
    private ArrayList<Type> typeArrayList;
    private JSONObject errorObj = null;

    private Bitmap bitmap = null;

    private final String[] urgencyClassList = new String[]{
            "Urgent", "Nonurgent"
    };

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
                        ivReportPicture.setImageURI(imgUri);

                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_add);

        init();
    }

    private void init() {
        layoutCustomType = findViewById(R.id.txtLayoutCustomType);
        layoutUrgencyClass = findViewById(R.id.txtLayoutUrgencyClass);
        layoutLandmark = findViewById(R.id.txtLayoutLandmark);
        layoutLocation = findViewById(R.id.txtLayoutLocation);
        layoutMessage = findViewById(R.id.txtLayoutMessage);

        inputTxtCustomType = findViewById(R.id.inputTxtCustomType);
        inputTxtLandmark = findViewById(R.id.inputTxtLandmark);
        inputTxtLocation = findViewById(R.id.inputTxtLocation);
        inputTxtMessage = findViewById(R.id.inputTxtMessage);

        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);
        ivReportPicture = findViewById(R.id.ivReportPicture);

        autoCompleteUrgencyClass = findViewById(R.id.autoCompleteUrgencyClass);

        chkCustomType = findViewById(R.id.chkCustomType);
        chkAnonymous = findViewById(R.id.chkAnonymous);

        btnSubmit = findViewById(R.id.btnSubmit);

        linearLayoutSelectType = findViewById(R.id.layoutSelectType);

        spnType = findViewById(R.id.spinnerType);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);


        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                ReportAddActivity.this,
                R.layout.list_item,
                urgencyClassList
        );

        autoCompleteUrgencyClass.setAdapter(adapterType);


        /* INITIALIZE ALL CLICK LISTENERS */
        initListeners();

        /* LOAD TYPES IN SPINNER */
        populateSpinner();

    }

    private void initListeners() {

        //pick photo from gallery
        txtSelectPhoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            getImageResultLauncher.launch(i);
        });

        // Listener for btnSubmit
        btnSubmit.setOnClickListener(v -> {
            // store feedback
            if (validate()) {
                submitReport();
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

        chkAnonymous.setOnClickListener(view -> isAnonymousChecked = true);

        autoCompleteUrgencyClass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(autoCompleteUrgencyClass.getText()).toString().isEmpty()){
                    layoutUrgencyClass.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        inputTxtCustomType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isCustomTypeInput) {
                    if (Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()>=4 || Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()<=60){
                        layoutCustomType.setErrorEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtLandmark.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtLandmark.getText()).toString().length()>=4 || Objects.requireNonNull(inputTxtLandmark.getText()).toString().length()<=60){
                    layoutLandmark.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtLocation.getText()).toString().length()>=4 && Objects.requireNonNull(inputTxtLocation.getText()).toString().length()<=60){
                    layoutLocation.setErrorEnabled(false);
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
                if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()>=10 && Objects.requireNonNull(inputTxtLocation.getText()).toString().length()<=250){
                    layoutMessage.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void populateSpinner() {
        progressDialog.setMessage("Loading assets.....");
        progressDialog.show();
        typeArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Api.REPORTS_CREATE, response -> {

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

        RequestQueue queue = Volley.newRequestQueue(ReportAddActivity.this);
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

    private boolean validate() {
        if (isCustomTypeInput){
            if (Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()<4){
                layoutCustomType.setErrorEnabled(true);
                layoutCustomType.setError("Required at least 4 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()>60){
                layoutCustomType.setErrorEnabled(true);
                layoutCustomType.setError("Required no more than  60 characters");
                return false;
            }
        }

        if (autoCompleteUrgencyClass.getText().toString().isEmpty()){
            layoutUrgencyClass.setErrorEnabled(true);
            layoutUrgencyClass.setError("Urgency classification is required");
            return false;
        }

        if (Objects.requireNonNull(inputTxtLandmark.getText()).toString().length()<5){
            layoutLandmark.setErrorEnabled(true);
            layoutLandmark.setError("Required at least 5 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtLandmark.getText()).toString().length()>60){
            layoutLandmark.setErrorEnabled(true);
            layoutLandmark.setError("Required no more than  60 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtLocation.getText()).toString().length()<5){
            layoutLocation.setErrorEnabled(true);
            layoutLocation.setError("Required at least 5 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtLocation.getText()).toString().length()>60){
            layoutLocation.setErrorEnabled(true);
            layoutLocation.setError("Required no more than  60 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()<10){
            layoutMessage.setErrorEnabled(true);
            layoutMessage.setError("Required at least 10 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtMessage.getText()).toString().length()>250){
            layoutMessage.setErrorEnabled(true);
            layoutMessage.setError("Required no more than  250 characters");
            return false;
        }

        return true;
    }

    private void submitReport() {
        progressDialog.setMessage("Submitting please wait.....");
        progressDialog.show();

        int typeID = selectedTypeId;
        String customType = Objects.requireNonNull(inputTxtCustomType.getText()).toString().trim();
        String urgencyClassification = autoCompleteUrgencyClass.getText().toString().trim();
        String locationAddress = Objects.requireNonNull(inputTxtLocation.getText()).toString().trim();
        String landmark = Objects.requireNonNull(inputTxtLandmark.getText()).toString().trim();
        String message = Objects.requireNonNull(inputTxtMessage.getText()).toString().trim();
        int isAnonymous = (isAnonymousChecked) ? 1 : 0;
        StringRequest request = new StringRequest(Request.Method.POST, Api.REPORTS, response->{

            try {
                JSONObject object = new JSONObject(response);

                JSONObject reportObj = object.getJSONObject("data");

                Report report = new Report(
                        reportObj.getInt("id"), reportObj.getString("submitted_by"), new Type(reportObj.getInt("type_id"), reportObj.getString("report_type")),
                        reportObj.getString("custom_type"), reportObj.getString("location_address"), reportObj.getString("landmark") , reportObj.getString("description"),
                        reportObj.getString("urgency_classification"), reportObj.getString("picture_name"), reportObj.getString("file_path"), reportObj.getString("admin_message"),
                        reportObj.getString("status"), reportObj.getString("created_at"), reportObj.getString("updated_at")
                );

                ReportFragment.arrayList.add(0,report);
                Objects.requireNonNull(ReportFragment.recyclerView.getAdapter()).notifyItemInserted(0);
                ReportFragment.recyclerView.getAdapter().notifyDataSetChanged();
                Toasty.success(this, "Your report has been submitted successfully, please wait for the administrator to respond to your report", Toast.LENGTH_SHORT, true).show();
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
                map.put("urgency_classification",urgencyClassification);
                map.put("location_address",locationAddress);
                map.put("landmark",landmark);
                map.put("description", message);
                map.put("is_anonymous", String.valueOf(isAnonymous));
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

        RequestQueue queue = Volley.newRequestQueue(ReportAddActivity.this);
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

    public void cancelEdit(View view) {
        finish();
    }
}