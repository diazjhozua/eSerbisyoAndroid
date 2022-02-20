package com.example.eserbisyo.HomeFragments.Analytics;

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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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
import com.example.eserbisyo.ModelRecyclerViewAdapters.TypesAdapter;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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

public class ReportAnalyticsFragment extends Fragment {
    private View view;
    private PieChart pieChart;
    private BarChart barChart;
    private RecyclerView rvTrending;
    private Button btnView;
    private JSONObject errorObj = null;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private TypesAdapter typesAdapter;

    public static ArrayList<Type> arrayTypeList;
    private ArrayList<PieEntry> pieEntries;
    private ArrayList<BarEntry> barEntries;


    public ReportAnalyticsFragment() {
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
        view =  inflater.inflate(R.layout.fragment_report_analytics, container, false);
        init();
        return view;
    }

    private void init() {
        pieChart = view.findViewById(R.id.pieChart);
        barChart = view.findViewById(R.id.barChart);
        rvTrending = view.findViewById(R.id.rvTrending);
        btnView = view.findViewById(R.id.btnView);
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

    private void getData() {
        arrayTypeList = new ArrayList<>();

        progressDialog.setMessage("Loading.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.REPORTS_ANALYTICS, response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONArray typesJsonArray = new JSONArray(object.getString("reportTypes"));
                JSONArray trendingTypesJsonArray = new JSONArray(object.getString("trendingReports"));
                JSONObject userSubmittedJsonObject = new JSONObject(object.getString("userReport"));
                pieEntries = new ArrayList<>();
                barEntries = new ArrayList<>();

                for (int i = 0; i < typesJsonArray.length(); i++) {
                    JSONObject typesJsonObject = typesJsonArray.getJSONObject(i);
                    Log.d("typesJsonObject", typesJsonObject.toString(4));

                    if (typesJsonObject.getInt("reports_count") > 0) {
                        pieEntries.add(new PieEntry((float)  (typesJsonObject.getInt("reports_count")),
                                typesJsonObject.getString("name")));
                    }
                }

                for (int i = 1; i < 13; i++) {
                    barEntries.add(new BarEntry(i, (float) userSubmittedJsonObject.getDouble(String.valueOf(i))));
                }

                for (int i = 0; i < trendingTypesJsonArray.length(); i++) {
                    JSONObject typesJsonObject = trendingTypesJsonArray.getJSONObject(i);
                    Log.d("typesJsonObject", typesJsonObject.toString(4));

                    Type mType = new Type(
                            typesJsonObject.getInt("id"), typesJsonObject.getString("name"), typesJsonObject.getInt("reports_count") + " reports"
                    );
                    arrayTypeList.add(mType);
                }

                typesAdapter = new TypesAdapter(requireContext(), arrayTypeList);
                rvTrending.setAdapter(typesAdapter);

                setPieChartData();
                setBarGraphData();



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

    private void setBarGraphData() {
        BarDataSet barDataSet = new BarDataSet(barEntries, "MONTHS");
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        barDataSet.setColors(colors);

        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("Average reports submitted by resident per month");
        barChart.animateY(2000);

        String[] xAxisLables = new String[]{"January","February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(xAxisLables));
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

        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.getData().setValueTextColor(Color.BLACK);
        pieChart.getLegend().setTextColor(Color.BLACK);
        pieChart.getLegend().setTextSize(11);

        // undo all highlights
        pieChart.highlightValues(null);

        pieChart.invalidate();
    }

    private SpannableString generateCenterSpannableText() {

        SpannableString s = new SpannableString("MOST SUBMITTED REPORTS (THIS YEAR)");
        s.setSpan(new RelativeSizeSpan(1.2f), 0, s.length(), 0);
        return s;
    }
}