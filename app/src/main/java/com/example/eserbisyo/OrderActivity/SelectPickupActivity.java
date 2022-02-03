package com.example.eserbisyo.OrderActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.eserbisyo.ModelActivities.MissingItemAddActivity;
import com.example.eserbisyo.ModelActivities.MissingItemEditActivity;
import com.example.eserbisyo.ModelActivities.MissingPersonAddActivity;
import com.example.eserbisyo.Models.Complainant;
import com.example.eserbisyo.Models.MissingItem;
import com.example.eserbisyo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SelectPickupActivity extends AppCompatActivity {

    private AutoCompleteTextView autoCompleteOrderType;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private JSONObject errorObj = null;

    private final String[] orderTypeSelector = new String[]{
            "Pickup", "Delivery"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pickup);


        init();
    }

    private void init() {
        autoCompleteOrderType = findViewById(R.id.autoCompleteOrderType);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(SelectPickupActivity.this);
        progressDialog.setCancelable(false);

        ArrayAdapter<String> orderTypeAdapter = new ArrayAdapter<>(
                SelectPickupActivity.this,
                R.layout.list_item,
                orderTypeSelector
        );

        autoCompleteOrderType.setAdapter(orderTypeAdapter);
        autoCompleteOrderType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(autoCompleteOrderType.getText()).toString().isEmpty()){
                    Toasty.error(SelectPickupActivity.this, "Please select order type to proceed", Toasty.LENGTH_LONG, true).show();
                } else {
                    getAvailCert();
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void getAvailCert() {
        progressDialog.setMessage("Getting available certificate please wait.....");
        progressDialog.show();

        String pickupType = Objects.requireNonNull(autoCompleteOrderType.getText()).toString().trim();

        StringRequest request = new StringRequest(Request.Method.GET, Api.ORDERS + Api.CREATE + pickupType, response->{
            try {
                JSONObject object = new JSONObject(response);
                JSONArray array = new JSONArray(object.getString("data"));

                if(array.length() > 0){
                    Intent intent = new Intent(SelectPickupActivity.this, CreateOrderActivity.class);
                    intent.putExtra(Extra.JSON_ARRAY, array.toString());
                    intent.putExtra(Extra.ORDER_TYPE, pickupType);
                    startActivity(intent);

                } else {
                    Toasty.info(SelectPickupActivity.this, "There are no available certificates to be requested in this order type. Please comeback later", Toasty.LENGTH_LONG, true).show();
                }
                progressDialog.dismiss();
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

        RequestQueue queue = Volley.newRequestQueue(SelectPickupActivity.this);
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

    @Override
    public void onBackPressed() {
        finish();
    }
}