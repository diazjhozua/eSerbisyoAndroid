package com.example.eserbisyo.ModelActivities.Profile;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.eserbisyo.Models.Document;
import com.example.eserbisyo.Models.Ordinance;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.github.barteksc.pdfviewer.PDFView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class OrdinanceActivity extends AppCompatActivity {
    private TextView txtNo, txtTitle, txtDateApp, txtType, txtCreatedAt;
    private PDFView pdfView;
    private Button btnDownload;

    private int modelId = 0;
    private ProgressDialog progressDialog;
    private SharedPreferences userPref;
    public JSONObject errorObj = null;

    public static Ordinance mOrdinance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordinance);
        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            modelId = extras.getInt(Extra.MODEL_ID, 0);
        }
        init();
    }

    private void init() {
        txtNo = findViewById(R.id.txtOrdinanceNo);
        txtTitle = findViewById(R.id.txtOrdinanceTitle);
        txtDateApp = findViewById(R.id.txtOrdinanceDateApproved);
        txtType = findViewById(R.id.txtOrdinanceType);
        txtCreatedAt = findViewById(R.id.txtOrdinanceCreatedAt);

        btnDownload = findViewById(R.id.btnDownload);
        pdfView = findViewById(R.id.pdfView);

        userPref = OrdinanceActivity.this.getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        getData();
    }

    private void getData() {
        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.ORDINANCES + "/" + modelId, response -> {
            try {
                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject ordinanceJSONObject = object.getJSONObject("data");

                mOrdinance = new Ordinance(
                        ordinanceJSONObject.getInt("id"), new Type(ordinanceJSONObject.getInt("type_id"), ordinanceJSONObject.getString("ordinance_type")),
                        ordinanceJSONObject.getString("custom_type"), ordinanceJSONObject.getString("ordinance_no"), ordinanceJSONObject.getString("title") ,
                        ordinanceJSONObject.getString("date_approved"), ordinanceJSONObject.getString("pdf_name"), ordinanceJSONObject.getString("file_path"),
                        ordinanceJSONObject.getString("created_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            setData();

            progressDialog.dismiss();

        },error -> {
            error.printStackTrace();
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
                        Toasty.error(this, errorObj.getString("message"), Toast.LENGTH_LONG, true).show();
                    } catch (JSONException ignored) {
                    }
                } else {
                    Toasty.error(this, "Request Timeout", Toast.LENGTH_SHORT, true).show();
                }
            } catch (Exception ignored) {
                Toasty.error(this, "No internet/data connection detected", Toast.LENGTH_SHORT, true).show();
            }

            finish();
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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(OrdinanceActivity.this);
        queue.add(request);
    }


    @SuppressLint("SetTextI18n")
    private void setData() {
        if (mOrdinance.getType().getId() == 0) {
            txtType.setText("Type: " + mOrdinance.getCustomType());
        } else {
           txtType.setText("Type: " + mOrdinance.getType().getName());
        }

        txtTitle.setText('"' + mOrdinance.getTitle() + '"');
        txtNo.setText("Ordinance No: " + mOrdinance.getOrdinanceNo());
        txtDateApp.setText("Date Approved: " + mOrdinance.getDateApproved());
        txtCreatedAt.setText(mOrdinance.getCreatedAt());

        new RetrievePDFromUrl().execute(mOrdinance.getFilePath());

        initListener();
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


    private void initListener() {
        btnDownload.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mOrdinance.getFilePath()));
            startActivity(browserIntent);
        });
    }

    // create an async task class for loading pdf file from URL.
    @SuppressLint("StaticFieldLeak")
    class RetrievePDFromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            pdfView.fromStream(inputStream)
                    .enableSwipe(true) // allows to block changing pages using swipe
                    .swipeHorizontal(true)
                    .enableDoubletap(true)
                    .defaultPage(0).load();
        }
    }

    public void back(View view) {
        finish();
    }
}