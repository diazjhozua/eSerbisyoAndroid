package com.example.eserbisyo.OrderActivity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.eserbisyo.Adapters.DDCertificatesAdapter;
import com.example.eserbisyo.Adapters.DDTypeAdapter;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.ModelRecyclerViewAdapters.DefendantsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.FormsAdapter;
import com.example.eserbisyo.Models.Certificate;
import com.example.eserbisyo.Models.Defendant;
import com.example.eserbisyo.Models.Form;
import com.example.eserbisyo.Models.MissingItem;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CreateOrderActivity extends AppCompatActivity {
    private TextInputLayout layoutName, layoutLocation, layoutEmail, layoutPhoneNo;
    private TextInputEditText inputTxtName, inputTxtLocation, inputTxtEmail, inputTxtPhoneNo;
    private TextView txtOrderType, txtMarkLocation, txtLocationSet, txtAddCertificate;
    private LinearLayoutCompat layoutDelivery;
    public static RecyclerView recyclerView;

    private Button btnSubmit;
    private Spinner spnType;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private String orderType;
    public static ArrayList<Form> formArrayList;
    private ArrayList<Certificate> certificateArrayList;
    public static FormsAdapter formsAdapter;
    private Certificate selCertificate;

    public static TextView txtCertificateCount, txtTotalCertPrice,txtDeliveryFee, txtTotalFee;
    public static Double totalCertPrice, deliveryFee, totalFee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            try {
                JSONArray jsonArray = new JSONArray(extras.getString(Extra.JSON_ARRAY));
                orderType = extras.getString(Extra.ORDER_TYPE);

                certificateArrayList = new ArrayList<>();

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject certificateObject = jsonArray.getJSONObject(i);
                    Certificate mCertificate = new Certificate(
                            certificateObject.getInt("id"),
                            certificateObject.getString("name"),
                            certificateObject.getDouble("price")
                    );
                    certificateArrayList.add(mCertificate);
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }
        }
        init();

    }

    @SuppressLint("SetTextI18n")
    private void init() {

        layoutName = findViewById(R.id.txtLayoutName);
        layoutLocation = findViewById(R.id.txtLayoutLocationAddress);
        layoutEmail = findViewById(R.id.txtLayoutEmail);
        layoutPhoneNo = findViewById(R.id.txtLayoutPhone);

        inputTxtName = findViewById(R.id.inputTxtName);
        inputTxtLocation = findViewById(R.id.inputTxtLocationAddress);
        inputTxtEmail = findViewById(R.id.inputTxtEmail);
        inputTxtPhoneNo = findViewById(R.id.inputTxtPhone);

        txtOrderType = findViewById(R.id.txtOrderType);
        txtMarkLocation = findViewById(R.id.txtMarkLocation);
        txtLocationSet = findViewById(R.id.txtLocationSet);
        txtAddCertificate = findViewById(R.id.txtAddCertificate);

        txtCertificateCount = findViewById(R.id.txtCertificateCount);
        txtTotalCertPrice = findViewById(R.id.txtTotalCertPrice);
        txtDeliveryFee = findViewById(R.id.txtDeliveryFee);
        txtTotalFee = findViewById(R.id.txtTotalFee);

        layoutDelivery = findViewById(R.id.layoutDelivery);
        recyclerView = findViewById(R.id.recyclerView);
        btnSubmit = findViewById(R.id.btnSubmit);

        spnType = findViewById(R.id.spinnerType);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CreateOrderActivity.this));

        formArrayList = new ArrayList<>();
        formArrayList.add(new Form(
                0, 1, "Barangay Indigency", 200.0, "Jhozua", "Manguera", "Diaz",
                "633 Purok 5", "Single", "09-12-2000", "Filipino", "NBI Clearance", null, null, null, null, null,
                null, null, null, null, null, null, null, null
        ));
        formsAdapter = new FormsAdapter(CreateOrderActivity.this, formArrayList);
        recyclerView.setAdapter(formsAdapter);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        setData();

    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        txtCertificateCount.setText("Certificate Requested: " + formsAdapter.getItemCount() + " (Total)");

        totalCertPrice = 0.0;

        if (orderType.equals("Delivery")) {
            deliveryFee = 60.0;
        } else {
            deliveryFee = 0.0;
        }

        totalFee = formsAdapter.getTotalPrice() + deliveryFee;
        txtTotalCertPrice.setText("Total Certificate Price: ₱ " + formsAdapter.getTotalPrice());
        txtDeliveryFee.setText("Delivery fee: ₱ " + deliveryFee);
        txtTotalFee.setText("Total fee: ₱ " + totalFee);

        txtOrderType.setText("ORDER TYPE: " + orderType.toUpperCase(Locale.ROOT));
        inputTxtName.setText(userPref.getString(Pref.FIRST_NAME, "") + " " + userPref.getString(Pref.MIDDLE_NAME, "") + " " + userPref.getString(Pref.LAST_NAME, ""));
        inputTxtEmail.setText(userPref.getString(Pref.EMAIL, ""));

        DDCertificatesAdapter ddCertificatesAdapter = new DDCertificatesAdapter(this, certificateArrayList);
        spnType.setAdapter(ddCertificatesAdapter);

        if (!orderType.equals("Delivery")) {
            layoutDelivery.setVisibility(View.GONE);
        } else {
            txtLocationSet.setVisibility(View.GONE);
        }

        initListener();
    }

    private void initListener() {
        btnSubmit.setOnClickListener(v -> {
            // store feedback
            if (validate()) {
                submitData();
            }
        });

        txtAddCertificate.setOnClickListener(v -> {
             Intent intent = new Intent(CreateOrderActivity.this, FormAddActivity.class);
             intent.putExtra(Extra.MODEL, selCertificate);
             startActivity(intent);
        });

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Certificate mcertificate = (Certificate) parent.getSelectedItem();
                selCertificate = mcertificate;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Name Listener
        inputTxtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtName.getText()).toString().length()>=3 && Objects.requireNonNull(inputTxtName.getText()).toString().length()<=200){
                    layoutName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Location Address Listener
        inputTxtLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtLocation.getText()).toString().length()>=3 && Objects.requireNonNull(inputTxtLocation.getText()).toString().length()<=200){
                    layoutLocation.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Email Listener
        inputTxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputTxtEmail.getText()).toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(inputTxtEmail.getText().toString()).matches()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // Phone Listener
        inputTxtPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputTxtPhoneNo.getText()).toString().isEmpty() && Objects.requireNonNull(inputTxtPhoneNo.getText()).toString().length() == 11){
                    layoutPhoneNo.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {

        if (Objects.requireNonNull(inputTxtName.getText()).toString().length() < 3){
            layoutName.setErrorEnabled(true);
            layoutName.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtName.getText()).toString().length() > 100){
            layoutName.setErrorEnabled(true);
            layoutName.setError("Required no more than 100 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtLocation.getText()).toString().length() < 3){
            layoutLocation.setErrorEnabled(true);
            layoutLocation.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtLocation.getText()).toString().length() > 200){
            layoutLocation.setErrorEnabled(true);
            layoutLocation.setError("Required no more than 200 characters");
            return false;
        }


        /* Email Validation  */
        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(inputTxtEmail.getText()).toString()).matches() || Objects.requireNonNull(inputTxtEmail.getText()).toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Invalid email address");
            return false;
        }

        /* Phone Validation */
        if (Objects.requireNonNull(inputTxtPhoneNo.getText()).toString().length() != 11){
            layoutPhoneNo.setErrorEnabled(true);
            layoutPhoneNo.setError("Invalid phone number. Must start at 09*********");
            return false;
        }

        return true;
    }

    private void submitData() {

    }



    public void cancelEdit(View view) {
        finish();
    }
}