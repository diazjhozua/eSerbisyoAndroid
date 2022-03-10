package com.example.eserbisyo.AccountActivities;

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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
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

public class ProfileActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    private TextInputLayout layoutFirstName;
    private TextInputLayout layoutLastName;
    private TextInputLayout layoutPurok;
    private TextInputLayout layoutAddress;
    private TextInputEditText inputTxtFirstName, inputTxtMiddleName, inputTxtLastName, inputTxtAddress;
    private TextView txtSelectPhoto ,txtCapturePhoto;
    private AutoCompleteTextView autoCompleteTxtPurok;
    private Button btnSave, btnEdit;
    private CircleImageView circleImageView;
    private Bitmap bitmap = null;
    private SharedPreferences userPref;
    private ProgressDialog loadingDialog;
    private int currentPurok = 0;


    private final String[] puroks = new String[]{
            "Purok 1", "Purok 2", "Purok 3", "Purok 4", "Purok 5"
    };

    private boolean isEditing = false;

    private JSONObject errorObj = null;

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
                        circleImageView.setImageURI(imgUri);

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
                        circleImageView.setImageBitmap(bitmap);

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        init();
    }

    private void init() {
        layoutFirstName = findViewById(R.id.txtLayoutFirstName);
        layoutLastName = findViewById(R.id.txtLayoutLastName);
        layoutPurok = findViewById(R.id.txtLayoutPurok);
        layoutAddress = findViewById(R.id.txtLayoutAddress);

        inputTxtFirstName = findViewById(R.id.inputTxtFirstName);
        inputTxtMiddleName = findViewById(R.id.inputTxtMiddleName);
        inputTxtLastName = findViewById(R.id.inputTxtLastName);
        autoCompleteTxtPurok = findViewById(R.id.autoCompleteTxtPurok);
        inputTxtAddress = findViewById(R.id.inputTxtAddress);

        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);
        txtCapturePhoto  = findViewById(R.id.txtCapturePhoto);

        circleImageView = findViewById(R.id.imgUserInfo);

        btnSave = findViewById(R.id.btnSave);
        btnEdit = findViewById(R.id.btnEdit);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                ProfileActivity.this,
                R.layout.list_item,
                puroks
        );

        autoCompleteTxtPurok.setAdapter(adapterType);

        inputTxtFirstName.setFocusable(false);
        inputTxtLastName.setFocusable(false);
        autoCompleteTxtPurok.setFocusable(false);
        inputTxtAddress.setFocusable(false);

        initListeners();

        loadUserInfo();
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

        btnSave.setOnClickListener(v->{
            // validate fields
            if(validate()){
                updateProfile();
            }
        });


        btnEdit.setOnClickListener(v->{
            isEditing = !isEditing;
            changeInputAccess(isEditing);
        });
        inputTxtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputTxtFirstName.getText()).toString().isEmpty()){
                    layoutFirstName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputTxtLastName.getText()).toString().isEmpty()){
                    layoutLastName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        autoCompleteTxtPurok.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(autoCompleteTxtPurok.getText()).toString().isEmpty()){
                    layoutPurok.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputTxtAddress.getText()).toString().isEmpty()){
                    layoutAddress.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void changeInputAccess(Boolean isEditing) {
        if (isEditing) {
            txtSelectPhoto.setVisibility(View.VISIBLE);
            txtCapturePhoto.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.VISIBLE);

            btnEdit.setBackgroundColor(getResources().getColor(R.color.firebrick));
            btnEdit.setTextColor(getResources().getColor(R.color.white));
            btnEdit.setText(R.string.cancel);

            inputTxtFirstName.setFocusableInTouchMode(true);
            inputTxtLastName.setFocusableInTouchMode(true);
            autoCompleteTxtPurok.setFocusableInTouchMode(true);
            inputTxtAddress.setFocusableInTouchMode(true);

        } else {
            txtSelectPhoto.setVisibility(View.GONE);
            txtCapturePhoto.setVisibility(View.GONE);
            btnSave.setVisibility(View.GONE);

            btnEdit.setBackgroundColor(getResources().getColor(R.color.white));
            btnEdit.setTextColor(getResources().getColor(R.color.black));
            btnEdit.setText(R.string.edit);

            inputTxtFirstName.setFocusable(false);
            inputTxtLastName.setFocusable(false);
            autoCompleteTxtPurok.setFocusable(false);
            inputTxtAddress.setFocusable(false);
        }
    }

    private void loadUserInfo() {
        loadingDialog.setMessage("Getting updated profile");
        loadingDialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, Api.MY_PROFILE, response->{

            try {
                JSONObject object = new JSONObject(response);
                JSONObject user = object.getJSONObject("data");

                SharedPreferences.Editor editor = userPref.edit();
                editor.putInt(Pref.ID, user.getInt("id"));
                editor.putInt(Pref.USER_ROLE_ID, user.getInt("user_role_id"));
                editor.putString(Pref.FIRST_NAME, user.getString("first_name"));
                editor.putString(Pref.MIDDLE_NAME, user.getString("middle_name"));
                editor.putString(Pref.LAST_NAME, user.getString("last_name"));
                editor.putString(Pref.PICTURE, user.getString("file_path"));
                editor.putString(Pref.STATUS, user.getString("status"));
                editor.putBoolean(Pref.IS_VERIFIED, user.getBoolean("is_verified"));
                currentPurok = user.getInt("purok_id");

                inputTxtFirstName.setText(user.getString("first_name"));
                inputTxtMiddleName.setText(user.getString("middle_name"));
                inputTxtLastName.setText(user.getString("last_name"));
                inputTxtAddress.setText(user.getString("address"));
                layoutPurok.setHint("Current: " + puroks[user.getInt("purok_id") - 1]);

                Picasso.get().load(user.getString("file_path")).fit().error(R.drawable.cupang).into(circleImageView);

                editor.apply();

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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
        queue.add(request);
    }

    private boolean validate() {

        if (!autoCompleteTxtPurok.getText().toString().trim().isEmpty()) {
            String purok = autoCompleteTxtPurok.getText().toString().trim();
            currentPurok = Integer.parseInt(purok.substring(purok.length()-1));
        }

        if (Objects.requireNonNull(inputTxtFirstName.getText()).toString().isEmpty()){
            layoutFirstName.setErrorEnabled(true);
            layoutFirstName.setError("First name is Required");
            return false;
        }

        if (Objects.requireNonNull(inputTxtLastName.getText()).toString().isEmpty()){
            layoutLastName.setErrorEnabled(true);
            layoutLastName.setError("Last name is required");
            return false;
        }

        if (currentPurok == 0){
            layoutPurok.setErrorEnabled(true);
            layoutPurok.setError("Purok is required");
            return false;
        }

        if (Objects.requireNonNull(inputTxtAddress.getText()).toString().isEmpty()){
            layoutAddress.setErrorEnabled(true);
            layoutAddress.setError("Address is required");
            return false;
        }

        return true;
    }
    private void updateProfile() {
        loadingDialog.setMessage("Saving");
        loadingDialog.show();

        String firstName = Objects.requireNonNull(inputTxtFirstName.getText()).toString().trim();
        String middleName = Objects.requireNonNull(inputTxtMiddleName.getText()).toString().trim();
        String lastName = Objects.requireNonNull(inputTxtLastName.getText()).toString().trim();
        String purokID = String.valueOf(currentPurok);
        String address = Objects.requireNonNull(inputTxtAddress.getText()).toString().trim();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.SAVE_USER_INFO, response->{

            try {
                JSONObject object = new JSONObject(response);
                JSONObject user = object.getJSONObject("data");

                SharedPreferences.Editor editor = userPref.edit();
                editor.putInt(Pref.ID, user.getInt("id"));
                editor.putInt(Pref.USER_ROLE_ID, user.getInt("user_role_id"));
                editor.putString(Pref.FIRST_NAME, user.getString("first_name"));
                editor.putString(Pref.MIDDLE_NAME, user.getString("middle_name"));
                editor.putString(Pref.LAST_NAME, user.getString("last_name"));
                editor.putString(Pref.PICTURE, user.getString("file_path"));
                editor.putString(Pref.STATUS, user.getString("status"));
                editor.apply();

                inputTxtFirstName.setText(user.getString("first_name"));
                inputTxtMiddleName.setText(user.getString("middle_name"));
                inputTxtLastName.setText(user.getString("last_name"));
                inputTxtAddress.setText(user.getString("address"));
                layoutPurok.setHint("Current: " + puroks[user.getInt("purok_id") - 1]);

                Picasso.get().load(user.getString("file_path")).fit().error(R.drawable.cupang).into(circleImageView);

                txtSelectPhoto.setVisibility(View.GONE);
                btnSave.setVisibility(View.GONE);

                btnEdit.setBackgroundColor(getResources().getColor(R.color.white));
                btnEdit.setTextColor(getResources().getColor(R.color.black));
                btnEdit.setText(R.string.edit);

                inputTxtFirstName.setFocusable(false);
                inputTxtLastName.setFocusable(false);
                autoCompleteTxtPurok.setFocusable(false);
                inputTxtAddress.setFocusable(false);


                Toasty.success(this, "Information is updated successfully", Toast.LENGTH_SHORT, true).show();

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
                map.put("first_name",firstName);
                map.put("middle_name",middleName);
                map.put("last_name",lastName);
                map.put("purok_id", purokID);
                map.put("address",address);
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

        RequestQueue queue = Volley.newRequestQueue(ProfileActivity.this);
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