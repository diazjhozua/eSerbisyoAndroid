package com.example.eserbisyo.ModelActivities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.AnnouncementFragment;
import com.example.eserbisyo.HomeFragments.MissingItemFragment;
import com.example.eserbisyo.HomeFragments.MissingPersonFragment;
import com.example.eserbisyo.ModelRecyclerViewAdapters.CommentsAdapter;
import com.example.eserbisyo.Models.Announcement;
import com.example.eserbisyo.Models.Comment;
import com.example.eserbisyo.Models.MissingItem;
import com.example.eserbisyo.Models.MissingPerson;
import com.example.eserbisyo.Models.User;
import com.example.eserbisyo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class CommentActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Comment> commentArrayList;
    private CommentsAdapter commentsAdapter;

    private int modelId;
    public static int modelPosition;
    public static String modelName;
    private String apiURL;

    private SharedPreferences sharedPreferences;
    private EditText txtAddComment;
    private ProgressDialog dialog;

    public JSONObject errorObj = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
    }

    private void init() {
        modelId = getIntent().getIntExtra(Extra.MODEL_ID, 0);
        modelPosition = getIntent().getIntExtra(Extra.MODEL_POSITION, 0);
        modelName = getIntent().getStringExtra(Extra.MODEL_NAME);

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);

        sharedPreferences = getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
        recyclerView = findViewById(R.id.recyclerComments);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        txtAddComment = findViewById(R.id.txtAddComment);

        getApiURL();

    }

    private void getApiURL() {
        switch (modelName) {
            case "ANNOUNCEMENT":
                apiURL = Api.ANNOUNCEMENTS_COMMENTS + modelId;
                break;
            case "MISSING_PERSON":
                apiURL = Api.MISSING_PERSONS_COMMENTS + modelId;
                break;
            case "MISSING_ITEM":
                apiURL = Api.MISSING_ITEMS_COMMENTS + modelId;
                break;
        }
        getComments();
    }

    private void getComments() {
        if (!apiURL.isEmpty()) {
            commentArrayList = new ArrayList<>();

            StringRequest request = new StringRequest(Request.Method.GET, apiURL, response -> {

                try {
                    JSONObject object = new JSONObject(response);

                    JSONArray array = new JSONArray(object.getString("data"));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject commentObject = array.getJSONObject(i);
                        Log.d("commentObject", commentObject.toString(4));

                        Comment comment = new Comment(
                                commentObject.getInt("id"), commentObject.getString("body"), new User(commentObject.getInt("user_id"),
                                commentObject.getString("submitted_by"), (!commentObject.isNull("picture_name")) ? commentObject.getString("picture_name") : "",
                                (!commentObject.isNull("file_path")) ? commentObject.getString("file_path") : ""), commentObject.getString("created_at")
                        );
                        commentArrayList.add(comment);
                    }
                    commentsAdapter = new CommentsAdapter(this, commentArrayList);
                    recyclerView.setAdapter(commentsAdapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            },error -> {
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

            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(request);
        }
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

    public void goBack(View view) {
        finish();
    }

    public void addComment(View view) {
        String commentText = txtAddComment.getText().toString();
        dialog.setMessage("Adding comment");
        dialog.show();
        if (commentText.length()>0){
            StringRequest request = new StringRequest(Request.Method.POST, apiURL, res->{

                try {
                    JSONObject object = new JSONObject(res);
                    if (object.getBoolean("success")){
                        JSONObject commentObject = object.getJSONObject("data");

                        Comment comment = new Comment(
                                commentObject.getInt("id"), commentObject.getString("body"), new User(commentObject.getInt("user_id"),
                                commentObject.getString("submitted_by"), (!commentObject.isNull("picture_name")) ? commentObject.getString("picture_name") : "",
                                (!commentObject.isNull("file_path")) ? commentObject.getString("file_path") : ""), commentObject.getString("created_at")
                        );


                        switch (modelName) {
                            case "ANNOUNCEMENT":
//                                apiURL = Api.ANNOUNCEMENTS_COMMENTS + modelId;
                                Announcement announcement = AnnouncementFragment.arrayList.get(modelPosition);
                                announcement.setCommentsCount(announcement.getCommentsCount() + 1);
                                AnnouncementFragment.arrayList.set(modelPosition,announcement);
                                Objects.requireNonNull(AnnouncementFragment.recyclerView.getAdapter()).notifyDataSetChanged();
                                break;
                            case "MISSING_PERSON":
//                                apiURL = Api.MISSING_PERSONS_COMMENTS + modelId;
                                MissingPerson missingPersonObj = MissingPersonFragment.arrayList.get(modelPosition);
                                missingPersonObj.setCommentsCount(missingPersonObj.getCommentsCount() + 1);
                                MissingPersonFragment.arrayList.set(modelPosition,missingPersonObj);
                                Objects.requireNonNull(MissingPersonFragment.recyclerView.getAdapter()).notifyDataSetChanged();

                                break;
                            case "MISSING_ITEM":
//                                apiURL = Api.MISSING_ITEMS_COMMENTS + modelId;
                                MissingItem missingItemObj = MissingItemFragment.arrayList.get(modelPosition);
                                missingItemObj.setCommentsCount(missingItemObj.getCommentsCount() + 1);
                                MissingItemFragment.arrayList.set(modelPosition,missingItemObj);
                                Objects.requireNonNull(MissingItemFragment.recyclerView.getAdapter()).notifyDataSetChanged();
                                break;
                        }
                        commentArrayList.add(comment);
                        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
                        txtAddComment.setText("");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();

            },err->{
                err.printStackTrace();
                dialog.dismiss();

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

            }){
                //add token to header

                @Override
                public Map<String, String> getHeaders() {
                    String token = sharedPreferences.getString(Pref.TOKEN,"");
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
            RequestQueue queue = Volley.newRequestQueue(CommentActivity.this);
            queue.add(request);
        }
    }
}