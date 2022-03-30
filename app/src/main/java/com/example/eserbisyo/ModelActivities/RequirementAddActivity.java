package com.example.eserbisyo.ModelActivities;

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
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.example.eserbisyo.Adapters.DDTypeAdapter;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.RequirementFragment;
import com.example.eserbisyo.Models.Requirement;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.Models.UserRequirement;
import com.example.eserbisyo.R;

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

public class RequirementAddActivity extends AppCompatActivity {
    public static final int CAMERA_PERM_CODE = 101;
    private TextView txtSelectPhoto, txtCapturePhoto;
    private ImageView ivRequirementPicture;
    private Button btnSubmit;
    private Spinner spnType;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;
    private int selectedTypeId;
    private ArrayList<Type> typeArrayList;
    private JSONObject errorObj = null;

    private Bitmap bitmap = null;


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

                        ivRequirementPicture.setImageURI(imgUri);

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
                        ivRequirementPicture.setImageBitmap(bitmap);

                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requirement_add);
        init();
    }

    private void init() {
        txtSelectPhoto = findViewById(R.id.txtSelectPhoto);
        txtCapturePhoto = findViewById(R.id.txtCapturePhoto);
        ivRequirementPicture = findViewById(R.id.ivRequirementPicture);

        btnSubmit = findViewById(R.id.btnSubmit);

        spnType = findViewById(R.id.spinnerType);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

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

        // Listener for btnSubmit
        btnSubmit.setOnClickListener(v -> {
            // store feedback
            if (validate()) {
                submitRequirement();
            }
        });

    }

    private void populateSpinner() {
        progressDialog.setMessage("Loading assets.....");
        progressDialog.show();
        typeArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Api.USER_REQUIREMENTS_CREATE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONArray typeArray = new JSONArray(object.getString("data"));
                if(typeArray.length() > 0){
                    for (int i = 0; i < typeArray.length(); i++) {
                        JSONObject typeObject = typeArray.getJSONObject(i);
                        Type type = new Type(
                                typeObject.getInt("id"),
                                typeObject.getString("name")
                        );
                        typeArrayList.add(type);

                        setAdapters();
                    }
                } else {
                    Toasty.info(this, "You have passed all the available requirements in the system. Please delete the existing requirement if you want to add new requirement", Toast.LENGTH_LONG, true).show();
                    btnSubmit.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        },error -> {
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

        RequestQueue queue = Volley.newRequestQueue(RequirementAddActivity.this);
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


    private boolean validate() {
        if (bitmap == null) {
            Toasty.error(this, "Please attached image correspond to the requirement", Toast.LENGTH_LONG, true).show();
            return false;
        }
        return true;
    }

    private void submitRequirement() {
        progressDialog.setMessage("Submitting please wait.....");
        progressDialog.show();

        int requirementID = selectedTypeId;
        StringRequest request = new StringRequest(Request.Method.POST, Api.USER_REQUIREMENTS, response->{

            try {
                JSONObject object = new JSONObject(response);

                JSONObject userRequirementObject = object.getJSONObject("data");
                JSONObject requirementObject = userRequirementObject.getJSONObject("requirement");
                Log.d("requirement", userRequirementObject.toString(4));

                UserRequirement userRequirement = new UserRequirement(
                        userRequirementObject.getInt("id"), userRequirementObject.getInt("user_id"),  userRequirementObject.getInt("requirement_id"),
                        userRequirementObject.getString("file_name"), userRequirementObject.getString("file_path"), userRequirementObject.getString("created_at"),
                        new Requirement(requirementObject.getInt("id"), requirementObject.getString("name")));

                RequirementFragment.arrayList.add(0,userRequirement);
                Objects.requireNonNull(RequirementFragment.recyclerView.getAdapter()).notifyItemInserted(0);
                RequirementFragment.recyclerView.getAdapter().notifyDataSetChanged();
                Toasty.success(this, "Your requirement has been submitted successfully", Toast.LENGTH_LONG, true).show();
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
                map.put("requirement_id", String.valueOf(requirementID));
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

        RequestQueue queue = Volley.newRequestQueue(RequirementAddActivity.this);
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