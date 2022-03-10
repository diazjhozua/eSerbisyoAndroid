package com.example.eserbisyo;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences userPref;
    private ProgressDialog progressDialog;
    private String userEmail, userPassword;

    public JSONObject errorObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        ImageView imageViewLogo = findViewById(R.id.imageViewLogo);
        TextView txtAppName = findViewById(R.id.txtAppName);
        TextView txtAppDesc = findViewById(R.id.txtAppDesc);

        imageViewLogo.setAnimation(topAnim);
        txtAppName.setAnimation(bottomAnim);
        txtAppDesc.setAnimation(bottomAnim);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        //this code will pause the app for 1.5 secs and then any thing in run method will run.
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
            userEmail = userPref.getString(Pref.EMAIL, "");
            userPassword = userPref.getString(Pref.PASSWORD, "");

            boolean isRemember = userPref.getBoolean(Pref.IS_REMEMBER,false);
            boolean isFirstTime = userPref.getBoolean(Pref.IS_FIRST_TIME,true);

            if (isRemember){
                loginPrefCredentials();
            } else if (isFirstTime) {
                isFirstTime();
            } else {
                startActivity(new Intent(MainActivity.this,AuthActivity.class));
                finish();
            }
        },1500);

    }

    private void loginPrefCredentials() {
        progressDialog.setMessage("Logging in");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Api.LOGIN, response -> {
            //we get response if connection success
            try {
                JSONObject object = new JSONObject(response);

                //make shared preference user
                SharedPreferences userPref = getApplicationContext().getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userPref.edit();

                JSONObject user = object.getJSONObject("user");
                String message = object.getString("message");

                if (user.isNull("first_name")) {
                    editor.putString(Pref.TOKEN, object.getString("access_token"));
                    editor.apply();

                    startActivity(new Intent(MainActivity.this,UserInfoActivity.class));
                } else {

                    editor.putInt(Pref.ID, user.getInt("id"));
                    editor.putInt(Pref.USER_ROLE_ID, user.getInt("user_role_id"));
                    editor.putString(Pref.FIRST_NAME, user.getString("first_name"));
                    editor.putString(Pref.MIDDLE_NAME, user.getString("middle_name"));
                    editor.putString(Pref.LAST_NAME, user.getString("last_name"));
                    editor.putString(Pref.PICTURE, user.getString("file_path"));
                    editor.putString(Pref.TOKEN, object.getString("access_token"));
                    editor.putString(Pref.STATUS, user.getString("status"));

                    try {
                        editor.putBoolean(Pref.IS_VERIFIED, user.getBoolean("is_verified"));
                    } catch (Exception e) {
                        editor.putBoolean(Pref.IS_VERIFIED, user.getInt("is_verified") == 1);
                    }

                    editor.apply();

                    startActivity(new Intent(MainActivity.this,HomeActivity.class));
                }
                Toasty.success(this, message, Toast.LENGTH_SHORT, true).show();
                finish();

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

            startActivity(new Intent(MainActivity.this,AuthActivity.class));
            finish();
        }){
            // add parameters
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("email", userEmail);
                map.put("password", userPassword);
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

        //add this request to request queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void isFirstTime() {
        //for checking if the app is running for the very first time
        //we need to save a value to shared preferences

        startActivity(new Intent(MainActivity.this,OnBoardActivity.class));

        userPref = getApplication().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        boolean isFirstTime = userPref.getBoolean(Pref.IS_FIRST_TIME,true);

        //default value true
        if (isFirstTime){
            // if its true then its first time and we will change it false
            SharedPreferences.Editor editor = userPref.edit();
            editor.putBoolean(Pref.IS_FIRST_TIME,false);
            editor.apply();

            // start Onboard activity
            startActivity(new Intent(MainActivity.this,OnBoardActivity.class));
            finish();
        }
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
}