package com.example.eserbisyo.ModelActivities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import com.android.volley.DefaultRetryPolicy;
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
import com.example.eserbisyo.HomeFragments.AuthMissingPersonFragment;
import com.example.eserbisyo.HomeFragments.MissingItemFragment;
import com.example.eserbisyo.HomeFragments.MissingPersonFragment;
import com.example.eserbisyo.Models.MissingItem;
import com.example.eserbisyo.Models.MissingPerson;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

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

public class MissingPersonEditActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;

    private CircleImageView cirIvMissingPicture;
    private ImageView ivCredentialPicture;
    private TextView txtSelectMissingPic, txtCaptureMissingPic, txtSelectCredentialPic, txtCaptureCredentialPic;
    private TextInputLayout layoutReportType, layoutMissingName,
            layoutHeight, layoutHeightUnit, layoutWeight, layoutWeightUnit,
            layoutEyeColor, layoutHairColor, layoutAge, layoutUniqueSign,
            layoutLastSeen, layoutImportantInfo, layoutEmail, layoutPhone;

    private TextInputEditText inputMissingName, inputHeight,
            inputWeight, inputEyeColor, inputHairColor, inputAge, inputUniqueSign,
            inputLastSeen, inputImportantInfo, inputEmail, inputPhone;

    private AutoCompleteTextView autoCompleteReportType, autoCompleteWeightUnit, autoCompleteHeightUnit;

    private Button btnSubmit;

    private Boolean isSelectingCredential, isCapturingCredential;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private JSONObject errorObj = null;

    private Bitmap pictureBitmap = null;
    private Bitmap credentialBitmap = null;

    private JSONObject missingPersonJsonObj;
    private MissingPerson mMissingPerson;
    private int selectedPosition;

    private final String[] reportTypeSelector = new String[]{
            "Missing", "Found"
    };

    private final String[] weightUnitSelector = new String[]{
            "kilogram(kg)", "pound(lbs)"
    };

    private final String[] heightUnitSelector = new String[]{
            "feet(ft)", "centimeter(cm)"
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
        setContentView(R.layout.activity_missing_person_edit);

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            try {
                missingPersonJsonObj= new JSONObject(extras.getString(Extra.JSON_OBJECT));
                selectedPosition = extras.getInt(Extra.MODEL_POSITION, 0);

                mMissingPerson = new MissingPerson(
                        missingPersonJsonObj.getInt("id"), missingPersonJsonObj.getInt("contact_id"), missingPersonJsonObj.getString("contact_name"), missingPersonJsonObj.getString("user_picture_name"),
                        missingPersonJsonObj.getString("user_file_path"), missingPersonJsonObj.getString("report_type"), missingPersonJsonObj.getString("name"), missingPersonJsonObj.getDouble("height"),
                        missingPersonJsonObj.getString("height_unit"), missingPersonJsonObj.getDouble("weight"), missingPersonJsonObj.getString("weight_unit"), missingPersonJsonObj.getInt("age"),
                        missingPersonJsonObj.getString("eyes"), missingPersonJsonObj.getString("hair"), missingPersonJsonObj.getString("unique_sign"), missingPersonJsonObj.getString("important_information"),
                        missingPersonJsonObj.getString("last_seen"), missingPersonJsonObj.getString("email"), missingPersonJsonObj.getString("phone_no"), missingPersonJsonObj.getString("picture_name"),
                        missingPersonJsonObj.getString("file_path"), missingPersonJsonObj.getString("credential_name"), missingPersonJsonObj.getString("credential_path"),
                        missingPersonJsonObj.getInt("comments_count"), missingPersonJsonObj.getString("status"), missingPersonJsonObj.getString("admin_message"),  missingPersonJsonObj.getString("created_at"),
                        missingPersonJsonObj.getString("updated_at"));

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        init();

    }

    private void init() {
        cirIvMissingPicture = findViewById(R.id.circIvMissingPicture);
        ivCredentialPicture = findViewById(R.id.ivCredentialPicture);

        txtSelectMissingPic = findViewById(R.id.txtSelectMissingPhoto);
        txtCaptureMissingPic = findViewById(R.id.txtCaptureMissingPhoto);

        txtSelectCredentialPic = findViewById(R.id.txtSelectCredentialPhoto);
        txtCaptureCredentialPic = findViewById(R.id.txtCaptureCredentialPhoto);


        autoCompleteReportType = findViewById(R.id.autoCompleteReportType);
        autoCompleteHeightUnit = findViewById(R.id.autoCompleteHeightUnit);
        autoCompleteWeightUnit = findViewById(R.id.autoCompleteWeightUnit);

        layoutReportType = findViewById(R.id.txtLayoutReportType);
        layoutMissingName = findViewById(R.id.txtLayoutMissingName);
        layoutHeight = findViewById(R.id.txtLayoutHeight);
        layoutHeightUnit = findViewById(R.id.txtLayoutHeightUnit);
        layoutWeight = findViewById(R.id.txtLayoutWeight);
        layoutWeightUnit = findViewById(R.id.txtLayoutWeightUnit);
        layoutEyeColor = findViewById(R.id.txtLayoutEyeColor);
        layoutHairColor = findViewById(R.id.txtLayoutHairColor);
        layoutAge = findViewById(R.id.txtLayoutAge);
        layoutUniqueSign = findViewById(R.id.txtLayoutUniqueSign);
        layoutLastSeen = findViewById(R.id.txtLayoutLastSeen);
        layoutImportantInfo = findViewById(R.id.txtLayoutImportantInformation);
        layoutEmail = findViewById(R.id.txtLayoutEmail);
        layoutPhone = findViewById(R.id.txtLayoutPhone);

        inputMissingName = findViewById(R.id.inputTxtMissingName);
        inputHeight = findViewById(R.id.inputTxtHeight);
        inputWeight = findViewById(R.id.inputTxtWeight);
        inputEyeColor = findViewById(R.id.inputTxtEyeColor);
        inputHairColor = findViewById(R.id.inputTxtHairColor);
        inputAge = findViewById(R.id.inputTxtAge);
        inputUniqueSign = findViewById(R.id.inputTxtUniqueSign);
        inputLastSeen = findViewById(R.id.inputTxtLastSeen);
        inputImportantInfo = findViewById(R.id.inputTxtImportantInformation);
        inputEmail = findViewById(R.id.inputTxtEmail);
        inputPhone = findViewById(R.id.inputTxtPhone);


        btnSubmit = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        populateAutoComplete();
    }

    private void populateAutoComplete() {
        ArrayAdapter<String> adapterReportType = new ArrayAdapter<>(
                MissingPersonEditActivity.this,
                R.layout.list_item,
                reportTypeSelector
        );

        ArrayAdapter<String> adapterWeightUnit = new ArrayAdapter<>(
                MissingPersonEditActivity.this,
                R.layout.list_item,
                weightUnitSelector
        );

        ArrayAdapter<String> adapterHeightUnit = new ArrayAdapter<>(
                MissingPersonEditActivity.this,
                R.layout.list_item,
                heightUnitSelector
        );

        autoCompleteReportType.setAdapter(adapterReportType);
        autoCompleteWeightUnit.setAdapter(adapterWeightUnit);
        autoCompleteHeightUnit.setAdapter(adapterHeightUnit);

        populateInputFields();
    }

    private void populateInputFields() {
        layoutReportType.setHint("Current: " + mMissingPerson.getReportType());
        layoutHeightUnit.setHint("Current: " + mMissingPerson.getHeightUnit());
        layoutWeightUnit.setHint("Current: " + mMissingPerson.getWeightUnit());


        Picasso.get().load(mMissingPerson.getPicturePath()).fit().error(R.drawable.user).into(cirIvMissingPicture);
        Picasso.get().load(mMissingPerson.getCredentialPath()).fit().error(R.drawable.user).into(ivCredentialPicture);

        inputMissingName.setText(mMissingPerson.getMissingName());
        inputHeight.setText(String.valueOf(mMissingPerson.getHeight()));
        inputWeight.setText(String.valueOf(mMissingPerson.getWeight()));
        inputEyeColor.setText(mMissingPerson.getEyes());
        inputHairColor.setText(mMissingPerson.getHair());
        inputAge.setText(String.valueOf(mMissingPerson.getAge()));
        inputUniqueSign.setText(mMissingPerson.getUniqueSign());
        inputImportantInfo.setText(mMissingPerson.getImportantInfo());
        inputHairColor.setText(mMissingPerson.getHair());
        inputLastSeen.setText(mMissingPerson.getLastSeen());
        inputEmail.setText(mMissingPerson.getEmail());
        inputPhone.setText(mMissingPerson.getPhoneNo());

        initListeners();
    }

    private void initListeners() {
        btnSubmit.setOnClickListener(v -> {
            if (validate()) {
                updateData();
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

        autoCompleteHeightUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(autoCompleteHeightUnit.getText()).toString().isEmpty()){
                    layoutHeightUnit.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        autoCompleteWeightUnit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(autoCompleteWeightUnit.getText()).toString().isEmpty()){
                    layoutWeightUnit.setErrorEnabled(false);
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
                        Objects.requireNonNull(inputMissingName.getText()).toString().length() <= 100){
                    layoutMissingName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputHeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Double.parseDouble(inputHeight.getText().toString().trim()) >= 1 &&
                        Double.parseDouble(inputHeight.getText().toString().trim()) <= 1000.99){
                    layoutHeight.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Double.parseDouble(inputWeight.getText().toString().trim()) >= 1 &&
                        Double.parseDouble(inputWeight.getText().toString().trim()) <= 1000.99){
                    layoutWeight.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Integer.parseInt(inputAge.getText().toString().trim()) >= 1 &&
                        Integer.parseInt(inputAge.getText().toString().trim()) <= 200){
                    layoutAge.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputEyeColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputEyeColor.getText()).toString().length() >= 3 &&
                        Objects.requireNonNull(inputEyeColor.getText()).toString().length() <= 50){
                    layoutEyeColor.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputHairColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputHairColor.getText()).toString().length() >= 3 &&
                        Objects.requireNonNull(inputHairColor.getText()).toString().length() <= 50){
                    layoutHairColor.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputUniqueSign.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputUniqueSign.getText()).toString().length() >= 3 &&
                        Objects.requireNonNull(inputUniqueSign.getText()).toString().length() <= 250){
                    layoutUniqueSign.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputImportantInfo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputImportantInfo.getText()).toString().length() >= 3 &&
                        Objects.requireNonNull(inputImportantInfo.getText()).toString().length() <= 250){
                    layoutImportantInfo.setErrorEnabled(false);
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
                        Objects.requireNonNull(inputLastSeen.getText()).toString().length() <= 60){
                    layoutLastSeen.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Email Validation
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

    private boolean validate() {
        if (autoCompleteReportType.getText().toString().isEmpty()){
            layoutReportType.setErrorEnabled(true);
            layoutReportType.setError("Report type is required");
            return false;
        }

        if (autoCompleteWeightUnit.getText().toString().isEmpty()){
            layoutWeightUnit.setErrorEnabled(true);
            layoutWeightUnit.setError("Weight unit is required");
            return false;
        }

        if (autoCompleteHeightUnit.getText().toString().isEmpty()){
            layoutHeightUnit.setErrorEnabled(true);
            layoutHeightUnit.setError("Height unit is required");
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

        if (Objects.requireNonNull(inputMissingName.getText()).toString().length() > 100){
            layoutMissingName.setErrorEnabled(true);
            layoutMissingName.setError("Required no more than  100 characters");
            return false;
        }

        /* Height Validation */
        if (inputHeight.getText().toString().isEmpty()){
            layoutHeight.setErrorEnabled(true);
            layoutHeight.setError("Height is required");
            return false;
        }

        if (Double.parseDouble(inputHeight.getText().toString().trim()) < 1){
            layoutHeight.setErrorEnabled(true);
            layoutHeight.setError("Required at last minimum 1 value");
            return false;
        }

        if (Double.parseDouble(inputHeight.getText().toString().trim()) > 1000.99){
            layoutHeight.setErrorEnabled(true);
            layoutHeight.setError("Required no more than 1000.99 value");
            return false;
        }

        /* Weight Validation */
        if (inputWeight.getText().toString().isEmpty()){
            layoutWeight.setErrorEnabled(true);
            layoutWeight.setError("Weight is required");
            return false;
        }

        if (Double.parseDouble(inputWeight.getText().toString().trim()) < 1){
            layoutWeight.setErrorEnabled(true);
            layoutWeight.setError("Required at last minimum 1 value");
            return false;
        }

        if (Double.parseDouble(inputWeight.getText().toString().trim()) > 1000.99){
            layoutWeight.setErrorEnabled(true);
            layoutWeight.setError("Required no more than 1000.99 value");
            return false;
        }


        /* Age Validation */
        if (inputAge.getText().toString().isEmpty()){
            layoutAge.setErrorEnabled(true);
            layoutAge.setError("Age is required");
            return false;
        }

        if (Integer.parseInt(inputAge.getText().toString().trim()) < 1){
            layoutAge.setErrorEnabled(true);
            layoutAge.setError("Required at last minimum 1 value");
            return false;
        }

        if (Double.parseDouble(inputWeight.getText().toString().trim()) > 200){
            layoutAge.setErrorEnabled(true);
            layoutAge.setError("Required no more than 200 value");
            return false;
        }

        /* Eye Color Validation */
        if (Objects.requireNonNull(inputEyeColor.getText()).toString().length() < 3){
            layoutEyeColor.setErrorEnabled(true);
            layoutEyeColor.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputEyeColor.getText()).toString().length() > 50){
            layoutEyeColor.setErrorEnabled(true);
            layoutEyeColor.setError("Required no more than 50 characters");
            return false;
        }

        /* Hair Color Validation */
        if (Objects.requireNonNull(inputHairColor.getText()).toString().length() < 3){
            layoutHairColor.setErrorEnabled(true);
            layoutHairColor.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputHairColor.getText()).toString().length() > 50){
            layoutHairColor.setErrorEnabled(true);
            layoutHairColor.setError("Required no more than 50 characters");
            return false;
        }

        /* Unique Sign Validation */
        if (Objects.requireNonNull(inputUniqueSign.getText()).toString().length() < 3){
            layoutUniqueSign.setErrorEnabled(true);
            layoutUniqueSign.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputUniqueSign.getText()).toString().length() > 250){
            layoutUniqueSign.setErrorEnabled(true);
            layoutUniqueSign.setError("Required no more than 250 characters");
            return false;
        }

        /* Important Information Validation */
        if (Objects.requireNonNull(inputImportantInfo.getText()).toString().length() < 3){
            layoutImportantInfo.setErrorEnabled(true);
            layoutImportantInfo.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputUniqueSign.getText()).toString().length() > 250){
            layoutImportantInfo.setErrorEnabled(true);
            layoutImportantInfo.setError("Required no more than 250 characters");
            return false;
        }

        /* Last Seen Validation */
        if (Objects.requireNonNull(inputLastSeen.getText()).toString().length() < 3){
            layoutLastSeen.setErrorEnabled(true);
            layoutLastSeen.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputLastSeen.getText()).toString().length() > 60){
            layoutLastSeen.setErrorEnabled(true);
            layoutLastSeen.setError("Required no more than 60 characters");
            return false;
        }

        /* Email Validation  */
        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(inputEmail.getText()).toString().trim()).matches() || Objects.requireNonNull(inputEmail.getText()).toString().trim().isEmpty()){
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

    private void updateData() {
        progressDialog.setMessage("Submitting data please wait.....");
        progressDialog.show();

        String missingName = Objects.requireNonNull(inputMissingName.getText()).toString().trim();
        String height = Objects.requireNonNull(inputHeight.getText()).toString().trim();
        String heightUnit = Objects.requireNonNull(autoCompleteHeightUnit.getText()).toString().trim();
        String weight = Objects.requireNonNull(inputWeight.getText()).toString().trim();
        String weightUnit = Objects.requireNonNull(autoCompleteWeightUnit.getText()).toString().trim();
        String age = Objects.requireNonNull(inputAge.getText()).toString().trim();
        String eyes = Objects.requireNonNull(inputEyeColor.getText()).toString().trim();
        String hair = Objects.requireNonNull(inputHairColor.getText()).toString().trim();
        String uniqueSign = Objects.requireNonNull(inputUniqueSign.getText()).toString().trim();
        String importantInfo = Objects.requireNonNull(inputImportantInfo.getText()).toString().trim();
        String lastSeen = Objects.requireNonNull(inputLastSeen.getText()).toString().trim();
        String email = Objects.requireNonNull(inputEmail.getText()).toString().trim();
        String phoneNo = Objects.requireNonNull(inputPhone.getText()).toString().trim();
        String reportType = autoCompleteReportType.getText().toString().trim();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.MISSING_PERSONS + "/" + mMissingPerson.getId(), response->{
            try {
                JSONObject object = new JSONObject(response);

                JSONObject missingPersonJSONObject = object.getJSONObject("data");

                MissingPerson missingPersonObj = new MissingPerson(
                        missingPersonJSONObject.getInt("id"), missingPersonJSONObject.getInt("contact_id"), missingPersonJSONObject.getString("contact_name"), missingPersonJSONObject.getString("user_picture_name"),
                        missingPersonJSONObject.getString("user_file_path"), missingPersonJSONObject.getString("report_type"), missingPersonJSONObject.getString("name"), missingPersonJSONObject.getDouble("height"),
                        missingPersonJSONObject.getString("height_unit"), missingPersonJSONObject.getDouble("weight"), missingPersonJSONObject.getString("weight_unit"), missingPersonJSONObject.getInt("age"),
                        missingPersonJSONObject.getString("eyes"), missingPersonJSONObject.getString("hair"), missingPersonJSONObject.getString("unique_sign"), missingPersonJSONObject.getString("important_information"),
                        missingPersonJSONObject.getString("last_seen"), missingPersonJSONObject.getString("email"), missingPersonJSONObject.getString("phone_no"), missingPersonJSONObject.getString("picture_name"),
                        missingPersonJSONObject.getString("file_path"), missingPersonJSONObject.getString("credential_name"), missingPersonJSONObject.getString("credential_path"),
                        missingPersonJSONObject.getInt("comments_count"), missingPersonJSONObject.getString("status"), missingPersonJSONObject.getString("admin_message"),  missingPersonJSONObject.getString("created_at"),
                        missingPersonJSONObject.getString("updated_at"));

                try {
                    AuthMissingPersonFragment.arrayList.set(selectedPosition, missingPersonObj);
                    AuthMissingPersonFragment.recyclerView.getAdapter().notifyItemChanged(selectedPosition);
                    AuthMissingPersonFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toasty.success(this, "The report has been updated successfully, Please wait for the administrator to verify the report", Toast.LENGTH_LONG, true).show();

                } catch (Exception exception) {}

                try {
                    MissingPersonFragment.arrayList.set(selectedPosition, missingPersonObj);
                    MissingPersonFragment.recyclerView.getAdapter().notifyItemChanged(selectedPosition);
                    MissingPersonFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toasty.success(this, "The report has been updated successfully, Please wait for the administrator to verify the report", Toast.LENGTH_LONG, true).show();
                } catch (Exception exception) {}

                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        },error ->{
            progressDialog.dismiss();
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

                map.put("name", missingName);
                map.put("height", height);
                map.put("height_unit", heightUnit);
                map.put("weight", weight);
                map.put("weight_unit", weightUnit);
                map.put("age", age);
                map.put("eyes", eyes);
                map.put("hair", hair);
                map.put("unique_sign", uniqueSign);
                map.put("important_information", importantInfo);
                map.put("last_seen",lastSeen);
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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(MissingPersonEditActivity.this);
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