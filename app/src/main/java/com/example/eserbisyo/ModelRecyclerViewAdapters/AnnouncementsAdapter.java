package com.example.eserbisyo.ModelRecyclerViewAdapters;

import static com.example.eserbisyo.R.string.open_paren;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.ModelActivities.CommentActivity;
import com.example.eserbisyo.Models.Announcement;
import com.example.eserbisyo.Models.Like;
import com.example.eserbisyo.Models.User;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class AnnouncementsAdapter extends RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementsHolder> {
    private final Context context;
    private final ArrayList<Announcement> list;
    private final ArrayList<Announcement> listAll;
    private final SharedPreferences sharedPreferences;

    private ArrayList<Like> likeArrayList;

    private Announcement mAnnouncement;
    private int mPosition;
    private JSONObject errorObj = null;

    private ProgressDialog progressDialog;
    private Dialog dialog;


    public AnnouncementsAdapter(Context context, ArrayList<Announcement> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public AnnouncementsAdapter.AnnouncementsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_announcement, parent, false);
        return new AnnouncementsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull AnnouncementsAdapter.AnnouncementsHolder holder, @SuppressLint("RecyclerView") int position) {
        Announcement announcement = list.get(position);

        holder.txtDate.setText(announcement.getCreatedAt());
        holder.txtTitle.setText(String.format("%s%s%s%s", announcement.getTitle(), context.getString(open_paren), announcement.getType().getName(), context.getString(R.string.close_paren)));
        String mDesc;
        if (announcement.getDescription().length() > 300) {
            mDesc = announcement.getDescription().substring(0, 300) + "...";
        } else {
            mDesc = announcement.getDescription();
        }

        holder.txtDesc.setText(mDesc);

        holder.btnLike.setImageResource(
                announcement.isSelfLike()?R.drawable.ic_baseline_favorite_24:R.drawable.ic_baseline_favorite_border_24
        );

        holder.btnLike.setOnClickListener(v->{
            holder.btnLike.setImageResource(
                    announcement.isSelfLike()?R.drawable.ic_baseline_favorite_border_24:R.drawable.ic_baseline_favorite_24
            );

            mAnnouncement = announcement;
            mPosition = position;
            likePost();
        });

        if(announcement.getPictureArray().isEmpty()){
            holder.imageSlider.setVisibility(View.GONE);
        } else {
            holder.imageSlider.setImageList(announcement.getPictureArray(), ScaleTypes.CENTER_CROP);

            holder.imageSlider.setItemClickListener(i -> {
                Intent intent= new Intent(context, ViewImageActivity.class);
                intent.putExtra("image_url", announcement.getPictureArray().get(i).getImageUrl());
                context.startActivity(intent);
            });

        }

        holder.txtComment.setText("View all "+ announcement.getCommentsCount() + ((announcement.getCommentsCount() > 1 ) ? " comments" : " comment"));
        holder.txtLike.setText(announcement.getLikesCount() + " likes");

        holder.txtLike.setOnClickListener(v -> {
            mAnnouncement = announcement;
            getLikeList();
        });

        holder.btnComment.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);

            i.putExtra(Extra.MODEL_NAME, "ANNOUNCEMENT");
            i.putExtra(Extra.MODEL_ID, announcement.getId());
            i.putExtra(Extra.MODEL_POSITION, position);
            context.startActivity(i);
        });

        holder.txtComment.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);

            i.putExtra(Extra.MODEL_NAME, "ANNOUNCEMENT");
            i.putExtra(Extra.MODEL_ID, announcement.getId());
            i.putExtra(Extra.MODEL_POSITION, position);
            context.startActivity(i);
        });

    }

    private void getLikeList() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

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
            if (errorObj.has("errors")) {
                try {
                    JSONObject errors = errorObj.getJSONObject("errors");
                    ((HomeActivity)context).showErrorMessage(errors);
                } catch (JSONException ignored) {
                }
            } else if (errorObj.has("message")) {
                try {
                    Toasty.error(context, errorObj.getString("message"), Toast.LENGTH_LONG, true).show();
                } catch (JSONException ignored) {
                }
            } else {
                Toasty.error(context, "Request Timeout", Toast.LENGTH_SHORT, true).show();
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

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(context));
        queue.add(request);


    }

    private void openLikeListDialog() {
        RecyclerView recyclerLike;
        ImageButton btnBack;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_like_list);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        btnBack = dialog.findViewById(R.id.btnHideLikeDialog);
        recyclerLike = dialog.findViewById(R.id.recyclerLike);
        recyclerLike.setHasFixedSize(true);
        recyclerLike.setLayoutManager(new LinearLayoutManager(context));

        LikesAdapter likesAdapter = new LikesAdapter(context, likeArrayList);
        recyclerLike.setAdapter(likesAdapter);

        btnBack.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void likePost() {
        StringRequest request = new StringRequest(Request.Method.POST, Api.ANNOUNCEMENTS_LIKE + "/" + mAnnouncement.getId(), response->{

            mAnnouncement.setSelfLike(!mAnnouncement.isSelfLike());
            mAnnouncement.setLikesCount(mAnnouncement.isSelfLike()?mAnnouncement.getLikesCount()+1:mAnnouncement.getLikesCount()-1);
            list.set(mPosition,mAnnouncement);
            notifyItemChanged(mPosition);
            notifyDataSetChanged();

        },error ->{
            if (errorObj.has("errors")) {
                try {
                    JSONObject errors = errorObj.getJSONObject("errors");
                    ((HomeActivity)context).showErrorMessage(errors);
                } catch (JSONException ignored) {
                }
            } else if (errorObj.has("message")) {
                try {
                    Toasty.error(context, errorObj.getString("message"), Toast.LENGTH_LONG, true).show();
                } catch (JSONException ignored) {
                }
            } else {
                Toasty.error(context, "Request Timeout", Toast.LENGTH_SHORT, true).show();
            }
        } ){

            //add token to headers
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

        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Announcement> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Announcement announcement : listAll){
                    if(announcement.getType().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || announcement.getTitle().toLowerCase().contains(constraint.toString().toLowerCase())
                            || announcement.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())
                            || announcement.getCreatedAt().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(announcement);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            list.addAll((Collection<? extends Announcement>) results.values);
            notifyDataSetChanged();
        }
    };

    public Filter getFilter() {
        return filter;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class AnnouncementsHolder extends RecyclerView.ViewHolder {
        private final TextView txtDate, txtTitle, txtDesc, txtLike, txtComment;
        private final ImageSlider imageSlider;
        private final ImageButton btnLike, btnComment;

        public AnnouncementsHolder(@NonNull View itemView) {
            super(itemView);

            txtDate = itemView.findViewById(R.id.txtAnnounceDate);
            txtTitle = itemView.findViewById(R.id.txtAnnounceTitle);
            txtDesc = itemView.findViewById(R.id.txtAnnounceDesc);
            txtLike = itemView.findViewById(R.id.txtAnnounceLikes);
            txtComment = itemView.findViewById(R.id.txtAnnounceComments);

            btnLike = itemView.findViewById(R.id.btnAnnounceLike);
            btnComment = itemView.findViewById(R.id.btnAnnounceComment);

            imageSlider = itemView.findViewById(R.id.imageSlider);


        }
    }
}
