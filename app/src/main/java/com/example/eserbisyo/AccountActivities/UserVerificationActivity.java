package com.example.eserbisyo.AccountActivities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
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

import es.dmoral.toasty.Toasty;

public class UserVerificationActivity extends AppCompatActivity {

    private TextView txtCurrentRequest, txtCredentialGuide, txtSelectPhoto, txtSubmittedCredential;
    private Button btnSubmit, btnResubmit;
    private ImageView imgCredential;

    private TextInputLayout layoutStatus, layoutAdminMessage;

    private Bitmap bitmap = null;

    private JSONObject userVerification;
    private JSONObject errorObj = null;

    private ProgressDialog loadingDialog;
    private SharedPreferences userPref;

    private boolean isEmpty = true;

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
                        imgCredential.setImageURI(imgUri);

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
        setContentView(R.layout.activity_user_verification);

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            try {
                userVerification= new JSONObject(extras.getString(Extra.USER_VERIFICATION_OBJECT));
                isEmpty = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        init();
    }

    private void init() {
        txtCurrentRequest = findViewById(R.id.txtCurrentRequest);
        txtCredentialGuide = findViewById(R.id.txtCredentialGuide);
        txtSubmittedCredential = findViewById(R.id.txtSubmittedCredential);
        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);

        btnSubmit = findViewById(R.id.btnSubmit);
        btnResubmit = findViewById(R.id.btnResubmit);

        imgCredential = findViewById(R.id.imgCredential);

        layoutStatus = findViewById(R.id.txtLayoutStatus);
        layoutAdminMessage = findViewById(R.id.txtLayoutAdminMessage);

        TextInputEditText inputTxtStatus = findViewById(R.id.inputTxtStatus);
        TextInputEditText inputTxtAdminMessage = findViewById(R.id.inputTxtAdminMessage);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setCancelable(false);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        if (isEmpty) {
            txtCurrentRequest.setText(R.string.new_request_message);
            txtSelectPhoto.setVisibility(View.VISIBLE);
            txtCredentialGuide.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
            layoutStatus.setVisibility(View.GONE);
            layoutAdminMessage.setVisibility(View.GONE);
        } else {
            try {
                txtSubmittedCredential.setVisibility(View.VISIBLE);
                Picasso.get().load(Api.STORAGE + userVerification.getString("credential_file_path")).fit().error(R.drawable.cupang).into(imgCredential);
                if (userVerification.getString("status").equals("Pending")) {
                    txtCurrentRequest.setText(R.string.pending_request_message);
                    layoutStatus.setVisibility(View.GONE);
                    layoutAdminMessage.setVisibility(View.GONE);
                } else if (userVerification.getString("status").equals("Denied")) {
                    txtCurrentRequest.setText(R.string.denied_request_message);
                    btnResubmit.setVisibility(View.VISIBLE);
                    inputTxtStatus.setText(userVerification.getString("status"));
                    inputTxtAdminMessage.setText(userVerification.getString("admin_message"));
                } else {
                    txtCurrentRequest.setText(R.string.approved_request_message);
                    inputTxtStatus.setText(userVerification.getString("status"));
                    inputTxtAdminMessage.setText(userVerification.getString("admin_message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        txtCurrentRequest.setTextSize(15);

        initListeners();
    }

    private void initListeners() {
        //pick photo from gallery
        txtSelectPhoto.setOnClickListener(v->{
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            getImageResultLauncher.launch(i);
        });

        btnResubmit.setOnClickListener(v->{
            txtCurrentRequest.setText(R.string.new_request_message);
            txtSelectPhoto.setVisibility(View.VISIBLE);
            txtCredentialGuide.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);

            layoutStatus.setVisibility(View.GONE);
            layoutAdminMessage.setVisibility(View.GONE);
            btnResubmit.setVisibility(View.GONE);
            txtSubmittedCredential.setVisibility(View.GONE);

            Picasso.get().load(R.drawable.cupang).fit().error(R.drawable.cupang).into(imgCredential);
        });

        btnSubmit.setOnClickListener(v->{
            if(bitmap!=null) {
                submitVerificationRequest();
            } else {
                Toasty.error(this, "Please pick a credential from your gallery", Toast.LENGTH_LONG, true).show();
            }
        });
    }

    private void submitVerificationRequest() {
        loadingDialog.setMessage("Saving");
        loadingDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Api.SUBMIT_VERIFICATION_REQUEST, response->{

            Toasty.success(this, "Verification request has been submitted. Please wait for the administrator to respond to your request", Toast.LENGTH_LONG, true).show();
            btnSubmit.setVisibility(View.GONE);
            loadingDialog.dismiss();

        },error ->{
            loadingDialog.dismiss();
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

        RequestQueue queue = Volley.newRequestQueue(UserVerificationActivity.this);
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