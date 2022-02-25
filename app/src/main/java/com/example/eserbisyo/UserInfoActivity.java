package com.example.eserbisyo;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
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

public class UserInfoActivity extends AppCompatActivity {
    private TextInputLayout layoutFirstName;
    private TextInputLayout layoutLastName;
    private TextInputLayout layoutPurok;
    private TextInputLayout layoutAddress;
    private TextInputEditText inputTxtFirstName, inputTxtMiddleName, inputTxtLastName, inputTxtAddress;
    private TextView txtSelectPhoto;
    private AutoCompleteTextView autoCompleteTxtPurok;
    private Button btnContinue;
    private CircleImageView circleImageView;
    private Bitmap bitmap = null;
    private SharedPreferences userPref;
    private ProgressDialog dialog;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        init();
    }

    private void init() {
        layoutFirstName = findViewById(R.id.txtLayoutFirstNameUserInfo);
        layoutLastName = findViewById(R.id.txtLayoutLastNameUserInfo);
        layoutPurok = findViewById(R.id.txtLayoutLPurokUserInfo);
        layoutAddress = findViewById(R.id.txtLayoutAddressUserInfo);

        inputTxtFirstName = findViewById(R.id.inputTxtFirstNameUserInfo);
        inputTxtMiddleName = findViewById(R.id.inputTxtMiddleNameUserInfo);
        inputTxtLastName = findViewById(R.id.inputTxtLastNameUserInfo);
        autoCompleteTxtPurok = findViewById(R.id.autoCompleteTxtPurok);
        inputTxtAddress = findViewById(R.id.inputTxtAddressUserInfo);

        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);

        circleImageView = findViewById(R.id.imgUserInfo);

        btnContinue = findViewById(R.id.btnContinue);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        String[] puroks = new String[]{
                "Purok 1", "Purok 2", "Purok 3", "Purok 4", "Purok 5"
        };

        ArrayAdapter<String> adapterType = new ArrayAdapter<>(
                UserInfoActivity.this,
                R.layout.list_item,
                puroks
        );

        autoCompleteTxtPurok.setAdapter(adapterType);

        initListeners();
    }

    private void initListeners() {
        //pick photo from gallery
        txtSelectPhoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            getImageResultLauncher.launch(i);
        });

        btnContinue.setOnClickListener(v->{
            // validate fields
            if(validate()){
                saveUserInfo();
            }
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

    private boolean validate() {

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

        if (autoCompleteTxtPurok.getText().toString().isEmpty()){
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

    private void saveUserInfo() {
        dialog.setMessage("Saving");
        dialog.show();

        String firstName = Objects.requireNonNull(inputTxtFirstName.getText()).toString().trim();
        String middleName = Objects.requireNonNull(inputTxtMiddleName.getText()).toString().trim();
        String lastName = Objects.requireNonNull(inputTxtLastName.getText()).toString().trim();
        String purok = autoCompleteTxtPurok.getText().toString().trim();
        String purokID = purok.substring(purok.length()-1);
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
                editor.putString(Pref.ADDRESS, user.getString("address"));
                editor.putString(Pref.PICTURE, user.getString("file_path"));
                editor.putString(Pref.STATUS, user.getString("status"));
                editor.putInt(Pref.IS_VERIFIED, user.getInt("is_verified"));
                editor.apply();

                Toasty.success(this, "Information is updated successfully", Toast.LENGTH_SHORT, true).show();

                startActivity(new Intent(UserInfoActivity.this,HomeActivity.class));
                finish();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            dialog.dismiss();

        },error ->{
            dialog.dismiss();
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

        RequestQueue queue = Volley.newRequestQueue(UserInfoActivity.this);
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
}