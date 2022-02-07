package com.example.eserbisyo.HomeFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.AuthActivity;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.ModelActivities.FeedbackAddActivity;
import com.example.eserbisyo.ModelActivities.ReportAddActivity;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ReportsAdapter;
import com.example.eserbisyo.Models.Report;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;


public class ReportFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Report> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private ReportsAdapter reportsAdapter;
    private SharedPreferences sharedPreferences;
    private EditText txtSearch;

    public JSONObject errorObj = null;

    public ReportFragment() {
        // Required empty public constructor
    }

    /* OVERRIDE TO ADD ANIMATION WHEN THE USER CLICK BACK BUTTON ON THEIR DEVICE */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ((HomeActivity) requireActivity()).switchFragment(new MainFragment());
                ((HomeActivity) requireActivity()).setHomeNavCheck();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((HomeActivity) requireActivity()).setReportNavCheck();
        view =  inflater.inflate(R.layout.fragment_report, container, false);
        init();
        return view;
    }

    private void init() {
        FloatingActionButton btnAdd = view.findViewById(R.id.btnAdd);
        txtSearch = view.findViewById(R.id.inputTxtSearch);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeRecyclerView);

        sharedPreferences = requireContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        /*For loading when the user submits the genre form*/
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        getReports();

        btnAdd.setOnClickListener(view -> {
            if(sharedPreferences.getInt(Pref.IS_VERIFIED, 0) != 1){
                Toasty.info(requireContext(), "This function is for verified user only.", Toast.LENGTH_LONG, true).show();
            } else {
                startActivity(new Intent(((HomeActivity)getContext()), ReportAddActivity.class));
            }
        });

        refreshLayout.setOnRefreshListener(this::getReports);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try { reportsAdapter.getFilter().filter(txtSearch.getText().toString());
                } catch (NullPointerException ignored) {}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void getReports() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Api.REPORTS, response -> {

            try {
                JSONObject object = new JSONObject(response);

                JSONArray array = new JSONArray(object.getString("data"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject reportObject = array.getJSONObject(i);
                    Log.d("report", reportObject.toString(4));

                    Report report = new Report(
                            reportObject.getInt("id"), reportObject.getString("submitted_by"), new Type(reportObject.getInt("type_id"), reportObject.getString("report_type")),
                            reportObject.getString("custom_type"), reportObject.getString("location_address"), reportObject.getString("landmark") , reportObject.getString("description"),
                            reportObject.getString("urgency_classification"), reportObject.getString("picture_name"), reportObject.getString("file_path"), reportObject.getString("admin_message"),
                            reportObject.getString("status"), reportObject.getString("created_at"), reportObject.getString("updated_at"));

                    arrayList.add(report);
                }
                reportsAdapter = new ReportsAdapter(requireContext(), arrayList);
                recyclerView.setAdapter(reportsAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        },error -> {
            refreshLayout.setRefreshing(false);

            if (errorObj.has("errors")) {
                try {
                    JSONObject errors = errorObj.getJSONObject("errors");
                    ((AuthActivity) requireActivity()).showErrorMessage(getContext(), errors);
                } catch (JSONException ignored) {
                }
            } else if (errorObj.has("message")) {
                try {
                    Toasty.error(requireContext(), errorObj.getString("message"), Toast.LENGTH_SHORT, true).show();
                } catch (JSONException ignored) {
                }
            } else {
                Toasty.error(requireContext(), "Request Timeout", Toast.LENGTH_SHORT, true).show();
            }
        }){
            // provide token in header
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString(Pref.TOKEN,"");
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

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
}