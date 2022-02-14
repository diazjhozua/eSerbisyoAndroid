package com.example.eserbisyo.HomeFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

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
import com.example.eserbisyo.ModelRecyclerViewAdapters.CertificatesAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ProjectsAdapter;
import com.example.eserbisyo.Models.Certificate;
import com.example.eserbisyo.Models.Complainant;
import com.example.eserbisyo.Models.Project;
import com.example.eserbisyo.Models.Requirement;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.OrderActivity.SelectPickupActivity;
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

public class CertificateFragment extends Fragment {

    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Certificate> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private CertificatesAdapter certificatesAdapter;
    private SharedPreferences sharedPreferences;
    private EditText txtSearch;

    public JSONObject errorObj = null;

    public CertificateFragment() {
        // Required empty public constructor
    }

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
        view =  inflater.inflate(R.layout.fragment_certificate, container, false);
        init();
        return view;
    }


    private void init() {
        txtSearch = view.findViewById(R.id.inputTxtSearch);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeRecyclerView);

        sharedPreferences = requireContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        FloatingActionButton btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(view -> {
            if(sharedPreferences.getInt(Pref.IS_VERIFIED, 0) != 1){
                Toasty.info(requireContext(), "This function is for verified user only.", Toast.LENGTH_LONG, true).show();
            } else {
                new Intent(((HomeActivity)getContext()), SelectPickupActivity.class);
            }
        });

        /*For loading when the user submits the genre form*/
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        getData();

        refreshLayout.setOnRefreshListener(this::getData);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try { certificatesAdapter.getFilter().filter(txtSearch.getText().toString());
                } catch (NullPointerException ignored) {}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getData() {
        arrayList = new ArrayList<>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Api.ORDER_CERTIFICATE, response -> {

            try {
                JSONObject object = new JSONObject(response);

                JSONArray array = new JSONArray(object.getString("data"));
                for (int i = 0; i < array.length(); i++) {

                    JSONObject certificateJSONObject = array.getJSONObject(i);
                    Log.d("certificate", certificateJSONObject.toString(4));

                    ArrayList<Requirement> requirementArrayList = new ArrayList<>();
                    JSONArray requirementsJSONArray = new JSONArray(certificateJSONObject.getString("requirements"));
                    if(requirementsJSONArray.length() > 0){
                        for (int j = 0; j < requirementsJSONArray.length(); j++) {
                            JSONObject requirementJSONObject = requirementsJSONArray.getJSONObject(j);

                            Requirement mRequirement = new Requirement(requirementJSONObject.getInt("id"), requirementJSONObject.getString("name"));
                            requirementArrayList.add(mRequirement);
                        }
                    }

                    Certificate mCertificate = new Certificate(
                            certificateJSONObject.getInt("id"), certificateJSONObject.getString("name"), certificateJSONObject.getDouble("price"),
                            certificateJSONObject.getString("status"), certificateJSONObject.getString("delivery_option"), certificateJSONObject.getString("requirements_count"),
                            requirementArrayList
                            );

                    arrayList.add(mCertificate);
                }

                certificatesAdapter = new CertificatesAdapter(requireContext(), arrayList);
                recyclerView.setAdapter(certificatesAdapter);

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