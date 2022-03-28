package com.example.eserbisyo.ModelActivities.Profile;

import static com.example.eserbisyo.R.string.open_paren;

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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.AnnouncementFragment;
import com.example.eserbisyo.ModelRecyclerViewAdapters.CommentsAdapter;
import com.example.eserbisyo.ModelRecyclerViewAdapters.LikesAdapter;
import com.example.eserbisyo.Models.Announcement;
import com.example.eserbisyo.Models.Comment;
import com.example.eserbisyo.Models.Like;
import com.example.eserbisyo.Models.Type;
import com.example.eserbisyo.Models.User;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;

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

public class AnnouncementActivity extends AppCompatActivity {

    private TextView txtDate, txtTitle, txtDesc, txtAddComment;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtComment, txtLike;
    private RecyclerView commentRecyclerView;
    private ArrayList<Comment> commentArrayList;

    public static String modelName;
    private int modelId = 0;
    public static int modelPosition = 0;
    private ImageSlider imageSlider;
    @SuppressLint("StaticFieldLeak")
    public static ImageButton btnLike;

    public static Announcement mAnnouncement;

    private ArrayList<Like> likeArrayList;
    public JSONObject errorObj = null;

    private ProgressDialog progressDialog;
    private SharedPreferences userPref;

    private Dialog likeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_announcement);

        Bundle extras  = getIntent().getExtras();

        if (extras != null) {
            modelId = extras.getInt(Extra.MODEL_ID, 0);
            modelPosition = extras.getInt(Extra.MODEL_POSITION, 0);
            modelName = "ANNOUNCEMENT";
        }
        init();
    }

    private void init() {
        txtDate = findViewById(R.id.txtAnnounceDate);
        txtTitle = findViewById(R.id.txtAnnounceTitle);
        txtDesc = findViewById(R.id.txtAnnounceDesc);
        txtLike = findViewById(R.id.txtAnnounceLikes);
        txtComment = findViewById(R.id.txtAnnounceComments);

        btnLike = findViewById(R.id.btnAnnounceLike);

        imageSlider = findViewById(R.id.imageSlider);

        commentRecyclerView = findViewById(R.id.recyclerComments);
        commentRecyclerView.setHasFixedSize(false);
        commentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        txtAddComment = findViewById(R.id.txtAddComment);
        userPref = AnnouncementActivity.this.getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        getData();
    }

    private void getData() {
        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.ANNOUNCEMENTS + "/" + modelId, response -> {
            try {

                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject announcementJSONObj = object.getJSONObject("data");
                likeArrayList = new ArrayList<>();
                commentArrayList = new ArrayList<>();

                ArrayList<SlideModel> pictureArrayList = new ArrayList<>();
                JSONArray commentJSONArr = new JSONArray(announcementJSONObj.getString("comments"));

                /* Getting of images in announcement */
                JSONArray pictureJSONArray = new JSONArray(announcementJSONObj.getString("announcement_pictures"));
                if(pictureJSONArray.length() > 0){
                    for (int j = 0; j < pictureJSONArray.length(); j++) {
                        JSONObject pictureObject = pictureJSONArray.getJSONObject(j);
                        pictureArrayList.add(new SlideModel(pictureObject.getString("file_path"),  null));
                    }
                }

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

                mAnnouncement = new Announcement(
                        announcementJSONObj.getInt("id"), new Type(announcementJSONObj.getInt("type_id"), announcementJSONObj.getString("announcement_type")),
                        announcementJSONObj.getString("custom_type"), announcementJSONObj.getBoolean("selfLike"), announcementJSONObj.getString("title") , announcementJSONObj.getString("description"),
                        pictureArrayList, announcementJSONObj.getString("created_at"), announcementJSONObj.getInt("likes_count"), announcementJSONObj.getInt("comments_count")
                );


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
        RequestQueue queue = Volley.newRequestQueue(AnnouncementActivity.this);
        queue.add(request);
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        txtDate.setText(mAnnouncement.getCreatedAt());

        if (mAnnouncement.getType().getName().contains("null")) {
            txtTitle.setText(String.format("%s%s%s%s", mAnnouncement.getTitle(), getString(open_paren), mAnnouncement.getCustomType(), getString(R.string.close_paren)));
        } else {
            txtTitle.setText(String.format("%s%s%s%s", mAnnouncement.getTitle(), getString(open_paren), mAnnouncement.getType().getName(), getString(R.string.close_paren)));
        }

        txtDesc.setText(mAnnouncement.getDescription());

        btnLike.setImageResource(
                mAnnouncement.isSelfLike()?R.drawable.ic_baseline_favorite_24:R.drawable.ic_baseline_favorite_border_24
        );

        if(mAnnouncement.getPictureArray().isEmpty()){
            imageSlider.setVisibility(View.GONE);
        } else {
            imageSlider.setImageList(mAnnouncement.getPictureArray(), ScaleTypes.CENTER_CROP);
        }

        txtComment.setText("Total of " + mAnnouncement.getCommentsCount() + ((mAnnouncement.getCommentsCount() > 1 ) ? " comments" : " comment"));
        txtLike.setText(mAnnouncement.getLikesCount() + " likes");

        CommentsAdapter commentsAdapter = new CommentsAdapter(this, commentArrayList);
        commentRecyclerView.setAdapter(commentsAdapter);

        initListener();
    }

    private void initListener() {
        txtLike.setOnClickListener(view -> getLikeList());

        btnLike.setOnClickListener(view -> likePost());

        if(mAnnouncement.getPictureArray().isEmpty()){
            imageSlider.setVisibility(View.GONE);
        } else {
            imageSlider.setImageList(mAnnouncement.getPictureArray(), ScaleTypes.CENTER_CROP);

            imageSlider.setItemClickListener(i -> {
                Intent intent= new Intent(AnnouncementActivity.this, ViewImageActivity.class);
                intent.putExtra("image_url", mAnnouncement.getPictureArray().get(i).getImageUrl());
                startActivity(intent);
            });
        }
    }

    @SuppressLint("SetTextI18n")
    private void likePost() {
        StringRequest request = new StringRequest(Request.Method.POST, Api.ANNOUNCEMENTS_LIKE + "/" + mAnnouncement.getId(), response->{

            mAnnouncement.setSelfLike(!mAnnouncement.isSelfLike());
            mAnnouncement.setLikesCount(mAnnouncement.isSelfLike()?mAnnouncement.getLikesCount()+1:mAnnouncement.getLikesCount()-1);
            txtLike.setText(mAnnouncement.getLikesCount() + " likes");
            btnLike.setImageResource(
                    mAnnouncement.isSelfLike()?R.drawable.ic_baseline_favorite_24:R.drawable.ic_baseline_favorite_border_24
            );

            try {
                Announcement selAnnouncement = AnnouncementFragment.arrayList.get(modelPosition);
                selAnnouncement.setLikesCount(mAnnouncement.getLikesCount());
                selAnnouncement.setSelfLike(mAnnouncement.isSelfLike());
                AnnouncementFragment.arrayList.set(modelPosition,selAnnouncement);
                Objects.requireNonNull(AnnouncementFragment.recyclerView.getAdapter()).notifyItemChanged(modelPosition);
            } catch (Exception ignored) { }

        },error ->{
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

        RequestQueue queue = Volley.newRequestQueue(AnnouncementActivity.this);
        queue.add(request);
    }


    private void getLikeList() {

        progressDialog.setMessage("Getting the likes list of announcement.....");
        progressDialog.show();

        likeArrayList = new ArrayList<>();

        StringRequest request = new StringRequest(Request.Method.GET, Api.ANNOUNCEMENTS_LIKE + "/"+ mAnnouncement.getId(), response -> {
            try {
                JSONObject object = new JSONObject(response);

                JSONArray array = new JSONArray(object.getString("data"));

                for (int i = 0; i < array.length(); i++) {
                    JSONObject likeObject = array.getJSONObject(i);

                    Like like = new Like(
                            likeObject.getInt("id"), new User(likeObject.getInt("user_id"),
                            likeObject.getString("submitted_by"), (!likeObject.isNull("picture_name")) ? likeObject.getString("picture_name") : "",
                            (!likeObject.isNull("file_path")) ? likeObject.getString("file_path") : ""), likeObject.getString("created_at")
                    );

                    likeArrayList.add(like);
                }

                openLikeListDialog();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialog.dismiss();

        },error -> {
            try {
                if (errorObj.has("errors")) {
                    try {
                        JSONObject errors = errorObj.getJSONObject("errors");
                        showErrorMessage(errors);
                    } catch (JSONException ignored) {
                    }
                } else if (errorObj.has("message")) {
                    try {
                        Toasty.error(AnnouncementActivity.this, errorObj.getString("message"), Toast.LENGTH_LONG, true).show();
                    } catch (JSONException ignored) {
                    }
                } else {
                    Toasty.error(AnnouncementActivity.this, "Request Timeout", Toast.LENGTH_SHORT, true).show();
                }
            } catch (Exception ignored) {
                Toasty.error(AnnouncementActivity.this, "No internet/data connection detected", Toast.LENGTH_SHORT, true).show();
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

        RequestQueue queue = Volley.newRequestQueue(AnnouncementActivity.this);
        queue.add(request);


    }

    private void openLikeListDialog() {
        RecyclerView recyclerLike;
        ImageButton btnBack;

        likeDialog = new Dialog(this);
        likeDialog.setContentView(R.layout.dialog_like_list);
        likeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        likeDialog.setCancelable(true);

        btnBack = likeDialog.findViewById(R.id.btnHideLikeDialog);
        recyclerLike = likeDialog.findViewById(R.id.recyclerLike);
        recyclerLike.setHasFixedSize(true);
        recyclerLike.setLayoutManager(new LinearLayoutManager(this));

        LikesAdapter likesAdapter = new LikesAdapter(this, likeArrayList);
        recyclerLike.setAdapter(likesAdapter);

        btnBack.setOnClickListener(v -> likeDialog.dismiss());

        likeDialog.show();
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    public void addComment(View view) {
        String commentText = txtAddComment.getText().toString();
        progressDialog.setMessage("Adding comment");
        progressDialog.show();

        if (commentText.length()>0){
            StringRequest request = new StringRequest(Request.Method.POST, Api.ANNOUNCEMENTS_COMMENTS + "/" + mAnnouncement.getId(), res->{

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
                            Announcement mAnnouncement = AnnouncementFragment.arrayList.get(modelPosition);
                            mAnnouncement.setCommentsCount(mAnnouncement.getCommentsCount() + 1);
                            AnnouncementFragment.arrayList.set(modelPosition,mAnnouncement);
                            Objects.requireNonNull(AnnouncementFragment.recyclerView.getAdapter()).notifyDataSetChanged();
                        } catch (Exception ignored) { }
                        commentArrayList.add(comment);
                        Objects.requireNonNull(commentRecyclerView.getAdapter()).notifyDataSetChanged();
                        txtAddComment.setText("");
                        txtComment.setText("Total of " + commentArrayList.size() + ((commentArrayList.size() > 1 ) ? " comments" : " comment"));
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

            RequestQueue queue = Volley.newRequestQueue(AnnouncementActivity.this);
            queue.add(request);
        } else {
            Toasty.error(this, "Comment is required", Toast.LENGTH_SHORT, true).show();
            progressDialog.dismiss();
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

    public void back(View view) {
        finish();
    }
}