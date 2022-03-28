package com.example.eserbisyo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Biker.BikerRegisterActivity;
import com.example.eserbisyo.Biker.OnBoardBikerActivity;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.ModelRecyclerViewAdapters.DocumentsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.NotificationsAdapter;
import com.example.eserbisyo.Models.Document;
import com.example.eserbisyo.Models.Notification;
import com.example.eserbisyo.Models.Type;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class NotificationActivity extends AppCompatActivity {

    public static RecyclerView recyclerView;
    private CheckBox checkboxSubscribed;
    private SwipeRefreshLayout refreshLayout;
    public static ArrayList<Notification> arrayList;
    private NotificationsAdapter notificationsAdapter;

    private SharedPreferences userPref;
    private ProgressDialog progressDialog;
    public JSONObject errorObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        init();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
        finish();
    }

    private void init() {

        checkboxSubscribed = findViewById(R.id.checkboxSubscribed);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        refreshLayout = findViewById(R.id.swipeRecyclerView);

        userPref = NotificationActivity.this.getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        getData();
    }

    private void getData() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Api.NOTIFICATION_LIST, response -> {

            try {
                JSONObject object = new JSONObject(response);

                if (object.getString("is_subscribed").equals("Yes")) {
                    checkboxSubscribed.setChecked(true);
                }

                JSONArray array = new JSONArray(object.getString("data"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject notificationJSONObject = array.getJSONObject(i);
                    Log.d("notification", notificationJSONObject.toString(4));

                    Notification mNotification = new Notification(
                            notificationJSONObject.getInt("id"),
                            notificationJSONObject.getString("message"),
                            notificationJSONObject.getString("is_seen"),
                            notificationJSONObject.getString("notifiable_type"),
                            notificationJSONObject.getInt("notifiable_id"),
                            notificationJSONObject.getString("created_at")
                    );

                    arrayList.add(mNotification);
                }

                notificationsAdapter = new NotificationsAdapter(NotificationActivity.this, arrayList);
                recyclerView.setAdapter(notificationsAdapter);

                initListener();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        },error -> {
            refreshLayout.setRefreshing(false);

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

                startActivity(new Intent(NotificationActivity.this,AuthActivity.class));
                finish();
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

        RequestQueue queue = Volley.newRequestQueue(NotificationActivity.this);
        queue.add(request);
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(this::getData);

        checkboxSubscribed.setOnClickListener(view -> subscribed());
    }

    private void subscribed() {
        progressDialog.setMessage("Submitting please wait.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.SUBSCRIBE, response -> {

            try {
                JSONObject object = new JSONObject(response);

                Toasty.success(this, object.getString("message"), Toast.LENGTH_LONG, true).show();

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

        RequestQueue queue = Volley.newRequestQueue(NotificationActivity.this);
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

    public void back(View view) {
        startActivity(new Intent(NotificationActivity.this, HomeActivity.class));
        finish();
    }
}