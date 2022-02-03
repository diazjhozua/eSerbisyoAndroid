package com.example.eserbisyo.ModelActivities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.eserbisyo.HomeFragments.AuthMissingItemFragment;
import com.example.eserbisyo.HomeFragments.ReportFragment;
import com.example.eserbisyo.Models.MissingItem;
import com.example.eserbisyo.Models.Report;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MissingItemAddActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;

    private CircleImageView cirIvMissingPicture;
    private ImageView ivCredentialPicture;
    private TextView txtSelectMissingPic, txtCaptureMissingPic, txtSelectCredentialPic, txtCaptureCredentialPic;
    private TextInputLayout layoutReportType, layoutMissingName, layoutLastSeen, layoutDesc, layoutEmail, layoutPhone;
    private TextInputEditText inputMissingName, inputLastSeen, inputDesc, inputEmail, inputPhone;
    private AutoCompleteTextView autoCompleteReportType;
    private Button btnSubmit;

    private String isMyReport = "NO";
    private Boolean isSelectingCredential, isCapturingCredential;


    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private JSONObject errorObj = null;

    private Bitmap pictureBitmap = null;
    private Bitmap credentialBitmap = null;



    private final String[] reportTypeSelector = new String[]{
            "Missing", "Found"
    };

    // Getting of images from the gallery
    ActivityResultLauncher<Intent> getImageResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        assert data != null;
                        Uri imgUri = data.getData();

                        try {
                            if (isSelectingCredential) {
                                ivCredentialPicture.setImageURI(imgUri);
                                credentialBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                            } else {
                                cirIvMissingPicture.setImageURI(imgUri);
                                pictureBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri);
                            }
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

                        if (isCapturingCredential) {
                            credentialBitmap = (Bitmap) data.getExtras().get("data");
                            ivCredentialPicture.setImageBitmap(credentialBitmap);
                        } else {
                            pictureBitmap = (Bitmap) data.getExtras().get("data");
                            cirIvMissingPicture.setImageBitmap(pictureBitmap);
                        }

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_item_add);
        init();
    }

    private void init() {

        isMyReport = getIntent().getStringExtra(Extra.IS_MY_REPORT);

        cirIvMissingPicture = findViewById(R.id.circIvMissingPicture);
        ivCredentialPicture = findViewById(R.id.ivCredentialPicture);

        txtSelectMissingPic = findViewById(R.id.txtSelectMissingPhoto);
        txtCaptureMissingPic = findViewById(R.id.txtCaptureMissingPhoto);

        txtSelectCredentialPic = findViewById(R.id.txtSelectCredentialPhoto);
        txtCaptureCredentialPic = findViewById(R.id.txtCaptureCredentialPhoto);

        layoutReportType = findViewById(R.id.txtLayoutReportType);
        layoutMissingName = findViewById(R.id.txtLayoutMissingName);
        layoutLastSeen = findViewById(R.id.txtLayoutLastSeen);
        layoutDesc = findViewById(R.id.txtLayoutDesc);
        layoutEmail = findViewById(R.id.txtLayoutEmail);
        layoutPhone = findViewById(R.id.txtLayoutPhone);

        inputMissingName = findViewById(R.id.inputTxtMissingName);
        inputLastSeen = findViewById(R.id.inputTxtLastSeen);
        inputDesc = findViewById(R.id.inputTxtDesc);
        inputEmail = findViewById(R.id.inputTxtEmail);
        inputPhone = findViewById(R.id.inputTxtPhone);

        autoCompleteReportType = findViewById(R.id.autoCompleteReportType);

        btnSubmit = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
        inputEmail.setText(userPref.getString(Pref.EMAIL, ""));
        populateAutoComplete();
    }

    private void populateAutoComplete() {
        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                MissingItemAddActivity.this,
                R.layout.list_item,
                reportTypeSelector
        );

        autoCompleteReportType.setAdapter(adapterType);

        initListeners();
    }

    private void initListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (validate()) {
                submitData();
            }
        });

        txtSelectCredentialPic.setOnClickListener(v->{
            isSelectingCredential = true;
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            getImageResultLauncher.launch(i);
        });

        txtSelectMissingPic.setOnClickListener(v->{
            isSelectingCredential = false;
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            getImageResultLauncher.launch(i);
        });

        txtCaptureCredentialPic.setOnClickListener(v->{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            }else {
                isCapturingCredential = true;
                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);

                getCaptureResultLauncher.launch(camera_intent);
            }
        });

        txtCaptureMissingPic.setOnClickListener(v->{

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            }else {
                isCapturingCredential = false;
                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);

                getCaptureResultLauncher.launch(camera_intent);
            }
        });

        autoCompleteReportType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(autoCompleteReportType.getText()).toString().isEmpty()){
                    layoutReportType.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        inputMissingName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputMissingName.getText()).toString().length() >= 3 &&
                        Objects.requireNonNull(inputMissingName.getText()).toString().length() <= 120){
                    layoutMissingName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputLastSeen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputLastSeen.getText()).toString().length() >= 3 &&
                        Objects.requireNonNull(inputLastSeen.getText()).toString().length() <= 120){
                    layoutLastSeen.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputDesc.getText()).toString().length() >= 3 &&
                        Objects.requireNonNull(inputDesc.getText()).toString().length() <= 250){
                    layoutLastSeen.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputDesc.getText()).toString().length() >= 3 &&
                        Objects.requireNonNull(inputDesc.getText()).toString().length() <= 250){
                    layoutLastSeen.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputEmail.getText()).toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputPhone.getText()).toString().isEmpty() && Objects.requireNonNull(inputPhone.getText()).toString().length() == 11){
                    layoutPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @SuppressLint("CheckResult")
    private boolean validate() {
        if (pictureBitmap == null) {
            Toasty.error(this, "You need to select a missing picture", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (credentialBitmap == null) {
            Toasty.error(this, "You need to select a credential picture", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (autoCompleteReportType.getText().toString().isEmpty()){
            layoutReportType.setErrorEnabled(true);
            layoutReportType.setError("Report type is required");
            return false;
        }

        /* Missing Name Validate */
        if (Objects.requireNonNull(inputMissingName.getText()).toString().length() < 3){
            layoutMissingName.setErrorEnabled(true);
            layoutMissingName.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputMissingName.getText()).toString().length() > 250){
            layoutMissingName.setErrorEnabled(true);
            layoutMissingName.setError("Required no more than  250 characters");
            return false;
        }

        /* Last Seen Validate */
        if (Objects.requireNonNull(inputLastSeen.getText()).toString().length() < 3){
            layoutLastSeen.setErrorEnabled(true);
            layoutLastSeen.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputLastSeen.getText()).toString().length() > 120){
            layoutLastSeen.setErrorEnabled(true);
            layoutLastSeen.setError("Required no more than  120 characters");
            return false;
        }

        /* Description Validate */
        if (Objects.requireNonNull(inputDesc.getText()).toString().length() < 3){
            layoutDesc.setErrorEnabled(true);
            layoutDesc.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputDesc.getText()).toString().length() > 250){
            layoutDesc.setErrorEnabled(true);
            layoutDesc.setError("Required no more than 250 characters");
            return false;
        }

        /* Email Validation  */
        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(inputEmail.getText()).toString()).matches() || Objects.requireNonNull(inputEmail.getText()).toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Invalid email address");
            return false;
        }

        /* Phone Validation */
        if (Objects.requireNonNull(inputPhone.getText()).toString().length() != 11){
            layoutPhone.setErrorEnabled(true);
            layoutPhone.setError("Invalid phone number. Must start at 0919*******");
            return false;
        }
        return true;
    }


    private void submitData() {
        progressDialog.setMessage("Submitting data please wait.....");
        progressDialog.show();

        String itemName = Objects.requireNonNull(inputMissingName.getText()).toString().trim();
        String lastSeen = Objects.requireNonNull(inputLastSeen.getText()).toString().trim();
        String description = Objects.requireNonNull(inputDesc.getText()).toString().trim();
        String email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
        String phoneNo = Objects.requireNonNull(inputPhone.getText()).toString().trim();
        String reportType = autoCompleteReportType.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, Api.MISSING_ITEMS, response->{
            try {
                JSONObject object = new JSONObject(response);

                JSONObject missingItemJSONObject = object.getJSONObject("data");

                MissingItem missingItemObj = new MissingItem(
                        missingItemJSONObject.getInt("id"), missingItemJSONObject.getInt("user_id"), missingItemJSONObject.getString("user_name"), missingItemJSONObject.getString("user_picture_name"),
                        missingItemJSONObject.getString("user_file_path"), missingItemJSONObject.getString("report_type"), missingItemJSONObject.getString("item"), missingItemJSONObject.getString("last_seen"),
                        missingItemJSONObject.getString("description"), missingItemJSONObject.getString("email"), missingItemJSONObject.getString("phone_no"), missingItemJSONObject.getString("picture_name"),
                        missingItemJSONObject.getString("file_path"), missingItemJSONObject.getString("credential_name"), missingItemJSONObject.getString("credential_path"),
                        missingItemJSONObject.getInt("comments_count"), missingItemJSONObject.getString("status"), missingItemJSONObject.getString("admin_message"),  missingItemJSONObject.getString("created_at"),
                        missingItemJSONObject.getString("updated_at"));

                if (isMyReport.equals("YES")) {
                    /* Meaning AuthMissingItemFragment Calls this activity */
                    AuthMissingItemFragment.arrayList.add(0,missingItemObj);
                    Objects.requireNonNull(AuthMissingItemFragment.recyclerView.getAdapter()).notifyItemInserted(0);
                    AuthMissingItemFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toasty.success(this, "Your report has been submitted successfully, please wait for the administrator to respond to your report", Toast.LENGTH_LONG, true).show();
                } else {
                    Toasty.success(this, "Your report has been submitted successfully, please go to the sidebar menu to see your report", Toast.LENGTH_LONG, true).show();
                }

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

                map.put("item", itemName);
                map.put("last_seen",lastSeen);
                map.put("description",description);
                map.put("email", email);
                map.put("phone_no", phoneNo);
                map.put("report_type", reportType);
                map.put("picture",bitmapToString(pictureBitmap));
                map.put("credential",bitmapToString(credentialBitmap));
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

        RequestQueue queue = Volley.newRequestQueue(MissingItemAddActivity.this);
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