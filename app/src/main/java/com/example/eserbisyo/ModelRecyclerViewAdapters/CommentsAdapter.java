package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeFragments.AnnouncementFragment;
import com.example.eserbisyo.ModelActivities.CommentActivity;
import com.example.eserbisyo.Models.Announcement;
import com.example.eserbisyo.Models.Comment;
import com.example.eserbisyo.Models.User;
import com.example.eserbisyo.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class CommentsAdapter  extends RecyclerView.Adapter<CommentsAdapter.CommentsHolder> {
    private final Context context;
    private final ArrayList<Comment> list;
    private final SharedPreferences sharedPreferences;

    /* ID FOR REFERENCE IN UPDATE|DELETE CERTIFICATE */
    private int id;
    private String comment;
    private int selectedPosition;

    private JSONObject errorObj = null;

    /* FOR DIALOGS */
    private Dialog dialog;
    private TextInputLayout layoutComment;
    private TextInputEditText txtComment;
    private TextView dialogTitle;
    private Button cancel;
    private ProgressDialog progressDialog;

    public CommentsAdapter(Context context, ArrayList<Comment> list) {
        this.context = context;
        this.list = list;
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public CommentsAdapter.CommentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment, parent, false);
        return new CommentsHolder(v);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentsHolder holder, @SuppressLint("RecyclerView") int position) {
        Comment comment = list.get(position);
        Picasso.get().load(Api.STORAGE + comment.getUser().getFilePath()).fit().error(R.drawable.user).into(holder.imgProfile);

        holder.txtDate.setText(comment.getCreatedAt());
        holder.txtComment.setText(comment.getBody());

        if (sharedPreferences.getInt(Pref.ID, 0) != comment.getUser().getId()) {
            holder.txtName.setText(comment.getUser().getName());
            holder.btnOption.setVisibility(View.GONE);
        } else {
            holder.txtName.setText(comment.getUser().getName() + " (You)");
            holder.btnOption.setVisibility(View.VISIBLE);
            holder.btnOption.setOnClickListener(v -> {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popupMenu = new PopupMenu(wrapper, holder.btnOption);
                popupMenu.inflate(R.menu.model_menu);

                popupMenu.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()) {
                        case R.id.item_edit: {
                            selectedPosition = position;
                            id = comment.getId();
                            getCommentData();
                            return true;
                        }
                        case R.id.item_delete: {
                            selectedPosition = position;
                            id = comment.getId();
                            openDeleteDialog();
                            return true;
                        }
                    }
                    return false;
                });
                popupMenu.show();
            });
        }
    }

    private void getCommentData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the comment data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.COMMENTS + id + Api.EDIT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject commentObject = object.getJSONObject("data");

                id = commentObject.getInt("id");
                comment = commentObject.getString("body");

                progressDialog.dismiss();
                /* Open now the edit dialog */
                openEditDialog();

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        },error -> {
            error.printStackTrace();
            progressDialog.dismiss();

            if (errorObj.has("errors")) {
                try {
                    JSONObject errors = errorObj.getJSONObject("errors");
                    ((CommentActivity)context).showErrorMessage(errors);
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

    @SuppressLint("SetTextI18n")
    private void openEditDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_comment);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialogTitle = dialog.findViewById(R.id.txtCommentDialogTitle);

        Button edit = dialog.findViewById(R.id.btnSubmitComment);
        cancel = dialog.findViewById(R.id.btnCancelComment);
        txtComment = dialog.findViewById(R.id.txtComment);
        layoutComment = dialog.findViewById(R.id.txtLayoutComment);
        dialogTitle.setText("Edit Comment");

        txtComment.setText(comment);

        edit.setOnClickListener(v -> {
            if (validate()) {
                progressDialog.setMessage("Updating comment.....");
                progressDialog.show();
                updateComment();
            }
        });

        cancel.setOnClickListener(v -> dialog.dismiss());

        txtComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!Objects.requireNonNull(txtComment.getText()).toString().isEmpty() && txtComment.getText().length() >= 3 || txtComment.getText().length() <= 60) {
                    layoutComment.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialog.show();
    }

    private void updateComment() {
        StringRequest request = new StringRequest(Request.Method.PUT, Api.COMMENTS + id, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("success")) {
                    JSONObject commentObject = object.getJSONObject("data");

                    Comment comment = new Comment(
                            commentObject.getInt("id"), commentObject.getString("body"), new User(commentObject.getInt("user_id"),
                            commentObject.getString("submitted_by"), (!commentObject.isNull("picture_name")) ? commentObject.getString("picture_name") : "",
                            (!commentObject.isNull("file_path")) ? commentObject.getString("file_path") : ""), commentObject.getString("created_at")
                    );

                    // Set the updateGenre to the array list
                    list.set(selectedPosition, comment);
                    // Notify the changes
                    notifyItemChanged(selectedPosition);
                    notifyDataSetChanged();

                    Toasty.success(context, "Comment Updated", Toast.LENGTH_SHORT, true).show();
                    dialog.dismiss();
                } else if (!object.getBoolean("success")) {
                    Object message = object.get("message");
                    ((CommentActivity) context).showErrorMessage(message);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        }, error -> {
            error.printStackTrace();
            progressDialog.dismiss();

            if (errorObj.has("errors")) {
                try {
                    JSONObject errors = errorObj.getJSONObject("errors");
                    ((CommentActivity)context).showErrorMessage(errors);
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
        }) {

            // add token to header
            @Override
            public Map<String, String> getHeaders() {
                String token = sharedPreferences.getString(Pref.TOKEN, "");
                HashMap<String, String> map = new HashMap<>();
                map.put("Authorization", "Bearer " + token);
                return map;
            }

            // add params
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("body", Objects.requireNonNull(txtComment.getText()).toString().trim());
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

    private boolean validate() {
        if (Objects.requireNonNull(txtComment.getText()).toString().isEmpty()) {
            layoutComment.setErrorEnabled(true);
            layoutComment.setError("Comment is Required");
            return false;
        }

        if (txtComment.getText().length() < 3) {
            layoutComment.setErrorEnabled(true);
            layoutComment.setError("Required at least 4 characters");
            return false;
        }

        if (Objects.requireNonNull(txtComment.getText()).toString().length()>60){
            layoutComment.setErrorEnabled(true);
            layoutComment.setError("Required no more than  60 characters");
            return false;
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void openDeleteDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirmation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        dialogTitle = dialog.findViewById(R.id.txtDialogConfirmationTitle);
        Button delete = dialog.findViewById(R.id.btnDelete);
        cancel = dialog.findViewById(R.id.btnCancel);

        dialogTitle.setText("DELETE COMMENT");

        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {
            progressDialog.setMessage("Deleting comment.....");
            progressDialog.show();
            deleteComment();
        });

        dialog.show();
    }

    private void deleteComment() {
        StringRequest request = new StringRequest(Request.Method.DELETE, Api.COMMENTS + id, response -> {

            list.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            notifyDataSetChanged();


            if (CommentActivity.modelName.equals("ANNOUNCEMENT")) {
                Announcement announcement = AnnouncementFragment.arrayList.get(CommentActivity.modelPosition);
                announcement.setCommentsCount(announcement.getCommentsCount()-1);
                AnnouncementFragment.arrayList.set(CommentActivity.modelPosition, announcement);
                Objects.requireNonNull(AnnouncementFragment.recyclerView.getAdapter()).notifyDataSetChanged();
            }

            notifyDataSetChanged();

            dialog.dismiss();
            Toasty.success(context, "Comment Deleted", Toast.LENGTH_LONG, true).show();

            progressDialog.dismiss();

        },error -> {
            error.printStackTrace();
            progressDialog.dismiss();

            if (errorObj.has("errors")) {
                try {
                    JSONObject errors = errorObj.getJSONObject("errors");
                    ((CommentActivity)context).showErrorMessage(errors);
                } catch (JSONException ignored) {
                }
            } else if (errorObj.has("message")) {
                try {
                    Toasty.error(context, errorObj.getString("message"), Toast.LENGTH_LONG, true).show();
                } catch (JSONException ignored) {
                }
            } else {
                Toasty.error(context, "Request Timeout", Toast.LENGTH_LONG, true).show();
            }
        }){
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class CommentsHolder extends RecyclerView.ViewHolder {
        private final CircleImageView imgProfile;
        private final TextView txtName, txtDate, txtComment;
        private final ImageButton btnOption;

        public CommentsHolder(@NonNull View itemView) {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgCommentProfile);
            txtName = itemView.findViewById(R.id.txtCommentName);
            txtDate = itemView.findViewById(R.id.txtCommentDate);
            txtComment = itemView.findViewById(R.id.txtCommentText);
            btnOption = itemView.findViewById(R.id.btnOption);
        }
    }
}