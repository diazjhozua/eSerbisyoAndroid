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
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.ModelActivities.FeedbackAddActivity;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ComplaintsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.OrdersAdapter;
import com.example.eserbisyo.Models.Complaint;
import com.example.eserbisyo.Models.Order;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.Models.User;
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

public class OrderFragment extends Fragment {
    private View view;
    public static RecyclerView recyclerView;
    public static ArrayList<Order> arrayList;
    private SwipeRefreshLayout refreshLayout;
    private OrdersAdapter ordersAdapter;
    private SharedPreferences sharedPreferences;
    private EditText txtSearch;

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


    public OrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_order, container, false);
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

        getData();

        btnAdd.setOnClickListener(view -> {
            if(!sharedPreferences.getBoolean(Pref.IS_VERIFIED, false)){
                Toasty.info(requireContext(), "This function is for verified user only.", Toast.LENGTH_LONG, true).show();
            } else {
                startActivity(new Intent(getContext(), SelectPickupActivity.class));
            }
        });

        refreshLayout.setOnRefreshListener(this::getData);

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try { ordersAdapter.getFilter().filter(txtSearch.getText().toString());
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

        StringRequest request = new StringRequest(Request.Method.GET, Api.ORDERS, response -> {

            try {
                JSONObject object = new JSONObject(response);

                JSONArray array = new JSONArray(object.getString("data"));
                for (int i = 0; i < array.length(); i++) {
                    JSONObject orderJSONObject = array.getJSONObject(i);
                    Log.d("order", orderJSONObject.toString(4));


                    User mBiker = null;

                    if (!orderJSONObject.isNull("biker")) {
                        JSONObject bikerJSONObject = orderJSONObject.getJSONObject("biker");

                        mBiker = new User(
                                bikerJSONObject.getInt("id"), bikerJSONObject.getString("first_name") + bikerJSONObject.getString("middle_name") + bikerJSONObject.getString("last_name"),
                                bikerJSONObject.getString("phone_no"), bikerJSONObject.getString("email"),
                                bikerJSONObject.getString("picture_name"), bikerJSONObject.getString("file_path"), bikerJSONObject.getString("bike_type"),
                                bikerJSONObject.getString("bike_color"),    bikerJSONObject.getString("bike_size")
                        );
                    }

                    Order mOrder = new Order(
                            orderJSONObject.getInt("id"), orderJSONObject.getString("created_at"), orderJSONObject.getString("pick_up_type"),
                            orderJSONObject.getString("order_status"), orderJSONObject.getString("pickup_date"), orderJSONObject.getString("received_at"), orderJSONObject.getDouble("total_price"),
                            orderJSONObject.getDouble("delivery_fee"), orderJSONObject.getString("application_status"), mBiker,
                            orderJSONObject.getString("admin_message"), orderJSONObject.getString("updated_at")
                    );

                    Log.d("order status", String.valueOf(mOrder.getmBiker() == null));

                    arrayList.add(mOrder);
                }
                ordersAdapter = new OrdersAdapter(requireContext(), arrayList);
                recyclerView.setAdapter(ordersAdapter);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            refreshLayout.setRefreshing(false);

        }, error -> {
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
        }) {
            // provide token in header
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString(Pref.TOKEN, "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
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