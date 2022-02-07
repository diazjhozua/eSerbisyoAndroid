package com.example.eserbisyo.ModelActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Adapters.DDTypeAdapter;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.AuthMissingItemFragment;
import com.example.eserbisyo.HomeFragments.AuthMissingPersonFragment;
import com.example.eserbisyo.HomeFragments.ComplaintFragment;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ComplainantsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.ComplaintsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.DefendantsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.EmployeesAdapter;
import com.example.eserbisyo.Models.Complainant;
import com.example.eserbisyo.Models.Complaint;
import com.example.eserbisyo.Models.Defendant;
import com.example.eserbisyo.Models.MissingItem;
import com.example.eserbisyo.Models.MissingPerson;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class ComplaintAddActivity extends AppCompatActivity {

    private TextInputLayout layoutCustomType, layoutReason, layoutAction, layoutEmail, layoutPhone;
    private TextInputEditText inputTxtCustomType, inputTxtReason, inputTxtAction, inputTxtEmail, inputTxtPhone;
    private AppCompatCheckBox chkCustomType;

    private TextView txtAddComplainant, txtAddDefendant;
    public  static TextView txtComplainantCount, txtDefendantCount;
    public static RecyclerView rvComplainant, rvDefendant;
    private SwipeRefreshLayout srlComplainant, srlDefendant;
    private ImageView ivComplainantNoList, ivDefendantNoList;

    private Button btnSubmit;
    private LinearLayoutCompat linearLayoutSelectType;

    private Spinner spnType;

    private  ArrayList<Type> typeArrayList;
    public static ArrayList<Complainant> complainantArrayList;
    public static ArrayList<Defendant> defendantArrayList;
    private ComplainantsAdapter complainantsAdapter;
    private DefendantsAdapter defendantsAdapter;


    private SharedPreferences userPref;
    private ProgressDialog progressDialog;
    public JSONObject errorObj = null;
    private boolean isCustomTypeInput;
    private int selectedTypeId;

    /* Defendant Dialog Variable */
    private Dialog dialogDefendant;
    private ImageView ivDefDiaOperation;
    private TextView txtDefDiaTitle;
    private TextInputLayout layoutDefDiaName;
    private TextInputEditText inputDefDiaName;
    private Button btnDefDiaCancel, btnDefDiaSubmit;

    private Dialog dialogComplainant;
    private ImageView ivCompDiaOperation;
    private TextView txtCompDiaTitle;
    private SignaturePad spCompSignature;
    private TextInputLayout layoutCompDiaName;
    private TextInputEditText inputCompDiaName;
    private Button btnCompDiaCancel, btnCompDiaSubmit;

    private Bitmap signatureBitmap;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint_add);
        init();
    }

    private void init() {
        layoutCustomType = findViewById(R.id.txtLayoutCustomType);
        layoutReason = findViewById(R.id.txtLayoutReason);
        layoutAction = findViewById(R.id.txtLayoutAction);
        layoutEmail = findViewById(R.id.txtLayoutEmail);
        layoutPhone = findViewById(R.id.txtLayoutPhone);

        inputTxtCustomType = findViewById(R.id.inputTxtCustomType);
        inputTxtReason = findViewById(R.id.inputTxtReason);
        inputTxtAction = findViewById(R.id.inputTxtAction);
        inputTxtEmail = findViewById(R.id.inputTxtEmail);
        inputTxtPhone = findViewById(R.id.inputTxtPhone);

        chkCustomType = findViewById(R.id.chkCustomType);

        txtComplainantCount = findViewById(R.id.txtComplainantCount);
        txtDefendantCount = findViewById(R.id.txtDefendantCount);
        txtAddComplainant = findViewById(R.id.txtAddComplainant);
        txtAddDefendant = findViewById(R.id.txtAddDefendant);

        rvComplainant = findViewById(R.id.recyclerViewComplainant);
//        srlComplainant = findViewById(R.id.swipeRecyclerViewComplainant);
        rvDefendant = findViewById(R.id.recyclerViewDefendant);
//        srlDefendant = findViewById(R.id.swipeRecyclerViewDefendant);

        rvDefendant.setHasFixedSize(true);
        rvDefendant.setLayoutManager(new LinearLayoutManager(ComplaintAddActivity.this));

        rvComplainant.setHasFixedSize(true);
        rvComplainant.setLayoutManager(new LinearLayoutManager(ComplaintAddActivity.this));

        btnSubmit = findViewById(R.id.btnSubmit);
        linearLayoutSelectType = findViewById(R.id.layoutSelectType);

        spnType = findViewById(R.id.spinnerType);

        userPref = ComplaintAddActivity.this.getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        complainantArrayList = new ArrayList<>();
        defendantArrayList = new ArrayList<>();

        complainantsAdapter = new ComplainantsAdapter(ComplaintAddActivity.this, complainantArrayList);
        rvComplainant.setAdapter(complainantsAdapter);

        defendantArrayList.add(new Defendant(1, 1, true, true, "Princess Angela Landicho".toString().trim()));
        defendantsAdapter = new DefendantsAdapter(ComplaintAddActivity.this, defendantArrayList);
        rvDefendant.setAdapter(defendantsAdapter);

        inputTxtReason.setText("Gusto po sana magreklamo kasi ang kapit bahay po namin ay ang iingay. Nag report na po ako kaso mas lalong lumala");
        inputTxtAction.setText("Gusto ko po sana mabigyan ng hustisya");
        inputTxtEmail.setText("jhozua@gmail.com");
        inputTxtPhone.setText("09196988952");

        initListeners();

    }

    private void initListeners() {
        btnSubmit.setOnClickListener(v -> {
            // store feedback
            if (validate()) {
                submitData();
            }
        });

        txtAddComplainant.setOnClickListener(v -> {
            openComplainantDialog();
        });

        txtAddDefendant.setOnClickListener(v -> {
            openDefendantDialog();
        });

        chkCustomType.setOnClickListener(view -> {
            if(((CompoundButton) view).isChecked()){
                linearLayoutSelectType.setVisibility(View.GONE);
                layoutCustomType.setVisibility(View.VISIBLE);
                isCustomTypeInput = true;
            } else {
                linearLayoutSelectType.setVisibility(View.VISIBLE);
                layoutCustomType.setVisibility(View.GONE);
                isCustomTypeInput = false;
            }
        });

        inputTxtCustomType.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isCustomTypeInput) {
                    if (Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()>=4 || Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()<=60){
                        layoutCustomType.setErrorEnabled(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtReason.getText()).toString().length()>=4 && Objects.requireNonNull(inputTxtReason.getText()).toString().length()<=500){
                    layoutReason.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        inputTxtAction.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtAction.getText()).toString().length()>=4 && Objects.requireNonNull(inputTxtAction.getText()).toString().length()<=500){
                    layoutAction.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Email Validation
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

        inputTxtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(inputTxtPhone.getText()).toString().isEmpty() && Objects.requireNonNull(inputTxtPhone.getText()).toString().length() == 11){
                    layoutPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        getCreateData();

    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void openDefendantDialog() {
        dialogDefendant = new Dialog(ComplaintAddActivity.this);
        dialogDefendant.setContentView(R.layout.dialog_defendant);
        dialogDefendant.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDefendant.setCancelable(true);

        ivDefDiaOperation = dialogDefendant.findViewById(R.id.ivDialogOperation);
        txtDefDiaTitle = dialogDefendant.findViewById(R.id.txtDialogOperation);
        layoutDefDiaName = dialogDefendant.findViewById(R.id.txtLayoutDialogName);
        inputDefDiaName = dialogDefendant.findViewById(R.id.inputTxtDialogName);
        btnDefDiaCancel = dialogDefendant.findViewById(R.id.btnDialogCancel);
        btnDefDiaSubmit= dialogDefendant.findViewById(R.id.btnDialogSubmit);

        Picasso.get().load(R.drawable.plus).fit().into(ivDefDiaOperation);
        txtDefDiaTitle.setText("Add Defendant");

        btnDefDiaCancel.setOnClickListener(v -> dialogDefendant.dismiss());

        btnDefDiaSubmit.setOnClickListener(v -> {
            if (validateDefendant()) {

                progressDialog.setMessage("Adding defendant.....");
                progressDialog.show();


                Defendant mDefendant = new Defendant(1, 1, true, true, Objects.requireNonNull(inputDefDiaName.getText()).toString().trim());

                defendantArrayList.add(0,mDefendant);
                Objects.requireNonNull(rvDefendant.getAdapter()).notifyItemInserted(0);
                rvDefendant.getAdapter().notifyDataSetChanged();

                Toasty.success(this, "Defendant Added", Toast.LENGTH_LONG, true).show();

                txtDefendantCount.setText("Defendant: " + defendantsAdapter.getItemCount() + " (Total)");

                progressDialog.hide();
                dialogDefendant.hide();
            }
        });

        inputDefDiaName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputDefDiaName.getText()).toString().length()>=5 && Objects.requireNonNull(inputDefDiaName.getText()).toString().length()<=150){
                    layoutDefDiaName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        dialogDefendant.show();
    }

    private boolean validateDefendant() {
        if (Objects.requireNonNull(inputDefDiaName.getText()).toString().isEmpty()) {
            layoutDefDiaName.setErrorEnabled(true);
            layoutDefDiaName.setError("Defendant name is required");
            return false;
        }

        if (inputDefDiaName.getText().length() < 5) {
            layoutDefDiaName.setErrorEnabled(true);
            layoutDefDiaName.setError("Required at least 5");
            return false;
        }

        if (Objects.requireNonNull(inputDefDiaName.getText()).toString().length()> 150){
            layoutDefDiaName.setErrorEnabled(true);
            layoutDefDiaName.setError("Required no more than  150 characters");
            return false;
        }
        return true;
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    private void openComplainantDialog() {
        dialogComplainant = new Dialog(ComplaintAddActivity.this);
        dialogComplainant.setContentView(R.layout.dialog_complainant);
        dialogComplainant.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogComplainant.setCancelable(true);

        signatureBitmap = null;

        ivCompDiaOperation = dialogComplainant.findViewById(R.id.ivDialogOperation);
        txtCompDiaTitle = dialogComplainant.findViewById(R.id.txtDialogOperation);
        layoutCompDiaName = dialogComplainant.findViewById(R.id.txtLayoutDialogName);
        inputCompDiaName = dialogComplainant.findViewById(R.id.inputTxtDialogName);
        spCompSignature = dialogComplainant.findViewById(R.id.spSignature);
        btnCompDiaCancel = dialogComplainant.findViewById(R.id.btnDialogCancel);
        btnCompDiaSubmit = dialogComplainant.findViewById(R.id.btnDialogSubmit);

        Picasso.get().load(R.drawable.plus).fit().into(ivCompDiaOperation);
        txtCompDiaTitle.setText("Add Complainant");

        btnCompDiaCancel.setOnClickListener(v -> dialogComplainant.dismiss());

        dialogComplainant.show();

        btnCompDiaSubmit.setOnClickListener(v -> {
            signatureBitmap = spCompSignature.getSignatureBitmap();

            if (validateComplainant()) {
                progressDialog.setMessage("Adding complainant.....");
                progressDialog.show();

                Complainant mComplainant = new Complainant(true, true, Objects.requireNonNull(inputCompDiaName.getText()).toString().trim(), signatureBitmap);

                complainantArrayList.add(0,mComplainant);
                Objects.requireNonNull(rvComplainant.getAdapter()).notifyItemInserted(0);
                rvComplainant.getAdapter().notifyDataSetChanged();

                Toasty.success(this, "Complainant Added", Toast.LENGTH_LONG, true).show();
                txtComplainantCount.setText("Complainant: " + complainantsAdapter.getItemCount() + " (Total)");
                progressDialog.hide();
                dialogComplainant.hide();
            }
        });

        inputCompDiaName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputCompDiaName.getText()).toString().length()>=5 && Objects.requireNonNull(inputCompDiaName.getText()).toString().length()<=150){
                    layoutCompDiaName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private boolean validateComplainant() {

        if (signatureBitmap == null) {
            Toasty.error(this, "Signature is required", Toasty.LENGTH_LONG, true).show();
            return false;
        }
        if (Objects.requireNonNull(inputCompDiaName.getText()).toString().isEmpty()) {
            layoutCompDiaName.setErrorEnabled(true);
            layoutCompDiaName.setError("Defendant name is required");
            return false;
        }

        if (inputCompDiaName.getText().length() < 5) {
            layoutCompDiaName.setErrorEnabled(true);
            layoutCompDiaName.setError("Required at least 5");
            return false;
        }

        if (Objects.requireNonNull(inputCompDiaName.getText()).toString().length()> 150){
            layoutCompDiaName.setErrorEnabled(true);
            layoutCompDiaName.setError("Required no more than  150 characters");
            return false;
        }
        return true;
    }

    private void getCreateData() {

        progressDialog.setMessage("Loading assets.....");
        progressDialog.show();
        typeArrayList = new ArrayList<>();
        StringRequest request = new StringRequest(Request.Method.GET, Api.COMPLAINTS + Api.CREATE, response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONArray typeArray = new JSONArray(object.getString("types"));
                for (int i = 0; i < typeArray.length(); i++) {
                    JSONObject typeObject = typeArray.getJSONObject(i);
                    Type type = new Type(
                            typeObject.getInt("id"),
                            typeObject.getString("name")
                    );
                    typeArrayList.add(type);
                }

                /* SET Adapters */
                setAdapters();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        },error -> {
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
        }){

            // provide token in header
            @Override
            public Map<String, String> getHeaders() {
                String token = userPref.getString(Pref.TOKEN,"");
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

        RequestQueue queue = Volley.newRequestQueue(ComplaintAddActivity.this);
        queue.add(request);
    }

    private void setAdapters() {
        DDTypeAdapter ddTypeAdapter = new DDTypeAdapter(this, typeArrayList);

        spnType.setAdapter(ddTypeAdapter);

        spnType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Type type = (Type) parent.getSelectedItem();
                selectedTypeId = type.getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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

    private boolean validate() {

        if (isCustomTypeInput){
            if (Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()<4){
                layoutCustomType.setErrorEnabled(true);
                layoutCustomType.setError("Required at least 4 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtCustomType.getText()).toString().length()>60){
                layoutCustomType.setErrorEnabled(true);
                layoutCustomType.setError("Required no more than  60 characters");
                return false;
            }
        }

        if (complainantsAdapter.getItemCount() <= 0) {
            Toasty.error(this, "Complainant is required", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (complainantsAdapter.getItemCount() > 10) {
            Toasty.error(this, "Complainant should be not more than 10 people", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (defendantsAdapter.getItemCount() <= 0) {
            Toasty.error(this, "Complainant is required", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (defendantsAdapter.getItemCount() > 10) {
            Toasty.error(this, "Complainant should be not more than 10 people", Toasty.LENGTH_LONG, true).show();
            return false;
        }

        if (Objects.requireNonNull(inputTxtAction.getText()).toString().length() < 4){
            layoutAction.setErrorEnabled(true);
            layoutAction.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtAction.getText()).toString().length() > 500){
            layoutAction.setErrorEnabled(true);
            layoutAction.setError("Required no more than 500 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtAction.getText()).toString().length() < 4){
            layoutAction.setErrorEnabled(true);
            layoutAction.setError("Required at least 3 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtAction.getText()).toString().length() > 500){
            layoutAction.setErrorEnabled(true);
            layoutAction.setError("Required no more than 500 characters");
            return false;
        }

        /* Email Validation  */
        if (!Patterns.EMAIL_ADDRESS.matcher(Objects.requireNonNull(inputTxtEmail.getText()).toString()).matches() || Objects.requireNonNull(inputTxtEmail.getText()).toString().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Invalid email address");
            return false;
        }

        /* Phone Validation */
        if (Objects.requireNonNull(inputTxtPhone.getText()).toString().length() != 11){
            layoutPhone.setErrorEnabled(true);
            layoutPhone.setError("Invalid phone number. Must start at 0919*******");
            return false;
        }

        return true;
    }

    private void submitData() {
        progressDialog.setMessage("Submitting data please wait.....");
        progressDialog.show();

        int typeID = selectedTypeId;
        String customType = Objects.requireNonNull(inputTxtCustomType.getText()).toString().trim();
        String action = Objects.requireNonNull(inputTxtAction.getText()).toString().trim();
        String reason = Objects.requireNonNull(inputTxtReason.getText()).toString().trim();
        String email = Objects.requireNonNull(inputTxtEmail.getText()).toString().trim();
        String phoneNo = Objects.requireNonNull(inputTxtPhone.getText()).toString().trim();

        StringRequest request = new StringRequest(Request.Method.POST, Api.COMPLAINTS, response->{
            try {
                JSONObject object = new JSONObject(response);

                JSONObject complaintJSONObject = object.getJSONObject("data");

                Complaint mComplaint = new Complaint(
                        complaintJSONObject.getInt("id"), complaintJSONObject.getInt("contact_id"), complaintJSONObject.getString("contact_name"),
                        new Type(complaintJSONObject.getInt("type_id"), complaintJSONObject.getString("complaint_type")), complaintJSONObject.getString("custom_type"),
                        complaintJSONObject.getString("reason"), complaintJSONObject.getString("action"), complaintJSONObject.getString("email"),
                        complaintJSONObject.getString("phone_no"), complaintJSONObject.getString("status"), complaintJSONObject.getString("admin_message"),
                        complaintJSONObject.getString("created_at"), complaintJSONObject.getString("updated_at")
                );

                /* Meaning AuthMissingItemFragment Calls this activity */
                try {
                    ComplaintFragment.arrayList.add(0,mComplaint);
                    Objects.requireNonNull(ComplaintFragment.recyclerView.getAdapter()).notifyItemInserted(0);
                    ComplaintFragment.recyclerView.getAdapter().notifyDataSetChanged();
                    Toasty.success(this, "Your complaint has been submitted successfully, please wait for the administrator to respond to your complaint", Toast.LENGTH_LONG, true).show();
                } catch (Exception ignored) { }

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

                if (isCustomTypeInput) {
                    map.put("custom_type", customType);
                } else {
                    map.put("type_id", String.valueOf(typeID));
                }

                ArrayList<Defendant> defendantArrayList = defendantsAdapter.getList();
                ArrayList<Complainant> complainantArrayList = complainantsAdapter.getList();

                map.put("reason", reason);
                map.put("action", action);
                map.put("email", email);
                map.put("phone_no", phoneNo);

                for(int i=0;i<defendantArrayList.size();i++){
                    map.put("defendant_list["+i+"][name]", defendantArrayList.get(i).getName());
                }

                for(int i=0;i<complainantArrayList.size();i++){
                    map.put("complainant_list["+i+"][name]", complainantArrayList.get(i).getName());
                    map.put("complainant_list["+i+"][signature]", bitmapToString(complainantArrayList.get(i).getBitmapSignature()));
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

        RequestQueue queue = Volley.newRequestQueue(ComplaintAddActivity.this);
        queue.add(request);
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }

        return "";
    }

    public void cancelEdit(View view) {
        finish();
    }
}