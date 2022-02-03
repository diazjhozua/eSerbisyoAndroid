package com.example.eserbisyo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.AccountActivities.ChangeEmailActivity;
import com.example.eserbisyo.AccountActivities.ChangePasswordActivity;
import com.example.eserbisyo.AccountActivities.ProfileActivity;
import com.example.eserbisyo.AccountActivities.UserVerificationActivity;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.AnnouncementFragment;
import com.example.eserbisyo.HomeFragments.AuthMissingItemFragment;
import com.example.eserbisyo.HomeFragments.AuthMissingPersonFragment;
import com.example.eserbisyo.HomeFragments.FeedbackFragment;
import com.example.eserbisyo.HomeFragments.MainFragment;
import com.example.eserbisyo.HomeFragments.OrderFragment;
import com.example.eserbisyo.HomeFragments.ReportFragment;
import com.example.eserbisyo.HomeFragments.RequirementFragment;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private SharedPreferences userPref;
    private Dialog dialog;
    private ProgressDialog progressDialog;

    private JSONObject errorObj = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        /* Hooks */
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);

        TextView txtUsername = headerView.findViewById(R.id.txtUserName);
        TextView txtVerifiedStatus = headerView.findViewById(R.id.txtVerifiedStatus);
        ImageView ivVerifiedStatus = headerView.findViewById(R.id.ivVerifiedStatus);
        CircleImageView ivUser = headerView.findViewById(R.id.ivUser);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);


        userPref = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
        String userPicture = userPref.getString(Pref.PICTURE, "");
        String firstName = userPref.getString(Pref.FIRST_NAME, "");
        String lastName = userPref.getString(Pref.LAST_NAME, "");
        int isVerified =  userPref.getInt(Pref.IS_VERIFIED, 0);
        String verifiedStatus = (isVerified == 1) ? "Verified" : "Not Verified";

        String fullName = firstName + " " + lastName;

        Picasso.get().load(Api.STORAGE + userPicture).fit().error(R.drawable.cupang).into(ivUser);
        txtUsername.setText(fullName);
        txtVerifiedStatus.setText(verifiedStatus);

        txtVerifiedStatus.setOnClickListener(v->{
            // validate fields
            if(isVerified == 1){
                Toasty.info(this, "You are already verified hence, no need to see your current requests", Toast.LENGTH_SHORT, true).show();

            } else {
                loadUserVerification();
            }
        });

        if (isVerified == 1) {
            Picasso.get().load(R.drawable.ic_baseline_check_circle_24).fit().error(R.drawable.ic_baseline_check_circle_24).into(ivVerifiedStatus);
        } else {
            Picasso.get().load(R.drawable.ic_baseline_radio_button_unchecked_24).fit().error(R.drawable.ic_baseline_check_circle_24).into(ivVerifiedStatus);
        }

        Toolbar toolbar = findViewById(R.id.toolbar);

        /* Toolbar*/
        setSupportActionBar(toolbar);

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        /* Navigation Drawer Menu */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.frameHomeContainer, new MainFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            switchFragment(new MainFragment());
        } else if (id == R.id.nav_feedback) {
            switchFragment(new FeedbackFragment());
        } else if (id == R.id.nav_order) {
            navigationView.setCheckedItem(R.id.nav_order);
            switchFragment(new OrderFragment());
        } else if (id == R.id.nav_report) {
            switchFragment(new ReportFragment());
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
        } else if (id == R.id.nav_requirement) {
            switchFragment(new RequirementFragment());
        } else if (id == R.id.nav_my_missing_item) {
            switchFragment(new AuthMissingItemFragment());
        }else if (id == R.id.nav_my_missing_person) {
            switchFragment(new AuthMissingPersonFragment());
        } else if (id == R.id.nav_change_email) {
            startActivity(new Intent(this, ChangeEmailActivity.class));
        } else if (id == R.id.nav_change_password) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        }  else if (id == R.id.nav_logout) {
            openLogoutDialog();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setHomeNavCheck() {
        navigationView.setCheckedItem(R.id.nav_home);
    }

    public void setFeedbackNavCheck() {
        navigationView.setCheckedItem(R.id.nav_feedback);
    }

    public void setReportNavCheck() {
        navigationView.setCheckedItem(R.id.nav_report);
    }

    public void setRequirementNavCheck() {
        navigationView.setCheckedItem(R.id.nav_requirement);
    }

    public void setAuthMissingPersonNavCheck() {
        navigationView.setCheckedItem(R.id.nav_my_missing_person);
    }

    public void setAuthMissingItemNavCheck() {
        navigationView.setCheckedItem(R.id.nav_my_missing_item);
    }

    private void loadUserVerification() {
        progressDialog.setMessage("Checking");
        progressDialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, Api.MY_VERIFICATION_REQUEST, response->{

            try {
                JSONObject object = new JSONObject(response);

                if (object.getBoolean("isEmpty")) {
                    startActivity(new Intent(this, UserVerificationActivity.class));
                } else {
                    JSONObject userVerification = object.getJSONObject("data");
                    Intent intent = new Intent(HomeActivity.this, UserVerificationActivity.class);
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
                    showErrorMessage(errors);
                } catch (JSONException ignored) {
                }
            } else if (errorObj.has("message")) {
                try {
                    Toasty.error(this, errorObj.getString("message"), Toast.LENGTH_SHORT, true).show();
                } catch (JSONException ignored) {
                }
            } else {
                Toasty.error(this, "Request Timeout", Toast.LENGTH_SHORT, true).show();
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

        RequestQueue queue = Volley.newRequestQueue(HomeActivity.this);
        queue.add(request);
    }
    private void openLogoutDialog() {

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        TextView dialogTitle = dialog.findViewById(R.id.txtDialogConfirmationTitle);
        Button delete = dialog.findViewById(R.id.btnDelete);
        Button cancel = dialog.findViewById(R.id.btnCancel);

        dialogTitle.setText(R.string.logout_account);
        delete.setText(R.string.logout);

        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {
            progressDialog.setMessage("Logout .....");
            progressDialog.show();
            logout();
        });

        dialog.show();
    }

    private void logout() {
        StringRequest request = new StringRequest(Request.Method.GET, Api.LOGOUT, res->{

            SharedPreferences.Editor editor = userPref.edit();
            editor.clear();
            editor.apply();

            Toasty.success(this, "Logout success", Toast.LENGTH_SHORT, true).show();

            startActivity(new Intent(HomeActivity.this, AuthActivity.class));
            finish();

            progressDialog.dismiss();
        },error -> {
            error.printStackTrace();
            progressDialog.dismiss();
        }){
            @Override
            public Map<String, String> getHeaders() {
                String token = userPref.getString(Pref.TOKEN,"");
                HashMap<String,String> map = new HashMap<>();
                map.put("Authorization","Bearer "+token);
                return map;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    @Override
    public void onBackPressed() {
        /* when the user press back we dont want to exit the activity instead check first if the drawer is open then it will close
         *  but if the drawer is not open, the activity then will be close*/
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        } else {
            super.onBackPressed();
        }
    }

    public void switchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.bottom_animation, R.anim.slide_out_right)
                .replace(R.id.frameHomeContainer, fragment)
                .commit();
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
}