package com.example.eserbisyo.Biker;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import android.widget.CompoundButton;
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
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.HomeFragments.AuthMissingPersonFragment;
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.ModelActivities.MissingPersonAddActivity;
import com.example.eserbisyo.Models.MissingPerson;
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

import es.dmoral.toasty.Toasty;

public class BikerRegisterActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    private TextInputLayout layoutType, layoutSize, layoutColor, layoutReason, layoutPhone;
    private TextInputEditText inputType, inputSize, inputColor, inputReason, inputPhone;
    private TextView txtSelectPhoto, txtCapturePhoto, txtExamplePicture, txtTerms;
    private AppCompatCheckBox chkTerms;
    private ImageView ivCredential;
    private Button btnSubmit;

    private Bitmap bitmap = null;
    private JSONObject errorObj = null;
    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private Bitmap scaleBitmap(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();

        Log.v("Pictures", "Width and height are " + width + "--" + height);

        if (width > height) {
            // landscape
            float ratio = (float) width / 1250;
            width = 1250;
            height = (int)(height / ratio);
        } else if (height > width) {
            // portrait
            float ratio = (float) height / 1250;
            height = 1250;
            width = (int)(width / ratio);
        } else {
            // square
            height = 1250;
            width = 1250;
        }

        Log.v("Pictures", "after scaling Width and height are " + width + "--" + height);

        bm = Bitmap.createScaledBitmap(bm, width, height, true);
        return bm;
    }

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
                        ivCredential.setImageURI(imgUri);
                        txtExamplePicture.setVisibility(View.GONE);
                        try {
                            bitmap = scaleBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(),imgUri));
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
                        ivCredential.setImageBitmap(bitmap);

                        txtExamplePicture.setVisibility(View.GONE);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biker_register);
        init();
    }

    private void init() {
        layoutType = findViewById(R.id.txtLayoutBikeType);
        layoutSize = findViewById(R.id.txtLayoutBikeSize);
        layoutColor = findViewById(R.id.txtLayoutBikeColor);
        layoutReason = findViewById(R.id.txtLayoutReason);
        layoutPhone = findViewById(R.id.txtLayoutPhone);

        inputType = findViewById(R.id.inputTxtBikeType);
        inputSize = findViewById(R.id.inputTxtBikeSize);
        inputColor = findViewById(R.id.inputTxtBikeColor);
        inputReason = findViewById(R.id.inputTxtReason);
        inputPhone = findViewById(R.id.inputTxtPhone);

        txtExamplePicture = findViewById(R.id.txtExamplePicture);
        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);
        txtCapturePhoto = findViewById(R.id.txtCapturePhoto);
        txtTerms = findViewById(R.id.txtTerms);

        chkTerms = findViewById(R.id.chkTerms);

        ivCredential = findViewById(R.id.imgCredential);

        btnSubmit = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        initListeners();
    }

    private void initListeners() {
        //pick photo from gallery
        txtSelectPhoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            getImageResultLauncher.launch(i);
        });

        txtCapturePhoto.setOnClickListener(v->{
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
            }else {
                Intent camera_intent
                        = new Intent(MediaStore
                        .ACTION_IMAGE_CAPTURE);

                getCaptureResultLauncher.launch(camera_intent);
            }
        });

        chkTerms.setOnClickListener(view -> {
            if(((CompoundButton) view).isChecked()){
                txtTerms.setTextColor(getResources().getColor(R.color.white));
            } else {
                txtTerms.setTextColor(getResources().getColor(R.color.firebrick));
            }
        });

        txtTerms.setOnClickListener(view -> openTermsDialog());

        btnSubmit.setOnClickListener(v->{
            if (validate()) {
                submitData();
            }
        });

        inputType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputType.getText()).toString().length() >= 4 &&
                        Objects.requireNonNull(inputType.getText()).toString().length() <= 30){
                    layoutType.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputSize.getText()).toString().length() >= 4 &&
                        Objects.requireNonNull(inputSize.getText()).toString().length() <= 30){
                    layoutSize.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputColor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputColor.getText()).toString().length() >= 4 &&
                        Objects.requireNonNull(inputColor.getText()).toString().length() <= 30){
                    layoutColor.setErrorEnabled(false);
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



        inputReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputReason.getText()).toString().length() >= 4 &&
                        Objects.requireNonNull(inputReason.getText()).toString().length() <= 250){
                    layoutReason.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    private void openTermsDialog() {
        Dialog dialog = new Dialog(BikerRegisterActivity.this);
        dialog.setContentView(R.layout.dialog_terms);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        Button btnExit = dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private boolean validate() {
        if (bitmap == null) {
            Toasty.error(this, "Please attached image correspond to the requirement", Toast.LENGTH_LONG, true).show();
            return false;
        }

        /* Bike type validation */
        if (Objects.requireNonNull(inputType.getText()).toString().length() < 3){
            layoutType.setErrorEnabled(true);
            layoutType.setError("Required at least 4 characters");
            Toasty.error(this, "Bike Type: Required at least 4 characters", Toast.LENGTH_LONG, true).show();
            return false;
        }

        if (Objects.requireNonNull(inputType.getText()).toString().length() > 30){
            layoutType.setErrorEnabled(true);
            layoutType.setError("Bike Type:  Required no more than 30 characters");
            Toasty.error(this, "Bike Type:  Required no more than 30 characters", Toast.LENGTH_LONG, true).show();
            return false;
        }

        /* Bike color validation */
        if (Objects.requireNonNull(inputColor.getText()).toString().length() < 3){
            layoutColor.setErrorEnabled(true);
            layoutColor.setError("Required at least 4 characters");
            Toasty.error(this, "Bike Color: Required at least 4 characters", Toast.LENGTH_LONG, true).show();
            return false;
        }

        if (Objects.requireNonNull(inputColor.getText()).toString().length() > 30){
            layoutColor.setErrorEnabled(true);
            layoutColor.setError("Bike Color:  Required no more than 30 characters");
            Toasty.error(this, "Bike Color:  Required no more than 30 characters", Toast.LENGTH_LONG, true).show();
            return false;
        }

        /* Bike size validation */
        if (Objects.requireNonNull(inputSize.getText()).toString().length() < 3){
            layoutSize.setErrorEnabled(true);
            layoutSize.setError("Required at least 4 characters");
            Toasty.error(this, "Bike Size: Required at least 4 characters", Toast.LENGTH_LONG, true).show();
            return false;
        }

        if (Objects.requireNonNull(inputColor.getText()).toString().length() > 30){
            layoutSize.setErrorEnabled(true);
            layoutSize.setError("Bike Size:  Required no more than 30 characters");
            Toasty.error(this, "Bike Size:  Required no more than 30 characters", Toast.LENGTH_LONG, true).show();
            return false;
        }

        /* Reason validation */
        if (Objects.requireNonNull(inputReason.getText()).toString().length() < 3){
            layoutReason.setErrorEnabled(true);
            layoutReason.setError("Required at least 4 characters");
            Toasty.error(this, "Bike Reason: Required at least 4 characters", Toast.LENGTH_LONG, true).show();
            return false;
        }

        /* Phone Validation */
        if (Objects.requireNonNull(inputPhone.getText()).toString().length() != 11){
            layoutPhone.setErrorEnabled(true);
            layoutPhone.setError("Invalid phone number. Must start at 0919*******");
            Toasty.error(this, "Invalid phone number. Must start at 0919*******", Toast.LENGTH_LONG, true).show();
            return false;
        }


        if (Objects.requireNonNull(inputReason.getText()).toString().length() > 250){
            layoutReason.setErrorEnabled(true);
            layoutReason.setError("Bike Reason:  Required no more than 250 characters");
            Toasty.error(this, "Bike Reason:  Required no more than 250 character", Toast.LENGTH_LONG, true).show();
            return false;
        }

        if (!chkTerms.isChecked()){
            Toasty.error(this, "You must agree the following terms and condition to proceed this request", Toast.LENGTH_LONG, true).show();
            return false;
        }

        return true;
    }


    private void submitData() {
        progressDialog.setMessage("Submitting data please wait.....");
        progressDialog.show();

        String type = Objects.requireNonNull(inputType.getText()).toString().trim();
        String size = Objects.requireNonNull(inputSize.getText()).toString().trim();
        String color = Objects.requireNonNull(inputColor.getText()).toString().trim();
        String reason = Objects.requireNonNull(inputReason.getText()).toString().trim();
        String phoneNo = Objects.requireNonNull(inputPhone.getText()).toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, Api.BIKERS_POST_VERIFICATION, response->{
            try {
                JSONObject object = new JSONObject(response);

                JSONObject jsonObject = object.getJSONObject("data");

                Intent intent = new Intent(BikerRegisterActivity.this, BikerViewRegistrationActivity.class);
                intent.putExtra(Extra.JSON_OBJECT, jsonObject.toString());
                startActivity(intent);

                Toasty.success(this, "Registration submitted, please wait for the administrator to respond to your request", Toast.LENGTH_LONG, true).show();

                progressDialog.dismiss();


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

                map.put("bike_type", type);
                map.put("bike_size", size);
                map.put("bike_color", color);
                map.put("reason", reason);
                map.put("phone_no", phoneNo);
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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(BikerRegisterActivity.this);
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