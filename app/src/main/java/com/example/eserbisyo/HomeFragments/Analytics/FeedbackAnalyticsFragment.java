package com.example.eserbisyo.HomeFragments.Analytics;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.eserbisyo.HomeFragments.ReportFragment;
import com.example.eserbisyo.ModelRecyclerViewAdapters.FeedbackTypesAdapter;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class FeedbackAnalyticsFragment extends Fragment {

    private View view;
    private PieChart pieChart;
    private RecyclerView rvTrending;
    private JSONObject errorObj = null;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private FeedbackTypesAdapter feedbackTypesAdapter;

    public static ArrayList<Type> arrayTypeList;
    private ArrayList<PieEntry> pieEntries;

    private AppCompatRatingBar ratingBarThisMonth, ratingBarThisYear, ratingBarOverall;
    private TextView txtRatingThisMonth, txtRatingThisYear, txtRatingOverall;

    public FeedbackAnalyticsFragment() {
        // Required empty public constructor
    }

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
        view =  inflater.inflate(R.layout.fragment_feedback_analytics, container, false);
        init();
        return view;
    }

    private void init() {
        ratingBarThisMonth = view.findViewById(R.id.ratingBarThisMonth);
        ratingBarThisYear = view.findViewById(R.id.ratingBarThisYear);
        ratingBarOverall = view.findViewById(R.id.ratingBarOverall);

        txtRatingThisMonth = view.findViewById(R.id.txtRatingThisMonth);
        txtRatingThisYear = view.findViewById(R.id.txtRatingThisYear);
        txtRatingOverall = view.findViewById(R.id.txtRatingOverall);

        pieChart = view.findViewById(R.id.pieChart);
        rvTrending = view.findViewById(R.id.rvTrending);
        Button btnView = view.findViewById(R.id.btnView);
        sharedPreferences = requireContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        rvTrending.setHasFixedSize(false);
        rvTrending.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);

        btnView.setOnClickListener(view -> {
            ((HomeActivity) requireActivity()).switchFragment(new ReportFragment());
            ((HomeActivity) requireActivity()).setReportNavCheck();
        });

        setPieChartSettings();
        getData();
    }

    private void setPieChartSettings() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setCenterText(generateCenterSpannableText());

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setDrawCenterText(true);

        pieChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.animateY(1400, Easing.EaseInOutQuad);
        // chart.spin(2000, 0, 360);


        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setEntryLabelTextSize(12f);
    }

    private CharSequence generateCenterSpannableText() {
        SpannableString s = new SpannableString("MOST SUBMITTED FEEDBACKS (THIS YEAR)");
        s.setSpan(new RelativeSizeSpan(1.2f), 0, s.length(), 0);
        return s;
    }
    @SuppressLint("SetTextI18n")
    private void getData() {
        arrayTypeList = new ArrayList<>();

        progressDialog.setMessage("Loading.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.FEEDBACKS_ANALYTICS, response -> {

            try {
                JSONObject object = new JSONObject(response);

                double monthAvg = object.getDouble("monthAvg");
                double yearAvg = object.getDouble("yearAvg");
                double overallAvg = object.getDouble("overall");

                JSONArray typesJsonArray = new JSONArray(object.getString("feedbackTypes"));
                JSONArray trendingTypesJsonArray = new JSONArray(object.getString("trendingFeedbacks"));

                ratingBarThisMonth.setRating((float) monthAvg);
                ratingBarThisYear.setRating((float) yearAvg);
                ratingBarOverall.setRating((float) overallAvg);

                txtRatingThisMonth.setText("Rating: " + monthAvg + "/5" );
                txtRatingThisYear.setText("Rating: " + yearAvg + "/5" );
                txtRatingOverall.setText("Rating: " + overallAvg + "/5" );

                pieEntries = new ArrayList<>();

                for (int i = 0; i < typesJsonArray.length(); i++) {
                    JSONObject typesJsonObject = typesJsonArray.getJSONObject(i);
                    Log.d("typesJsonObject", typesJsonObject.toString(4));

                    if (typesJsonObject.getInt("feedbacks_count") > 0) {
                        pieEntries.add(new PieEntry((float) (typesJsonObject.getInt("feedbacks_count")),
                                typesJsonObject.getString("name")));
                    }

                }

                for (int i = 0; i < trendingTypesJsonArray.length(); i++) {
                    JSONObject typesJsonObject = trendingTypesJsonArray.getJSONObject(i);
                    Log.d("typesJsonObject", typesJsonObject.toString(4));

                    Type mType = new Type(
                            typesJsonObject.getInt("id"), typesJsonObject.getString("name"),
                            typesJsonObject.getInt("feedbacks_count") + " feedbacks",
                            typesJsonObject.getDouble("feedbacks_avg_rating")
                    );
                    arrayTypeList.add(mType);

                }

                feedbackTypesAdapter = new FeedbackTypesAdapter(arrayTypeList);
                rvTrending.setAdapter(feedbackTypesAdapter);

                setPieChartData();

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

    private void setPieChartData() {
        PieDataSet dataSet = new PieDataSet(pieEntries, "");
        dataSet.setDrawIcons(false);

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        dataSet.setColors(colors);
        dataSet.setValueTextColor(Color.BLUE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);

        data.setValueTextSize(9f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.getData().setValueTextColor(Color.BLACK);
        pieChart.getLegend().setTextColor(Color.BLACK);
        pieChart.getLegend().setTextSize(9);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }
}