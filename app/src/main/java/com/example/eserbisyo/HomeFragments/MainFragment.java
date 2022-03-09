package com.example.eserbisyo.HomeFragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.AccountActivities.UserVerificationActivity;
import com.example.eserbisyo.AuthActivity;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.HomeFragments.Analytics.ComplaintAnalyticsFragment;
import com.example.eserbisyo.HomeFragments.Analytics.FeedbackAnalyticsFragment;
import com.example.eserbisyo.HomeFragments.Analytics.ReportAnalyticsFragment;
import com.example.eserbisyo.OrderActivity.SelectPickupActivity;
import com.example.eserbisyo.R;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class MainFragment extends Fragment {
    private View view;

    public JSONObject errorObj = null;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((HomeActivity) requireActivity()).setHomeNavCheck();
        view = inflater.inflate(R.layout.fragment_main, container, false);
        init();
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        CardView cardAnnouncement = view.findViewById(R.id.cardAnnouncement);
        CardView cardReport = view.findViewById(R.id.cardReport);
        CardView cardFeedback = view.findViewById(R.id.cardFeedback);
        CardView cardOrdinance = view.findViewById(R.id.cardOrdinance);
        CardView cardDocument = view.findViewById(R.id.cardDocument);
        CardView cardProject= view.findViewById(R.id.cardProject);
        CardView cardEmployee = view.findViewById(R.id.cardEmployee);
        TextView txtUnverified = view.findViewById(R.id.txtVerifiedStatus);
        TextView txtNotice = view.findViewById(R.id.txtNoticeApp);

        CardView cardMissingPerson = view.findViewById(R.id.cardMissingPerson);
        CardView cardMissingItem = view.findViewById(R.id.cardMissingItem);
        CardView cardComplaint = view.findViewById(R.id.cardComplaint);

        CardView cardCertificate = view.findViewById(R.id.cardCertificate);
        CardView cardOrder = view.findViewById(R.id.cardOrder);

        sharedPreferences = requireContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
        boolean isVerified =  sharedPreferences.getBoolean(Pref.IS_VERIFIED, false);

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setCancelable(false);

        if (isVerified) {
            txtUnverified.setVisibility(View.GONE);
            txtNotice.setVisibility(View.VISIBLE);
            txtNotice.setText("Important Notice: You will only receive notification in this specific email and phone number (Depending on what type of services)." +
                    System.getProperty("line.separator") +
                    System.getProperty("line.separator") +
                    "Email: brg.cupang.unofficial@gmail.com" +
                    System.getProperty("line.separator") +
                    "Phone #: 0977-714-6176"
            );
        }

        txtUnverified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserVerification();
            }
        });

        cardAnnouncement.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new AnnouncementFragment()));

        cardReport.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new ReportAnalyticsFragment()));

        cardFeedback.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new FeedbackAnalyticsFragment()));

        cardCertificate.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new CertificateFragment()));

        cardOrdinance.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new OrdinanceFragment()));

        cardDocument.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new DocumentFragment()));

        cardProject.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new ProjectFragment()));

        cardEmployee.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new EmployeeFragment()));

        cardMissingPerson.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new MissingPersonFragment()));

        cardMissingItem.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new MissingItemFragment()));

        cardComplaint.setOnClickListener(v-> ((HomeActivity) requireActivity()).switchFragment(new ComplaintAnalyticsFragment()));

        cardOrder.setOnClickListener(v->{
            if(isVerified){
                startActivity(new Intent(requireContext(), SelectPickupActivity.class));
            }
        });
    }

    private void loadUserVerification() {
        progressDialog.setMessage("Checking");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, Api.MY_VERIFICATION_REQUEST, response->{

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("isEmpty")) {
                    startActivity(new Intent(requireContext(), UserVerificationActivity.class));
                } else {
                    JSONObject userVerification = object.getJSONObject("data");
                    Intent intent = new Intent(requireContext(), UserVerificationActivity.class);
                    intent.putExtra(Extra.USER_VERIFICATION_OBJECT, userVerification.toString());
                    startActivity(intent);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        },error ->{
            progressDialog.dismiss();
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
        } ){

            //add token to headers
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