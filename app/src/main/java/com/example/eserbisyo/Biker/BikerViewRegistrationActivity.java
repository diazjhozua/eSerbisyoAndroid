package com.example.eserbisyo.Biker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class BikerViewRegistrationActivity extends AppCompatActivity {
    private TextView txtOverallStatus, txtBikeType, txtBikeSize, txtBikeColor, txtReason, txtStatus, txtAdminMessage, txtRespondedAt, txtPhoneNo;
    private ImageView ivCredentialPicture;
    private Button btnResubmit;

    private String status, type, size, color, reason, imgPath, adminMessage, respondedAt, phoneNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biker_view_registration);

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            try {
                JSONObject jsonObject = new JSONObject(extras.getString(Extra.JSON_OBJECT));

                status = jsonObject.getString("status");
                phoneNo = jsonObject.getString("phone_no");
                type = jsonObject.getString("bike_type");
                size = jsonObject.getString("bike_size");
                color = jsonObject.getString("bike_color");
                reason = jsonObject.getString("reason");
                imgPath = jsonObject.getString("credential_file_path");
                adminMessage = jsonObject.getString("admin_message");
                respondedAt = jsonObject.getString("updated_at");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        init();
    }

    private void init() {
        txtOverallStatus = findViewById(R.id.txtOverallStatus);
        txtPhoneNo = findViewById(R.id.txtPhoneNo);
        txtBikeType = findViewById(R.id.txtBikeType);
        txtBikeSize = findViewById(R.id.txtBikeSize);
        txtBikeColor = findViewById(R.id.txtBikeColor);
        txtReason = findViewById(R.id.txtReason);
        txtStatus = findViewById(R.id.txtStatus);
        txtAdminMessage = findViewById(R.id.txtAdminMessage);
        txtRespondedAt = findViewById(R.id.txtUpdatedAt);
        ivCredentialPicture = findViewById(R.id.ivCredentialPicture);
        btnResubmit = findViewById(R.id.btnResubmit);

        setData();
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        if (status.equals("Pending")) {
            txtAdminMessage.setVisibility(View.GONE);
            txtRespondedAt.setVisibility(View.GONE);
            txtStatus.setTextColor(getResources().getColor(R.color.primaryColor));

        } else if (status.equals("Denied")) {
            txtOverallStatus.setText("DENIED");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.firebrick));
            btnResubmit.setVisibility(View.VISIBLE);
            txtStatus.setTextColor(getResources().getColor(R.color.firebrick));
        } else {
            txtOverallStatus.setText("APPROVED. PLEASE RE-OPEN THE PAGE TO REFRESH YOUR CREDENTIAL");
            txtOverallStatus.setBackgroundColor(getResources().getColor(R.color.teal_700));
            txtStatus.setTextColor(getResources().getColor(R.color.teal_700));
        }

        txtPhoneNo.setText("Phone No: " + phoneNo);
        txtBikeType.setText("Bike Type: " + type);
        txtBikeSize.setText("Bike Size: " + size);
        txtBikeColor.setText("Bike Color: " + color);
        txtReason.setText(reason);
        txtStatus.setText(status);
        txtAdminMessage.setText("Admin Message: " + adminMessage);
        txtRespondedAt.setText(respondedAt);
        Picasso.get().load(Api.STORAGE + imgPath).fit().error(R.drawable.no_picture).into(ivCredentialPicture);

        initListener();
    }

    private void initListener() {
        btnResubmit.setOnClickListener(view -> {
            startActivity(new Intent(BikerViewRegistrationActivity.this, BikerRegisterActivity.class));
            finish();
        });

        ivCredentialPicture.setOnClickListener(view -> {
            Intent intent= new Intent(BikerViewRegistrationActivity.this, ViewImageActivity.class);
            intent.putExtra("image_url", Api.STORAGE + imgPath);
            startActivity(intent);
        });
    }

    public void cancelEdit(View view) {
        finish();
    }
}