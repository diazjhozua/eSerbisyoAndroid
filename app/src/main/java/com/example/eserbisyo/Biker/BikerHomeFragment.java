package com.example.eserbisyo.Biker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
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
import com.example.eserbisyo.HomeFragments.MainFragment;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ComplaintsAdapter;
import com.example.eserbisyo.Models.Complaint;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class BikerHomeFragment extends Fragment {
    private View view;
    private TextView txtTotalEarnings, txtTotalCompletedTransaction, txtTotalUnprocessedDelivery, txtTotalPendingDelivery, txtTotalReturnableItem;
    private Button btnViewTransaction;

    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;

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

    public BikerHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_biker_home, container, false);
        init();
        return view;
    }

    private void init() {
        txtTotalEarnings = view.findViewById(R.id.txtTotalEarnings);
        txtTotalCompletedTransaction = view.findViewById(R.id.txtTotalCompletedTransaction);
        txtTotalUnprocessedDelivery = view.findViewById(R.id.txtTotalUnprocessedDelivery);
        txtTotalPendingDelivery = view.findViewById(R.id.txtTotalPendingDelivery);
        txtTotalReturnableItem = view.findViewById(R.id.txtTotalReturnableItem);

        btnViewTransaction = view.findViewById(R.id.btnViewTransaction);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        sharedPreferences = requireContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        initListeners();

        getData();
    }

    private void initListeners() {
        btnViewTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((HomeActivity) requireActivity()).switchFragment(new BikerTransactionFragment());
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void getData() {
        progressDialog.setMessage("Getting data please wait.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.BIKERS_AUTH_ANALYTICS, response -> {
            try {
                JSONObject object = new JSONObject(response);

                txtTotalEarnings.setText("₱ " + object.getDouble("totalEarnings"));
                txtTotalCompletedTransaction.setText(object.getInt("totalCompletedOrder") + " total of transaction");
                txtTotalUnprocessedDelivery.setText(object.getInt("totalUnprocessedDelivery") + " total of transaction");
                txtTotalPendingDelivery.setText(object.getInt("totalPendingDelivery") + " total of transaction");
                txtTotalReturnableItem.setText(object.getInt("totalReturnableItem") + " total of transaction");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        },error -> {
            progressDialog.dismiss();

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