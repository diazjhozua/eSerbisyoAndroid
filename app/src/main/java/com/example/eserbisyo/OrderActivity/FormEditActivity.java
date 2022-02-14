package com.example.eserbisyo.OrderActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.ComplaintFragment;
import com.example.eserbisyo.Models.Certificate;
import com.example.eserbisyo.Models.Form;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class FormEditActivity extends AppCompatActivity {
    final Calendar myCalendar= Calendar.getInstance();

    private TextInputLayout layoutFirstName, layoutMiddleName, layoutLastName, layoutAddress, layoutCivilStatus, layoutBirthday,
            layoutCitizenship, layoutPurpose, layoutBirthplace, layoutProfession, layoutHeight, layoutWeight, layoutSex, layoutCedulaType, layoutTinNo,
            layoutIcrNo, layoutContactNo, layoutContactPerson, layoutContactPersonRelation, layoutContactPersonNo, layoutBusinessName;

    private TextInputEditText inputTxtFirstName,  inputTxtMiddleName, inputTxtLastName, inputTxtAddress, inputTxtCitizenship,
            inputTxtPurpose, inputTxtBirthplace, inputTxtProfession, inputTxtHeight, inputTxtWeight, inputTxtTinNo, inputTxtIcrNo, inputTxtContactNo, inputTxtContactPerson,
            inputTxtContactPersonRelation, inputTxtContactPersonNo, inputTxtBusinessName;

    private EditText inputDateBirthday;

    private AutoCompleteTextView autoCompleteCivilStatus, autoCompleteSex, autoCompleteCedulaType;
    private TextView txtTitle;
    private Button btnSubmit;

    private Certificate mCertificate;
    private Form selForm;
    private int formPos;
    private ProgressDialog progressDialog;
    private SharedPreferences userPref;
    private JSONObject errorObj = null;

    private final String[] civilStatusSelector = new String[]{
            "Single", "Married", "Divorced", "Widowed"
    };

    private final String[] sexSelector = new String[]{
            "Male", "Female"
    };

    private final String[] cedulaTypeSelector = new String[]{
            "Individual", "Corporation"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_edit);

        Intent intent = getIntent();
        selForm = intent.getParcelableExtra(Extra.MODEL);
        formPos = intent.getIntExtra(Extra.MODEL_POSITION, 0);
        init();

    }

    private void init() {

        layoutFirstName = findViewById(R.id.txtLayoutFirstName);
        layoutMiddleName = findViewById(R.id.txtLayoutMiddleName);
        layoutLastName = findViewById(R.id.txtLayoutLastName);
        layoutAddress = findViewById(R.id.txtLayoutAddress);
        layoutCivilStatus = findViewById(R.id.txtLayoutCivilStatus);
        layoutBirthday = findViewById(R.id.txtLayoutBirthday);
        layoutCitizenship = findViewById(R.id.txtLayoutCitizenship);
        layoutPurpose = findViewById(R.id.txtLayoutPurpose);
        layoutBirthplace = findViewById(R.id.txtLayoutBirthplace);
        layoutProfession = findViewById(R.id.txtLayoutProfession);
        layoutHeight = findViewById(R.id.txtLayoutHeight);
        layoutWeight = findViewById(R.id.txtLayoutWeight);
        layoutSex = findViewById(R.id.txtLayoutSex);
        layoutCedulaType = findViewById(R.id.txtLayoutCedulaType);
        layoutTinNo = findViewById(R.id.txtLayoutTinNo);
        layoutIcrNo = findViewById(R.id.txtLayoutIcrNo);
        layoutContactNo = findViewById(R.id.txtLayoutContactNo);
        layoutContactPerson = findViewById(R.id.txtLayoutContactPersonName);
        layoutContactPersonRelation = findViewById(R.id.txtLayoutContactPersonRelation);
        layoutContactPersonNo = findViewById(R.id.txtLayoutContactPersonNo);
        layoutBusinessName = findViewById(R.id.txtLayoutBusinessName);

        inputTxtFirstName = findViewById(R.id.inputTxtFirstName);
        inputTxtMiddleName = findViewById(R.id.inputTxMiddleName);
        inputTxtLastName = findViewById(R.id.inputTxtLastName);
        inputTxtAddress = findViewById(R.id.inputTxtAddress);
        inputDateBirthday = findViewById(R.id.inputTxtBirthday);
        inputTxtCitizenship = findViewById(R.id.inputTxtCitizenship);
        inputTxtPurpose = findViewById(R.id.inputTxtPurpose);
        inputTxtBirthplace = findViewById(R.id.inputTxtBirthplace);
        inputTxtProfession = findViewById(R.id.inputTxtProfession);
        inputTxtHeight = findViewById(R.id.inputTxtHeight);
        inputTxtWeight = findViewById(R.id.inputTxtWeight);
        inputTxtTinNo = findViewById(R.id.inputTxtTinNo);
        inputTxtIcrNo = findViewById(R.id.inputTxtIcrNo);
        inputTxtContactNo = findViewById(R.id.inputTxtContactNo);
        inputTxtContactPerson = findViewById(R.id.inputTxtContactPersonName);
        inputTxtContactPersonRelation = findViewById(R.id.inputTxtContactPersonRelation);
        inputTxtContactPersonNo = findViewById(R.id.inputTxtContactPersonNo);
        inputTxtBusinessName = findViewById(R.id.inputTxtBusinessName);

        autoCompleteCivilStatus = findViewById(R.id.autoCompleteCivilStatus);
        autoCompleteSex = findViewById(R.id.autoCompleteSex);
        autoCompleteCedulaType = findViewById(R.id.autoCompleteCedulaType);

        txtTitle = findViewById(R.id.txtTitle);

        btnSubmit = findViewById(R.id.btnSubmit);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        inputDateBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(FormEditActivity.this, R.style.MyDatePickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH,monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        updateDateLabel();
                    }
                }, 2015, 02, 26).show();
            }
        });

        hideUnnecessaryInput();
    }

    private void updateDateLabel() {
        String myFormat="yyyy-dd-MM";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.ROOT);
        inputDateBirthday.setText(dateFormat.format(myCalendar.getTime()));
    }

    private void hideUnnecessaryInput() {
        switch(selForm.getCertId()) {
            case 1:
            case 3:
                // For clearance and indigency
                layoutBirthplace.setVisibility(View.GONE);
                layoutProfession.setVisibility(View.GONE);
                layoutHeight.setVisibility(View.GONE);
                layoutWeight.setVisibility(View.GONE);
                layoutSex.setVisibility(View.GONE);
                layoutCedulaType.setVisibility(View.GONE);
                layoutTinNo.setVisibility(View.GONE);
                layoutIcrNo.setVisibility(View.GONE);
                layoutContactNo.setVisibility(View.GONE);
                layoutContactPerson.setVisibility(View.GONE);
                layoutContactPersonRelation.setVisibility(View.GONE);
                layoutContactPersonNo.setVisibility(View.GONE);
                layoutBusinessName.setVisibility(View.GONE);
                break;
            case 2:
                // for cedula
                layoutPurpose.setVisibility(View.GONE);
                layoutContactNo.setVisibility(View.GONE);
                layoutContactPerson.setVisibility(View.GONE);
                layoutContactPersonRelation.setVisibility(View.GONE);
                layoutContactPersonNo.setVisibility(View.GONE);
                layoutBusinessName.setVisibility(View.GONE);

                break;
            case 4:
                // for id
                layoutPurpose.setVisibility(View.GONE);
                layoutProfession.setVisibility(View.GONE);
                layoutHeight.setVisibility(View.GONE);
                layoutWeight.setVisibility(View.GONE);
                layoutSex.setVisibility(View.GONE);
                layoutCedulaType.setVisibility(View.GONE);
                layoutTinNo.setVisibility(View.GONE);
                layoutIcrNo.setVisibility(View.GONE);
                layoutBusinessName.setVisibility(View.GONE);
                break;
            case 5:
                // for business clearance
                layoutPurpose.setVisibility(View.GONE);
                layoutBirthplace.setVisibility(View.GONE);
                layoutProfession.setVisibility(View.GONE);
                layoutHeight.setVisibility(View.GONE);
                layoutWeight.setVisibility(View.GONE);
                layoutSex.setVisibility(View.GONE);
                layoutCedulaType.setVisibility(View.GONE);
                layoutTinNo.setVisibility(View.GONE);
                layoutIcrNo.setVisibility(View.GONE);
                layoutContactNo.setVisibility(View.GONE);
                layoutContactPerson.setVisibility(View.GONE);
                layoutContactPersonRelation.setVisibility(View.GONE);
                layoutContactPersonNo.setVisibility(View.GONE);
        }
        setData();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {

        ArrayAdapter<String> adapterCivilStatus = new ArrayAdapter<>(
                FormEditActivity.this,
                R.layout.list_item,
                civilStatusSelector
        );

        ArrayAdapter<String> adapterSex = new ArrayAdapter<>(
                FormEditActivity.this,
                R.layout.list_item,
                sexSelector
        );

        ArrayAdapter<String> adapterCedulaType = new ArrayAdapter<>(
                FormEditActivity.this,
                R.layout.list_item,
                cedulaTypeSelector
        );

        autoCompleteCivilStatus.setAdapter(adapterCivilStatus);
        autoCompleteSex.setAdapter(adapterSex);
        autoCompleteCedulaType.setAdapter(adapterCedulaType);

        txtTitle.setText("EDIT " + selForm.getCertName().toUpperCase(Locale.ROOT));

        inputTxtFirstName.setText(selForm.getFirstName());
        inputTxtMiddleName.setText(selForm.getMiddleName());
        inputTxtLastName.setText(selForm.getLastName());
        inputTxtAddress.setText(selForm.getAddress());
        inputDateBirthday.setText(selForm.getBirthday());
        inputTxtCitizenship.setText(selForm.getCitizenship());
        inputTxtPurpose.setText(selForm.getPurpose());
        inputTxtBirthplace.setText(selForm.getBirthplace());
        inputTxtProfession.setText(selForm.getProfession());

        try {
            inputTxtHeight.setText(selForm.getHeight().toString());
        } catch (NullPointerException ignored) { }

        try {
            inputTxtWeight.setText(selForm.getWeight().toString());
        } catch (NullPointerException ignored) { }

        inputTxtTinNo.setText(selForm.getTinNo());
        inputTxtIcrNo.setText(selForm.getIcrNo());
        inputTxtContactNo.setText(selForm.getPhoneNo());
        inputTxtContactPerson.setText(selForm.getContactPerson());
        inputTxtContactPersonRelation.setText(selForm.getContactPersonRelation());
        inputTxtContactPersonNo.setText(selForm.getContactPersonPhoneNo());
        inputTxtBusinessName.setText(selForm.getBusinessName());

        try {
            switch (selForm.getCivilStatus()) {
                case "Single":
                    autoCompleteCivilStatus.setSelection(0);
                    break;
                case "Married":
                    autoCompleteCivilStatus.setSelection(1);
                    break;
                case "Divorced":
                    autoCompleteCivilStatus.setSelection(2);
                    break;
                case "Widowed":
                    autoCompleteCivilStatus.setSelection(3);
                    break;
            }
        } catch (NullPointerException ignored) { }


        try {
            switch (selForm.getSex()) {
                case "Male":
                    autoCompleteSex.setSelection(0);
                    break;
                case "Female":
                    autoCompleteSex.setSelection(1);
                    break;
            }
        } catch (NullPointerException ignored) { }

        try {
            switch (selForm.getCedulaType()) {
                case "Individual":
                    autoCompleteCedulaType.setSelection(0);
                    break;
                case "Corporation":
                    autoCompleteCedulaType.setSelection(1);
                    break;
            }
        } catch (NullPointerException ignored) { }




        initListener();
    }

    private void initListener() {
        btnSubmit.setOnClickListener(v -> {
            if (validate()) {
                submitData();
            }
        });

        /* First Name Listener */
        inputTxtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtFirstName.getText()).toString().length() >= 4 &&
                        Objects.requireNonNull(inputTxtFirstName.getText()).toString().length() <= 150){
                    layoutFirstName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* Middle Name Listener */
        inputTxtMiddleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtMiddleName.getText()).toString().length() >= 4 &&
                        Objects.requireNonNull(inputTxtMiddleName.getText()).toString().length() <= 150){
                    layoutMiddleName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* Last Name Listener */
        inputTxtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtLastName.getText()).toString().length() >= 4 &&
                        Objects.requireNonNull(inputTxtLastName.getText()).toString().length() <= 150){
                    layoutLastName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* Address Listener */
        inputTxtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputTxtAddress.getText()).toString().length() >= 4 &&
                        Objects.requireNonNull(inputTxtAddress.getText()).toString().length() <= 100){
                    layoutAddress.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // if indigency, cedula, clearance, id
        if (selForm.getCertId() >= 1 && selForm.getCertId() <= 4 ) {
            /* Civil Status Listener */
            autoCompleteCivilStatus.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!Objects.requireNonNull(autoCompleteCivilStatus.getText()).toString().isEmpty()){
                        layoutCivilStatus.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            /* Birthday Listener */
            inputDateBirthday.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!Objects.requireNonNull(inputDateBirthday.getText()).toString().isEmpty()){
                        layoutBirthday.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            /* Citizenship Listener */
            inputTxtCitizenship.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Objects.requireNonNull(inputTxtCitizenship.getText()).toString().length() >= 4 &&
                            Objects.requireNonNull(inputTxtCitizenship.getText()).toString().length() <= 50){
                        layoutCitizenship.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        // if indigency, clearance
        if (selForm.getCertId() == 1 || selForm.getCertId() == 3) {
            /* Purpose Listener */
            inputTxtPurpose.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Objects.requireNonNull(inputTxtPurpose.getText()).toString().length() >= 4 &&
                            Objects.requireNonNull(inputTxtPurpose.getText()).toString().length() <= 150){
                        layoutPurpose.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        // if cedula, id
        if (selForm.getCertId() == 2 || selForm.getCertId() == 4) {
            /* Birthplace Listener */
            inputTxtBirthplace.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Objects.requireNonNull(inputTxtBirthplace.getText()).toString().length() >= 4 &&
                            Objects.requireNonNull(inputTxtBirthplace.getText()).toString().length() <= 150){
                        layoutBirthplace.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        // if cedula
        if (selForm.getCertId() == 2) {
            /* Profession Listener */
            inputTxtProfession.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Objects.requireNonNull(inputTxtProfession.getText()).toString().length() >= 4 &&
                            Objects.requireNonNull(inputTxtProfession.getText()).toString().length() <= 50){
                        layoutProfession.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /* Height Listener */
            inputTxtHeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Double.parseDouble(inputTxtHeight.getText().toString().trim()) >= 1 &&
                            Double.parseDouble(inputTxtHeight.getText().toString().trim()) <= 10.99){
                        layoutHeight.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /* Weight Listener */
            inputTxtWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Double.parseDouble(inputTxtWeight.getText().toString().trim()) >= 1 &&
                            Double.parseDouble(inputTxtWeight.getText().toString().trim()) <= 200.99){
                        layoutWeight.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /* Sex Listener */
            autoCompleteSex.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!Objects.requireNonNull(autoCompleteSex.getText()).toString().isEmpty()){
                        layoutSex.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            /* Cedula type Listener */
            autoCompleteCedulaType.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!Objects.requireNonNull(autoCompleteCedulaType.getText()).toString().isEmpty()){
                        layoutCedulaType.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) { }
            });

            /* Tin No Listener */
            inputTxtTinNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Objects.requireNonNull(inputTxtTinNo.getText()).toString().isEmpty() || Objects.requireNonNull(inputTxtTinNo.getText()).toString().length() == 9){
                        layoutTinNo.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /* Icr No Listener */
            inputTxtIcrNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Objects.requireNonNull(inputTxtIcrNo.getText()).toString().isEmpty() || Objects.requireNonNull(inputTxtIcrNo.getText()).toString().length() == 9){
                        layoutIcrNo.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        // if id
        if (selForm.getCertId() == 4) {
            /* ContactNo Listener */
            inputTxtContactNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!Objects.requireNonNull(inputTxtContactNo.getText()).toString().isEmpty() && Objects.requireNonNull(inputTxtContactNo.getText()).toString().length() == 11){
                        layoutContactNo.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /* Contact Name Listener */
            inputTxtContactPerson.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Objects.requireNonNull(inputTxtContactPerson.getText()).toString().length() >= 3 &&
                            Objects.requireNonNull(inputTxtContactPerson.getText()).toString().length() <= 100){
                        layoutContactPerson.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /* Contact Relation Listener */
            inputTxtContactPersonRelation.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Objects.requireNonNull(inputTxtContactPersonRelation.getText()).toString().length() >= 3 &&
                            Objects.requireNonNull(inputTxtContactPersonRelation.getText()).toString().length() <= 100){
                        layoutContactPerson.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            /* Contact Person No Listener */
            inputTxtContactPersonNo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!Objects.requireNonNull(inputTxtContactPersonNo.getText()).toString().isEmpty() && Objects.requireNonNull(inputTxtContactPersonNo.getText()).toString().length() == 11){
                        layoutContactPersonNo.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            // if business
            if (selForm.getCertId() == 5) {
                /* Contact Relation Listener */
                inputTxtBusinessName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (Objects.requireNonNull(inputTxtBusinessName.getText()).toString().length() >= 3 &&
                                Objects.requireNonNull(inputTxtBusinessName.getText()).toString().length() <= 150){
                            layoutBusinessName.setErrorEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        }
    }

    private boolean validate() {
        /* First Name Validate */
        if (Objects.requireNonNull(inputTxtFirstName.getText()).toString().length() < 4){
            layoutFirstName.setErrorEnabled(true);
            layoutFirstName.setError("Required at least 4 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtFirstName.getText()).toString().length() > 150){
            layoutFirstName.setErrorEnabled(true);
            layoutFirstName.setError("Required no more than  150 characters");
            return false;
        }

        /* Middle Name Validate */
        if (Objects.requireNonNull(inputTxtMiddleName.getText()).toString().length() < 4){
            layoutMiddleName.setErrorEnabled(true);
            layoutMiddleName.setError("Required at least 4 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtMiddleName.getText()).toString().length() > 150){
            layoutMiddleName.setErrorEnabled(true);
            layoutMiddleName.setError("Required no more than  150 characters");
            return false;
        }

        /* Last Name Validate */
        if (Objects.requireNonNull(inputTxtLastName.getText()).toString().length() < 4){
            layoutLastName.setErrorEnabled(true);
            layoutLastName.setError("Required at least 4 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtLastName.getText()).toString().length() > 150){
            layoutLastName.setErrorEnabled(true);
            layoutLastName.setError("Required no more than  150 characters");
            return false;
        }

        /* Address Validate */
        if (Objects.requireNonNull(inputTxtAddress.getText()).toString().length() < 4){
            layoutAddress.setErrorEnabled(true);
            layoutAddress.setError("Required at least 4 characters");
            return false;
        }

        if (Objects.requireNonNull(inputTxtAddress.getText()).toString().length() > 100){
            layoutAddress.setErrorEnabled(true);
            layoutAddress.setError("Required no more than  100 characters");
            return false;
        }

        // if indigency, cedula, clearance, id
        if (selForm.getCertId() >= 1 && selForm.getCertId() <= 4 ) {
            /* Civil Status Validate */
            if (autoCompleteCivilStatus.getText().toString().isEmpty()){
                layoutCivilStatus.setErrorEnabled(true);
                layoutCivilStatus.setError("Civil status is required");
                return false;
            }

            /* Birthday Validate */
            if (inputDateBirthday.getText().toString().isEmpty()){
                layoutBirthday.setErrorEnabled(true);
                layoutBirthday.setError("Birthday is required");
                return false;
            }

            /* Citizenship Validate */
            if (Objects.requireNonNull(inputTxtCitizenship.getText()).toString().length() < 4){
                layoutCitizenship.setErrorEnabled(true);
                layoutCitizenship.setError("Required at least 4 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtCitizenship.getText()).toString().length() > 50){
                layoutCitizenship.setErrorEnabled(true);
                layoutCitizenship.setError("Required no more than  50 characters");
                return false;
            }
        }

        // if indigency, clearance
        if (selForm.getCertId() == 1 || selForm.getCertId() == 3) {
            /* Purpose Validate */
            if (Objects.requireNonNull(inputTxtPurpose.getText()).toString().length() < 4){
                layoutPurpose.setErrorEnabled(true);
                layoutPurpose.setError("Required at least 4 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtPurpose.getText()).toString().length() > 150){
                layoutPurpose.setErrorEnabled(true);
                layoutPurpose.setError("Required no more than  150 characters");
                return false;
            }
        }

        // if cedula, id
        if (selForm.getCertId() == 2 || selForm.getCertId() == 4) {
            /* Birthplace Validate */
            if (Objects.requireNonNull(inputTxtBirthplace.getText()).toString().length() < 4){
                layoutBirthplace.setErrorEnabled(true);
                layoutBirthplace.setError("Required at least 4 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtBirthplace.getText()).toString().length() > 150){
                layoutBirthplace.setErrorEnabled(true);
                layoutBirthplace.setError("Required no more than  150 characters");
                return false;
            }
        }

        // if cedula
        if (selForm.getCertId() == 2) {
            /* Profession Validate */
            if (Objects.requireNonNull(inputTxtProfession.getText()).toString().length() < 4){
                layoutProfession.setErrorEnabled(true);
                layoutProfession.setError("Required at least 4 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtProfession.getText()).toString().length() > 50){
                layoutProfession.setErrorEnabled(true);
                layoutProfession.setError("Required no more than  50 characters");
                return false;
            }

            /* Height Validate */
            if (inputTxtHeight.getText().toString().isEmpty()){
                layoutHeight.setErrorEnabled(true);
                layoutHeight.setError("Height is required");
                return false;
            }

            if (Double.parseDouble(inputTxtHeight.getText().toString().trim()) < 1){
                layoutHeight.setErrorEnabled(true);
                layoutHeight.setError("Required at last minimum 1 value");
                return false;
            }

            if (Double.parseDouble(inputTxtHeight.getText().toString().trim()) > 10.99){
                layoutHeight.setErrorEnabled(true);
                layoutHeight.setError("Required no more than 10.99 value");
                return false;
            }

            /* Weight Validate */
            if (inputTxtWeight.getText().toString().isEmpty()){
                layoutWeight.setErrorEnabled(true);
                layoutWeight.setError("Weight is required");
                return false;
            }

            if (Double.parseDouble(inputTxtWeight.getText().toString().trim()) < 1){
                layoutWeight.setErrorEnabled(true);
                layoutWeight.setError("Required at last minimum 1 value");
                return false;
            }

            if (Double.parseDouble(inputTxtWeight.getText().toString().trim()) > 200.99){
                layoutWeight.setErrorEnabled(true);
                layoutWeight.setError("Required no more than 200.99 value");
                return false;
            }

            /* Sex Validate */
            if (autoCompleteSex.getText().toString().isEmpty()){
                layoutSex.setErrorEnabled(true);
                layoutSex.setError("Sex is required");
                return false;
            }

            /* Cedula Type Validate */
            if (autoCompleteCedulaType.getText().toString().isEmpty()){
                layoutCedulaType.setErrorEnabled(true);
                layoutCedulaType.setError("Cedula type is required");
                return false;
            }

            /* Tin Validation */
            if (!Objects.requireNonNull(inputTxtTinNo.getText()).toString().isEmpty() && Objects.requireNonNull(inputTxtTinNo.getText()).toString().length() != 9){
                layoutTinNo.setErrorEnabled(true);
                layoutTinNo.setError("Invalid tin number. Tin number must be a 9 digit number ");
                return false;
            }

            /* Icr Validation */
            if (!Objects.requireNonNull(inputTxtIcrNo.getText()).toString().isEmpty() && Objects.requireNonNull(inputTxtIcrNo.getText()).toString().length() != 9){
                layoutIcrNo.setErrorEnabled(true);
                layoutIcrNo.setError("Invalid icr number. ICR number must be a 9 digit number ");
                return false;
            }
        }

        // if id
        if (selForm.getCertId() == 4) {
            /* ContactNo Validation */
            if (Objects.requireNonNull(inputTxtContactNo.getText()).toString().length() != 11){
                layoutContactNo.setErrorEnabled(true);
                layoutContactNo.setError("Invalid phone number. Must start at 0919*******");
                return false;
            }

            /* Contact Person Validate */
            if (Objects.requireNonNull(inputTxtContactPerson.getText()).toString().length() < 3){
                layoutContactPerson.setErrorEnabled(true);
                layoutContactPerson.setError("Required at least 3 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtContactPerson.getText()).toString().length() > 100){
                layoutContactPerson.setErrorEnabled(true);
                layoutContactPerson.setError("Required no more than  100 characters");
                return false;
            }

            /* Contact Person Relation */
            if (Objects.requireNonNull(inputTxtContactPersonRelation.getText()).toString().length() < 3){
                layoutContactPersonRelation.setErrorEnabled(true);
                layoutContactPersonRelation.setError("Required at least 3 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtContactPersonRelation.getText()).toString().length() > 100){
                layoutContactPersonRelation.setErrorEnabled(true);
                layoutContactPersonRelation.setError("Required no more than  100 characters");
                return false;
            }

            /* ContactPersonNo Validation */
            if (Objects.requireNonNull(inputTxtContactPersonNo.getText()).toString().length() != 11){
                layoutContactPersonNo.setErrorEnabled(true);
                layoutContactPersonNo.setError("Invalid phone number. Must start at 0919*******");
                return false;
            }
        }

        // if business
        if (selForm.getCertId() == 5) {
            /* Business Name Validate */
            if (Objects.requireNonNull(inputTxtBusinessName.getText()).toString().length() < 4){
                layoutBusinessName.setErrorEnabled(true);
                layoutBusinessName.setError("Required at least 4 characters");
                return false;
            }

            if (Objects.requireNonNull(inputTxtBusinessName.getText()).toString().length() > 150){
                layoutBusinessName.setErrorEnabled(true);
                layoutBusinessName.setError("Required no more than  150 characters");
                return false;
            }
        }

        return true;
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void submitData() {

        String firstName = Objects.requireNonNull(inputTxtFirstName.getText()).toString().trim();
        String middleName = Objects.requireNonNull(inputTxtMiddleName.getText()).toString().trim();
        String lastName = Objects.requireNonNull(inputTxtLastName.getText()).toString().trim();
        String address =  Objects.requireNonNull(inputTxtAddress.getText()).toString().trim();
        String civilStatus = Objects.requireNonNull(autoCompleteCivilStatus.getText()).toString().trim();
        String birthday = Objects.requireNonNull(inputDateBirthday.getText()).toString().trim();
        String citizenship = Objects.requireNonNull(inputTxtCitizenship.getText()).toString().trim();
        String purpose = Objects.requireNonNull(inputTxtPurpose.getText()).toString().trim();
        String birthplace = Objects.requireNonNull(inputTxtBirthplace.getText()).toString().trim();
        String profession = Objects.requireNonNull(inputTxtProfession.getText()).toString().trim();

        Double weight;
        try {
            weight = Double.parseDouble(Objects.requireNonNull(inputTxtWeight.getText()).toString().trim());
        } catch (NumberFormatException e) {
            weight = null;
        }

        Double height;
        try {
            height = Double.parseDouble(Objects.requireNonNull(inputTxtHeight.getText()).toString().trim());
        } catch (NumberFormatException e) {
            height = null;
        }

        String cedulaType = Objects.requireNonNull(autoCompleteCedulaType.getText()).toString().trim();
        String sex = Objects.requireNonNull(autoCompleteSex.getText()).toString().trim();
        String tinNo = Objects.requireNonNull(inputTxtTinNo.getText()).toString().trim();
        String icrNo = Objects.requireNonNull(inputTxtIcrNo.getText()).toString().trim();
        String contactNo = Objects.requireNonNull(inputTxtContactNo.getText()).toString().trim();
        String contactPerson = Objects.requireNonNull(inputTxtContactPerson.getText()).toString().trim();
        String contactPersonRelation = Objects.requireNonNull(inputTxtContactPersonRelation.getText()).toString().trim();
        String contactPersonNo = Objects.requireNonNull(inputTxtContactPersonNo.getText()).toString().trim();
        String businessName = Objects.requireNonNull(inputTxtBusinessName.getText()).toString().trim();

        Form mForm = new Form(
                0, selForm.getCertId(), selForm.getCertName(), selForm.getCertPrice(), firstName, middleName, lastName,
                address, civilStatus, birthday, citizenship, purpose, businessName, birthplace, height, weight, profession,
                cedulaType, sex, tinNo, icrNo, contactNo, contactPerson, contactPersonNo, contactPersonRelation, true
        );

        CreateOrderActivity.formArrayList.set(formPos,mForm);
        Objects.requireNonNull(CreateOrderActivity.recyclerView.getAdapter()).notifyItemInserted(formPos);
        CreateOrderActivity.recyclerView.getAdapter().notifyDataSetChanged();
        Toasty.success(this, "Form updated in the order", Toast.LENGTH_LONG, true).show();

        finish();
    }

    public void cancelEdit(View view) {
        finish();
    }
}