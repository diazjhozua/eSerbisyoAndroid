package com.example.eserbisyo.AuthFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
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
import com.example.eserbisyo.R;
import com.example.eserbisyo.UserInfoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class LoginFragment extends Fragment {

    private View view;
    private TextInputLayout layoutEmail,layoutPassword;
    private TextInputEditText txtEmail,txtPassword;
    private ProgressDialog dialog;
    private AppCompatCheckBox chkRememberMe;

    public JSONObject errorObj = null;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        init();
        return view;
    }

    private void init() {
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignIn);
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignIn);

        txtEmail = view.findViewById(R.id.txtEmailSignIn);
        txtPassword = view.findViewById(R.id.txtPasswordSignIn);
        TextView txtForgotPass = view.findViewById(R.id.txtForgotPass);

        Button btnLogin = view.findViewById(R.id.btnLogin);
        Button btnGoToRegister = view.findViewById(R.id.btnGoToRegister);

        chkRememberMe = view.findViewById(R.id.chkRememberMe);

        btnGoToRegister.setOnClickListener(v -> requireActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer,new RegisterFragment()).commit());

        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);

        btnLogin.setOnClickListener(v -> {
            // validate fields first
            if (validate()) {
                login();
            }
        });

        // Forgot Password
        txtForgotPass.setOnClickListener(v -> {
            // validate fields first
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Api.URL + "forget-password"));
            startActivity(browserIntent);
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(txtEmail.getText()).toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString()).matches()){
                    layoutEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(txtPassword.getText()).toString().length()>7){
                    layoutPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validate() {
        if (Objects.requireNonNull(txtEmail.getText()).toString().trim().isEmpty()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Email is Required");
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(txtEmail.getText().toString().trim()).matches()){
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError("Invalid email address");
            return false;
        }

        if (Objects.requireNonNull(txtPassword.getText()).toString().length()<8){
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError("Required at least 8 characters");
            return false;
        }
        return true;
    }

    private void login() {
        dialog.setMessage("Logging in");
        dialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, Api.LOGIN, response -> {
            //we get response if connection success
            try {
                JSONObject object = new JSONObject(response);

                JSONObject user = object.getJSONObject("user");

                String message = object.getString("message");
                SharedPreferences userPref = requireActivity().getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = userPref.edit();

                if (chkRememberMe.isChecked()) {
                    editor.putBoolean(Pref.IS_REMEMBER,true);
                    editor.putString(Pref.EMAIL, user.getString("email"));
                    editor.putString(Pref.PASSWORD, Objects.requireNonNull(txtPassword.getText()).toString().trim());
                    editor.apply();
                }

                //make shared preference user
                if (user.isNull("first_name")) {
                    editor.putString(Pref.TOKEN, object.getString("access_token"));
                    editor.apply();
                    startActivity(new Intent(((AuthActivity)getContext()), UserInfoActivity.class));
                } else {
                    editor.putInt(Pref.ID, user.getInt("id"));
                    editor.putInt(Pref.USER_ROLE_ID, user.getInt("user_role_id"));
                    editor.putString(Pref.FIRST_NAME, user.getString("first_name"));
                    editor.putString(Pref.MIDDLE_NAME, user.getString("middle_name"));
                    editor.putString(Pref.LAST_NAME, user.getString("last_name"));
                    editor.putString(Pref.ADDRESS, user.getString("address"));
                    editor.putString(Pref.PICTURE, user.getString("file_path"));
                    editor.putString(Pref.TOKEN, object.getString("access_token"));
                    editor.putString(Pref.STATUS, user.getString("status"));
                    editor.putBoolean(Pref.IS_VERIFIED, user.getBoolean("is_verified"));
//                    editor.putBoolean(Pref.IS_VERIFIED, true);
                    editor.putInt(Pref.USER_ROLE_ID, user.getInt("user_role_id"));

                    editor.apply();

                    Toasty.success(requireContext(), message, Toast.LENGTH_LONG, true).show();

                    startActivity(new Intent(((AuthActivity)getContext()), HomeActivity.class));
                }

                ((AuthActivity) requireContext()).finish();


            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        },error -> {
            dialog.dismiss();

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
            // add parameters
            @Override
            protected Map<String, String> getParams() {
                HashMap<String,String> map = new HashMap<>();
                map.put("email", Objects.requireNonNull(txtEmail.getText()).toString().trim());
                map.put("password", Objects.requireNonNull(txtPassword.getText()).toString().trim());
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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        //add this request to request queue
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(request);
    }
}