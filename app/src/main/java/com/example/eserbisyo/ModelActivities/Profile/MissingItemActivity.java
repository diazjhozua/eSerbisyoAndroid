package com.example.eserbisyo.ModelActivities.Profile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.AuthMissingItemFragment;
import com.example.eserbisyo.HomeFragments.AuthMissingPersonFragment;
import com.example.eserbisyo.HomeFragments.MissingItemFragment;
import com.example.eserbisyo.HomeFragments.MissingPersonFragment;
import com.example.eserbisyo.ModelRecyclerViewAdapters.CommentsAdapter;
import com.example.eserbisyo.Models.Comment;
import com.example.eserbisyo.Models.MissingItem;
import com.example.eserbisyo.Models.MissingPerson;
import com.example.eserbisyo.Models.User;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MissingItemActivity extends AppCompatActivity {
    private LinearLayout linLayAdmin;
    private CircleImageView circIvUserPic;
    private ImageView ivMissingPicture;
    private TextView txtUserName, txtCreatedAt, txtMissingName, txtImportantInfo, txtLastSeen, txtStatus, txtAdminMessage, txtRespondedAt;

    private RecyclerView commentRecyclerView;
    private ArrayList<Comment> commentArrayList;

    @SuppressLint("StaticFieldLeak")
    public static TextView txtCommentCount;

    private TextView txtAddComment;

    private Dialog userContactDialog;
    private int modelId = 0;
    public static int modelPosition = 0;
    public static String modelName;
    private ProgressDialog progressDialog;
    private SharedPreferences userPref;
    public JSONObject errorObj = null;

    public static MissingItem mMissingItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_item);

        Bundle extras  = getIntent().getExtras();
        if (extras != null) {
            modelId = extras.getInt(Extra.MODEL_ID, 0);
            modelPosition = extras.getInt(Extra.MODEL_POSITION, 0);
            modelName = "MISSING_ITEM";
        }

        init();
    }

    private void init() {
        linLayAdmin = findViewById(R.id.linLayAdmin);

        circIvUserPic = findViewById(R.id.circIvUserImage);
        ivMissingPicture = findViewById(R.id.ivMissingPicture);

        txtUserName = findViewById(R.id.txtUserName);
        txtCreatedAt = findViewById(R.id.txtCreatedAt);
        txtMissingName = findViewById(R.id.txtMissingName);

        txtImportantInfo = findViewById(R.id.txtInformation);
        txtLastSeen = findViewById(R.id.txtLastSeen);
        txtCommentCount = findViewById(R.id.txtCommentsCount);
        txtStatus = findViewById(R.id.txtStatus);
        txtAdminMessage = findViewById(R.id.txtAdminMessage);
        txtRespondedAt = findViewById(R.id.txtUpdatedAt);

        commentRecyclerView = findViewById(R.id.recyclerComments);
        commentRecyclerView.setHasFixedSize(false);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtAddComment = findViewById(R.id.txtAddComment);
        userPref = MissingItemActivity.this.getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        getData();
    }

    private void getData() {
        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.MISSING_ITEMS + "/" + modelId, response -> {
            try {

                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject missingItemJSONObject = object.getJSONObject("data");
                JSONArray commentJSONArr = new JSONArray(missingItemJSONObject.getString("comments"));
                commentArrayList = new ArrayList<>();

                /* Getting the comment list */
                for (int i = 0; i < commentJSONArr.length(); i++) {
                    JSONObject commentObject = commentJSONArr.getJSONObject(i);
                    Log.d("commentObject", commentObject.toString(4));

                    Comment comment = new Comment(
                            commentObject.getInt("id"), commentObject.getString("body"), new User(commentObject.getInt("user_id"),
                            commentObject.getString("submitted_by"), (!commentObject.isNull("picture_name")) ? commentObject.getString("picture_name") : "",
                            (!commentObject.isNull("file_path")) ? commentObject.getString("file_path") : ""), commentObject.getString("created_at")
                    );
                    commentArrayList.add(comment);
                }

                mMissingItem = new MissingItem(
                        missingItemJSONObject.getInt("id"), missingItemJSONObject.getInt("contact_id"), missingItemJSONObject.getString("contact_name"), missingItemJSONObject.getString("user_picture_name"),
                        missingItemJSONObject.getString("user_file_path"), missingItemJSONObject.getString("report_type"), missingItemJSONObject.getString("item"), missingItemJSONObject.getString("last_seen"),
                        missingItemJSONObject.getString("description"), missingItemJSONObject.getString("email"), missingItemJSONObject.getString("phone_no"), missingItemJSONObject.getString("picture_name"),
                        missingItemJSONObject.getString("file_path"), missingItemJSONObject.getString("credential_name"), missingItemJSONObject.getString("credential_path"),
                        missingItemJSONObject.getInt("comments_count"), missingItemJSONObject.getString("status"), missingItemJSONObject.getString("admin_message"),  missingItemJSONObject.getString("created_at"),
                        missingItemJSONObject.getString("updated_at"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            setData();

            progressDialog.dismiss();

        },error -> {
            error.printStackTrace();
            progressDialog.dismiss();

            try {
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
                    Toasty.error(this, "Request Timeout", Toast.LENGTH_SHORT, true).show();
                }
            } catch (Exception ignored) {
                Toasty.error(this, "No internet/data connection detected", Toast.LENGTH_SHORT, true).show();
            }

            finish();
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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(MissingItemActivity.this);
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


    @SuppressLint("SetTextI18n")
    private void setData() {
        Picasso.get().load(mMissingItem.getUserPicturePath()).fit().error(R.drawable.user).into(circIvUserPic);
        Picasso.get().load(mMissingItem.getPicturePath()).fit().error(R.drawable.no_picture).into(ivMissingPicture);

        txtUserName.setText(mMissingItem.getUserName());
        txtCreatedAt.setText(mMissingItem.getCreatedAt());
        txtMissingName.setText("Item:" + mMissingItem.getItemName() + " (" + mMissingItem.getReportType() + ")");
        txtImportantInfo.setText("Description: " + mMissingItem.getDescription());
        txtLastSeen.setText("Last Seen: " + mMissingItem.getLastSeen());
        txtCommentCount.setText("Total of "+ mMissingItem.getCommentsCount() + ((mMissingItem.getCommentsCount() > 1 ) ? " comments" : " comment"));

        if (userPref.getInt(Pref.ID, 0) != mMissingItem.getUserId() || !userPref.getBoolean(Pref.IS_VERIFIED, false)) {
            linLayAdmin.setVisibility(View.GONE);
        } else {
            switch (mMissingItem.getStatus()) {
                case "Pending":
                    txtStatus.setTextColor(getResources().getColor(R.color.warningColor));
                    txtRespondedAt.setVisibility(View.GONE);
                    txtAdminMessage.setVisibility(View.GONE);
                    break;
                case "Denied":
                    txtStatus.setTextColor(getResources().getColor(R.color.firebrick));
                    break;
                case "Approved":
                    txtStatus.setTextColor(getResources().getColor(R.color.primaryColor));
                    break;
                case "Resolved":
                    txtStatus.setTextColor(getResources().getColor(R.color.teal_700));
                    break;
            }

            txtStatus.setText(mMissingItem.getStatus());
            txtAdminMessage.setText("Admin Message: " + mMissingItem.getAdminMessage());
            txtRespondedAt.setText("Responded At: " + mMissingItem.getUpdatedAt());
        }

        CommentsAdapter commentsAdapter = new CommentsAdapter(this, commentArrayList);
        commentRecyclerView.setAdapter(commentsAdapter);

        initListener();
    }


    private void initListener() {
        txtUserName.setOnClickListener(v->{
            openUserContactDialog();
        });

        ivMissingPicture.setOnClickListener(v -> {
            Intent intent= new Intent(MissingItemActivity.this, ViewImageActivity.class);
            intent.putExtra("image_url", mMissingItem.getPicturePath());
            startActivity(intent);
        });
    }

    private void openUserContactDialog() {
        userContactDialog = new Dialog(MissingItemActivity.this);
        userContactDialog.setContentView(R.layout.dialog_user_contact);
        userContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userContactDialog.setCancelable(true);

        CircleImageView ivUser = userContactDialog.findViewById(R.id.ivUser);
        TextView txtUserName = userContactDialog.findViewById(R.id.txtUserName);
        TextView txtEmail = userContactDialog.findViewById(R.id.txtEmail);
        TextView txtPhoneNo = userContactDialog.findViewById(R.id.txtPhoneNo);
        Button btnExit = userContactDialog.findViewById(R.id.btnExit);

        Picasso.get().load(mMissingItem.getUserPicturePath()).fit().error(R.drawable.user).into(ivUser);
        txtUserName.setText(mMissingItem.getUserName());
        txtEmail.setText(mMissingItem.getEmail());
        txtPhoneNo.setText(mMissingItem.getPhoneNo());
        btnExit.setOnClickListener(v -> userContactDialog.dismiss());

        userContactDialog.show();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void addComment(View view) {
        String commentText = txtAddComment.getText().toString();
        progressDialog.setMessage("Adding comment");
        progressDialog.show();

        if (commentText.length()>0){
            StringRequest request = new StringRequest(Request.Method.POST, Api.MISSING_ITEMS_COMMENTS + "/" + modelId, res->{

                try {
                    JSONObject object = new JSONObject(res);
                    if (object.getBoolean("success")){
                        JSONObject commentObject = object.getJSONObject("data");

                        Comment comment = new Comment(
                                commentObject.getInt("id"), commentObject.getString("body"), new User(commentObject.getInt("user_id"),
                                commentObject.getString("submitted_by"), (!commentObject.isNull("picture_name")) ? commentObject.getString("picture_name") : "",
                                (!commentObject.isNull("file_path")) ? commentObject.getString("file_path") : ""), commentObject.getString("created_at")
                        );

                        try {
                            MissingItem mMissingItem = MissingItemFragment.arrayList.get(modelPosition);
                            mMissingItem.setCommentsCount(mMissingItem.getCommentsCount() + 1);
                            MissingItemFragment.arrayList.set(modelPosition,mMissingItem);
                            Objects.requireNonNull(MissingItemFragment.recyclerView.getAdapter()).notifyItemChanged(modelPosition);
                        } catch (Exception ignored) { }

                        try {
                            MissingItem mMissingItem = AuthMissingItemFragment.arrayList.get(modelPosition);
                            mMissingItem.setCommentsCount(mMissingItem.getCommentsCount() + 1);
                            AuthMissingItemFragment.arrayList.set(modelPosition,mMissingItem);
                            Objects.requireNonNull(AuthMissingItemFragment.recyclerView.getAdapter()).notifyItemChanged(modelPosition);
                        } catch (Exception ignored) { }

                        commentArrayList.add(comment);
                        Objects.requireNonNull(commentRecyclerView.getAdapter()).notifyDataSetChanged();
                        txtAddComment.setText("");
                        txtCommentCount.setText("Total of " + commentArrayList.size() + ((commentArrayList.size() > 1 ) ? " comments" : " comment"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();

            },err->{
                err.printStackTrace();
                progressDialog.dismiss();

                try {
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
                } catch (Exception ignored) {
                    Toasty.error(this, "No internet/data connection detected", Toast.LENGTH_SHORT, true).show();
                }

            }){
                //add token to header

                @Override
                public Map<String, String> getHeaders() {
                    String token = userPref.getString(Pref.TOKEN,"");
                    HashMap<String,String> map = new HashMap<>();
                    map.put("Authorization","Bearer "+token);
                    return map;
                }

                @Override
                protected Map<String, String> getParams() {
                    HashMap<String,String> map = new HashMap<>();
                    map.put("body",commentText);
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

            RequestQueue queue = Volley.newRequestQueue(MissingItemActivity.this);
            queue.add(request);
        } else {
            Toasty.error(this, "Comment is required", Toast.LENGTH_SHORT, true).show();
            progressDialog.dismiss();
        }
    }

    public void back(View view) {
        finish();
    }
}