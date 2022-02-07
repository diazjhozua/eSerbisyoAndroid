package com.example.eserbisyo.OrderActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Adapters.DDCertificatesAdapter;
import com.example.eserbisyo.Adapters.DDTypeAdapter;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.ComplaintFragment;
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.ModelRecyclerViewAdapters.DefendantsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.FormsAdapter;
import com.example.eserbisyo.Models.Certificate;
import com.example.eserbisyo.Models.Complainant;
import com.example.eserbisyo.Models.Complaint;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class CreateOrderActivity extends AppCompatActivity implements LocationListener {
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

    public static TextView txtCertificateCount, txtTotalCertPrice, txtDeliveryFee, txtTotalFee;
    public static Double totalCertPrice, deliveryFee, totalFee;

    private LocationManager locationManager;

    private Double longitude, latitude;

    public JSONObject errorObj = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        Bundle extras = getIntent().getExtras();
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

        //Runtime permissions
        if (ContextCompat.checkSelfPermission(CreateOrderActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CreateOrderActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
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
                "633 Purok 5", "Single", "2000-09-12", "Filipino", "NBI Clearance", null, null, null, null, null,
                null, null, null, null, null, null, null, null
        ));
        formsAdapter = new FormsAdapter(CreateOrderActivity.this, formArrayList);
        recyclerView.setAdapter(formsAdapter);
        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        setData();

    }

    @Override
    public void onLocationChanged(Location location) {
        progressDialog.hide();
        Toasty.success(this, "Location has been set", Toasty.LENGTH_LONG, true).show();
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        txtMarkLocation.setVisibility(View.GONE);
        txtLocationSet.setVisibility(View.VISIBLE);


        try {
            Geocoder geocoder = new Geocoder(CreateOrderActivity.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);

//            textView_location.setText(address);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        if (orderType.equals("Delivery")) {
            txtMarkLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create method
                    progressDialog.setMessage("Getting current location.....");
                    progressDialog.show();

                    getLocation();
                }
            });

            txtLocationSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //create method
                    progressDialog.setMessage("Getting current location.....");
                    progressDialog.show();

                    getLocation();
                }
            });
        } else {
            layoutDelivery.setVisibility(View.GONE);
        }

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

    @SuppressLint("MissingPermission")
    private void getLocation() {

        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,5000,5,CreateOrderActivity.this);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initListener() {
        btnSubmit.setOnClickListener(v -> {
            // store feedback
            if (validate()) {
                submitData();
            }
        });

        txtAddCertificate.setOnClickListener(v -> {
            if (formsAdapter.checkIfExists(selCertificate.getId())) {
                Toasty.info(CreateOrderActivity.this, "You already requested this certificate.", Toasty.LENGTH_LONG, true).show();
            } else {
                Intent intent = new Intent(CreateOrderActivity.this, FormAddActivity.class);
                intent.putExtra(Extra.MODEL, selCertificate);
                startActivity(intent);
            }
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
            Toasty.error(this, "Name: Required at least 3 characters", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (Objects.requireNonNull(inputTxtName.getText()).toString().length() > 100){
            layoutName.setErrorEnabled(true);
            layoutName.setError("Required no more than 100 characters");
            Toasty.error(this, "Name: Required no more than 100 characters", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (Objects.requireNonNull(inputTxtLocation.getText()).toString().length() < 3){
            layoutLocation.setErrorEnabled(true);
            layoutLocation.setError("Required at least 3 characters");
            Toasty.error(this, "Location: Required at least 3 characters", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (Objects.requireNonNull(inputTxtLocation.getText()).toString().length() > 200){
            layoutLocation.setErrorEnabled(true);
            layoutLocation.setError("Required no more than 200 characters");
            Toasty.error(this, "Location: Required no more than 200 characters", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (orderType.equals("Delivery")) {
            if (longitude == null || latitude == null) {
                Toasty.error(this, "Please set the location", Toasty.LENGTH_LONG, true).show();
            }
        }

        if (formsAdapter.getItemCount() <= 0) {
            Toasty.error(this, "FORM: Please add a minimum of 1 request of certificate to proceed", Toasty.LENGTH_LONG, true).show();
        }

        /* Email Validation  */
        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(inputTxtEmail.getText()).toString()).matches() || Objects.requireNonNull(inputTxtEmail.getText()).toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Invalid email address");
            Toasty.error(this, "Email: Invalid email address", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        /* Phone Validation */
        if (Objects.requireNonNull(inputTxtPhoneNo.getText()).toString().length() != 11){
            layoutPhoneNo.setErrorEnabled(true);
            layoutPhoneNo.setError("Invalid phone number. Must start at 09*********");
            Toasty.error(this, "Phone: Invalid phone number. Must start at 09*********", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        return true;
    }

    private void submitData() {
        progressDialog.setMessage("Submitting order please wait.....");
        progressDialog.show();

        String name = Objects.requireNonNull(inputTxtName.getText()).toString();
        String homeAddress = Objects.requireNonNull(inputTxtLocation.getText()).toString();
        String email = Objects.requireNonNull(inputTxtEmail.getText()).toString();
        String phoneNo = Objects.requireNonNull(inputTxtPhoneNo.getText()).toString();

        StringRequest request = new StringRequest(Request.Method.POST, Api.ORDERS, response->{
            try {
                JSONObject object = new JSONObject(response);

                JSONObject jsonObject = object.getJSONObject("data");

//                Complaint mComplaint = new Complaint(
//                        complaintJSONObject.getInt("id"), complaintJSONObject.getInt("contact_id"), complaintJSONObject.getString("contact_name"),
//                        new Type(complaintJSONObject.getInt("type_id"), complaintJSONObject.getString("complaint_type")), complaintJSONObject.getString("custom_type"),
//                        complaintJSONObject.getString("reason"), complaintJSONObject.getString("action"), complaintJSONObject.getString("email"),
//                        complaintJSONObject.getString("phone_no"), complaintJSONObject.getString("status"), complaintJSONObject.getString("admin_message"),
//                        complaintJSONObject.getString("created_at"), complaintJSONObject.getString("updated_at")
//                );

                /* Meaning AuthMissingItemFragment Calls this activity */
//                ComplaintFragment.arrayList.add(0,mComplaint);
//                Objects.requireNonNull(ComplaintFragment.recyclerView.getAdapter()).notifyItemInserted(0);
//                ComplaintFragment.recyclerView.getAdapter().notifyDataSetChanged();
                Toasty.success(this, "Your order has been submitted successfully, please wait for the administrator to respond to your order", Toast.LENGTH_LONG, true).show();
                SelectPickupActivity.finishThis();
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        },error ->{
            progressDialog.dismiss();
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
                Toasty.error(this, "Request Timeout", Toast.LENGTH_LONG, true).show();
            }
        } ){

            //add token to headers
            @Override
            public Map<String, String> getHeaders() {
                String token = userPref.getString(Pref.TOKEN,"");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }

            //add params
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> map = new HashMap<>();

                ArrayList<Form> formArrayList = formsAdapter.getList();

                map.put("name", name);
                map.put("email", email);
                map.put("phone_no", phoneNo);
                map.put("pick_up_type", orderType);
                map.put("location_address", homeAddress);

                if (orderType.equals("Delivery")) {
                    map.put("latitude", String.valueOf(latitude));
                    map.put("longitude", String.valueOf(longitude));
                }

                for(int i=0;i<formArrayList.size();i++){
                    map.put("certificate_forms["+i+"][certificate_id]", String.valueOf(formArrayList.get(i).getCertId()));
                    map.put("certificate_forms["+i+"][first_name]", formArrayList.get(i).getFirstName());
                    map.put("certificate_forms["+i+"][middle_name]", formArrayList.get(i).getMiddleName());
                    map.put("certificate_forms["+i+"][last_name]", formArrayList.get(i).getLastName());
                    map.put("certificate_forms["+i+"][address]", formArrayList.get(i).getAddress());

                    // if indigency, cedula, clearance, id
                    if (formArrayList.get(i).getCertId() >= 1 && formArrayList.get(i).getCertId() <= 4) {
                        // civil status, bday, citizenship,
                        map.put("certificate_forms["+i+"][civil_status]", formArrayList.get(i).getCivilStatus());
                        map.put("certificate_forms["+i+"][birthday]", formArrayList.get(i).getBirthday());
                        map.put("certificate_forms["+i+"][citizenship]", formArrayList.get(i).getCitizenship());
                    }

                    // if indigency, clearance
                    if (formArrayList.get(i).getCertId() == 1 || formArrayList.get(i).getCertId() == 3) {
                        // purpose
                        map.put("certificate_forms["+i+"][purpose]", formArrayList.get(i).getPurpose());
                    }
                    // if cedula, id
                    if (formArrayList.get(i).getCertId() == 2 || formArrayList.get(i).getCertId() == 4) {
                        //birthplace
                        map.put("certificate_forms["+i+"][birthplace]", formArrayList.get(i).getBirthplace());
                    }

                    // if cedula
                    if (formArrayList.get(i).getCertId() == 2) {
                        //profession, height, weight, sex, cedulaType,tin, icr
                        map.put("certificate_forms["+i+"][profession]", formArrayList.get(i).getProfession());
                        map.put("certificate_forms["+i+"][height]", String.valueOf(formArrayList.get(i).getHeight()));
                        map.put("certificate_forms["+i+"][weight]", String.valueOf(formArrayList.get(i).getWeight()));
                        map.put("certificate_forms["+i+"][sex]", formArrayList.get(i).getSex());
                        map.put("certificate_forms["+i+"][cedula_type]", formArrayList.get(i).getCedulaType());
                        map.put("certificate_forms["+i+"][tin_no]", formArrayList.get(i).getTinNo());
                        map.put("certificate_forms["+i+"][icr_no]", formArrayList.get(i).getIcrNo());
                    }

                    // if id
                    if (formArrayList.get(i).getCertId() == 2) {
                        // contact no, contact person, relation contact person no
                        map.put("certificate_forms["+i+"][contact_no]", formArrayList.get(i).getSex());
                        map.put("certificate_forms["+i+"][contact_person]", formArrayList.get(i).getCedulaType());
                        map.put("certificate_forms["+i+"][contact_person_no]", formArrayList.get(i).getTinNo());
                        map.put("certificate_forms["+i+"][contact_person_relation]", formArrayList.get(i).getIcrNo());
                    }

                    // if business
                    if (formArrayList.get(i).getCertId() == 5) {
                        // business
                        map.put("certificate_forms["+i+"][business_name]", formArrayList.get(i).getBusinessName());
                    }
                }

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

        RequestQueue queue = Volley.newRequestQueue(CreateOrderActivity.this);
        queue.add(request);
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

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    public void cancelEdit(View view) {
        finish();
    }
}