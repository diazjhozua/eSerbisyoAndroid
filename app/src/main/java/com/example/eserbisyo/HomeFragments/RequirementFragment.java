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

import com.android.volley.AuthFailureError;
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
import com.example.eserbisyo.ModelActivities.ReportAddActivity;
import com.example.eserbisyo.ModelActivities.RequirementAddActivity;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ReportsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.RequirementsAdapter;
import com.example.eserbisyo.Models.Requirement;
import com.example.eserbisyo.Models.UserRequirement;
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


public class RequirementFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<UserRequirement> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private RequirementsAdapter requirementsAdapter;
    private SharedPreferences sharedPreferences;
    private EditText txtSearch;
    private FloatingActionButton btnAdd;

    private ProgressDialog progressDialog;

    public JSONObject errorObj = null;

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

    public RequirementFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((HomeActivity) requireActivity()).setRequirementNavCheck();
        view =  inflater.inflate(R.layout.fragment_requirement, container, false);
        init();
        return view;
    }

    private void init() {
        btnAdd = view.findViewById(R.id.btnAdd);
        txtSearch = view.findViewById(R.id.inputTxtSearch);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refreshLayout = view.findViewById(R.id.swipeRecyclerView);

        sharedPreferences = getContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        /*For loading when the user submits the genre form*/
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        getRequirements();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(((HomeActivity)getContext()), RequirementAddActivity.class));
            }
        });

        refreshLayout.setOnRefreshListener(this::getRequirements);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try { requirementsAdapter.getFilter().filter(txtSearch.getText().toString());
                } catch (NullPointerException ignored) {}
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void getRequirements() {
        arrayList = new ArrayList<UserRequirement>();
        refreshLayout.setRefreshing(true);

        StringRequest request = new StringRequest(Request.Method.GET, Api.USER_REQUIREMENTS, response -> {

            try {
                JSONObject object = new JSONObject(response);

                JSONArray array = new JSONArray(object.getString("data"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject userRequirementObject = array.getJSONObject(i);
                    JSONObject requirementObject = userRequirementObject.getJSONObject("requirement");

                    Log.d("requirement", userRequirementObject.toString(4));

                    UserRequirement userRequirement = new UserRequirement(
                            userRequirementObject.getInt("id"), userRequirementObject.getInt("user_id"),  userRequirementObject.getInt("requirement_id"),
                            userRequirementObject.getString("file_name"), userRequirementObject.getString("file_path"), userRequirementObject.getString("created_at"),
                            new Requirement(requirementObject.getInt("id"), requirementObject.getString("name")));
                    arrayList.add(userRequirement);
                }
                requirementsAdapter = new RequirementsAdapter(getContext(), arrayList);
                recyclerView.setAdapter(requirementsAdapter);

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
                        ((HomeActivity) requireActivity()).showErrorMessage(errors);
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
            } catch (Exception ignored) {
                Toasty.error(requireContext(), "No internet/data connection detected", Toast.LENGTH_SHORT, true).show();
            }
        }){
            // provide token in header
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
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