package com.example.eserbisyo.ModelRecyclerViewAdapters;

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
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

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
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.HomeFragments.AnnouncementFragment;
import com.example.eserbisyo.ModelActivities.CommentActivity;
import com.example.eserbisyo.ModelActivities.MissingItemEditActivity;
import com.example.eserbisyo.ModelActivities.MissingPersonEditActivity;
import com.example.eserbisyo.ModelActivities.Profile.DocumentActivity;
import com.example.eserbisyo.ModelActivities.Profile.MissingPersonActivity;
import com.example.eserbisyo.Models.Announcement;
import com.example.eserbisyo.Models.MissingPerson;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class MissingPersonsAdapter extends RecyclerView.Adapter<MissingPersonsAdapter.MissingPersonsHolder> {

    private final Context context;
    private final ArrayList<MissingPerson> list;
    private final ArrayList<MissingPerson> listAll;
    private final SharedPreferences sharedPreferences;

    private MissingPerson mMissingPersonObj;
    private Dialog userContactDialog, deleteDialog;
    private int selectedPosition;
    private ProgressDialog progressDialog;
    private JSONObject errorObj = null;
    private TextView dialogTitle;
    private Button cancel;

    public MissingPersonsAdapter(Context context, ArrayList<MissingPerson> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public MissingPersonsAdapter.MissingPersonsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_missing_person, parent, false);
        return new MissingPersonsAdapter.MissingPersonsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MissingPersonsAdapter.MissingPersonsHolder holder, @SuppressLint("RecyclerView") int position) {
        MissingPerson missingPersonObj = list.get(position);
        Picasso.get().load(missingPersonObj.getUserPicturePath()).fit().error(R.drawable.user).into(holder.circIvUserPic);
        Picasso.get().load(missingPersonObj.getPicturePath()).fit().error(R.drawable.no_picture).into(holder.ivMissingPicture);

        holder.txtUserName.setText(missingPersonObj.getUserName());
        holder.txtCreatedAt.setText(missingPersonObj.getCreatedAt());
        holder.txtMissingName.setText("Name: " + missingPersonObj.getMissingName() + " (" + missingPersonObj.getReportType() + ")");
        holder.txtHeight.setText("Height: " + missingPersonObj.getHeight() + " " + missingPersonObj.getHeightUnit());
        holder.txtWeight.setText("Weight: " + missingPersonObj.getWeight() + " " + missingPersonObj.getWeightUnit());
        holder.txtEyes.setText("Eyes: " + missingPersonObj.getEyes());
        holder.txtHair.setText("Hair: " + missingPersonObj.getHair());
        holder.txtAge.setText("Age: " + missingPersonObj.getAge() + ((missingPersonObj.getAge() > 1 ) ? " yrs old" : " yr old"));
        holder.txtUniqueSign.setText("Unique Sign: " + missingPersonObj.getUniqueSign());
        holder.txtImportantInfo.setText("Important Information: " + missingPersonObj.getImportantInfo());
        holder.txtLastSeen.setText("Last Seen: " + missingPersonObj.getLastSeen());
        holder.txtCommentCount.setText("View all "+ missingPersonObj.getCommentsCount() + ((missingPersonObj.getCommentsCount() > 1 ) ? " comments" : " comment"));

        if (sharedPreferences.getInt(Pref.ID, 0) != missingPersonObj.getUserId() || !sharedPreferences.getBoolean(Pref.IS_VERIFIED, false)) {
            holder.imgBtnOption.setVisibility(View.GONE);
            holder.linLayAdmin.setVisibility(View.GONE);
        } else {
            switch (missingPersonObj.getStatus()) {
                case "Pending":
                    holder.txtStatus.setTextColor(context.getResources().getColor(R.color.warningColor));
                    holder.txtAdminMessage.setVisibility(View.GONE);
                    holder.txtRespondedAt.setVisibility(View.GONE);
                    break;
                case "Denied":
                    holder.txtStatus.setTextColor(context.getResources().getColor(R.color.firebrick));
                    break;
                case "Approved":
                    holder.txtStatus.setTextColor(context.getResources().getColor(R.color.primaryColor));
                    break;
                case "Resolved":
                    holder.txtStatus.setTextColor(context.getResources().getColor(R.color.teal_700));
                    break;
            }

            holder.txtStatus.setText(missingPersonObj.getStatus());
            holder.txtAdminMessage.setText("Admin Message: " + missingPersonObj.getAdminMessage());
            holder.txtRespondedAt.setText("Responded At: " + missingPersonObj.getUpdatedAt());
        }

        holder.txtUserName.setOnClickListener(v->{
            mMissingPersonObj = missingPersonObj;
            openUserContactDialog();
        });

        holder.txtMissingName.setOnClickListener(view -> {
            Intent intent = new Intent(context, MissingPersonActivity.class);
            intent.putExtra(Extra.MODEL_ID, missingPersonObj.getId());
            intent.putExtra(Extra.MODEL_POSITION, position);
            context.startActivity(intent);
        });

        holder.ivMissingPicture.setOnClickListener(v -> {
            Intent intent= new Intent(context, ViewImageActivity.class);
            intent.putExtra("image_url", missingPersonObj.getPicturePath());
            context.startActivity(intent);
        });


        holder.imgBtnComment.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra(Extra.MODEL_NAME, "MISSING_PERSON");
            i.putExtra(Extra.MODEL_ID, missingPersonObj.getId());
            i.putExtra(Extra.MODEL_POSITION, position);
            context.startActivity(i);
        });

        holder.txtCommentCount.setOnClickListener(v->{
            Intent i = new Intent(((HomeActivity)context), CommentActivity.class);
            i.putExtra(Extra.MODEL_NAME, "MISSING_PERSON");
            i.putExtra(Extra.MODEL_ID, missingPersonObj.getId());
            i.putExtra(Extra.MODEL_POSITION, position);
            context.startActivity(i);
        });

        holder.imgBtnOption.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, holder.imgBtnOption);
            popupMenu.inflate(R.menu.model_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                mMissingPersonObj = missingPersonObj;
                selectedPosition = position;
                switch (item.getItemId()) {
                    case R.id.item_edit: {
                        getData();
                        return true;
                    }
                    case R.id.item_delete: {
                        openDeleteDialog();
                        return true;
                    }
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void getData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.MISSING_PERSONS + "/" + mMissingPersonObj.getId() + Api.EDIT, response -> {
            try {
                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject missingPersonJSONObj = object.getJSONObject("data");
                Intent intent = new Intent(context, MissingPersonEditActivity.class);
                intent.putExtra(Extra.JSON_OBJECT, missingPersonJSONObj.toString());
                intent.putExtra(Extra.MODEL_POSITION, selectedPosition);
                context.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

        },error -> {
            error.printStackTrace();
            progressDialog.dismiss();

            try {
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
            } catch (Exception ignored) {
                Toasty.error(context, "No internet/data connection detected", Toast.LENGTH_SHORT, true).show();
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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(context));
        queue.add(request);
    }

    @SuppressLint("SetTextI18n")
    private void openDeleteDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        deleteDialog = new Dialog(context);
        deleteDialog.setContentView(R.layout.dialog_confirmation);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteDialog.setCancelable(true);

        dialogTitle = deleteDialog.findViewById(R.id.txtDialogConfirmationTitle);
        Button delete = deleteDialog.findViewById(R.id.btnDelete);
        cancel = deleteDialog.findViewById(R.id.btnCancel);

        dialogTitle.setText("DELETE MISSING PERSON REPORT");

        cancel.setOnClickListener(v -> deleteDialog.dismiss());

        delete.setOnClickListener(v -> {
            progressDialog.setMessage("Deleting missing report.....");
            progressDialog.show();
            deleteData();
        });

        deleteDialog.show();
    }

    private void deleteData() {
        StringRequest request = new StringRequest(Request.Method.DELETE, Api.MISSING_PERSONS + "/" + mMissingPersonObj.getId(), response -> {

            list.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            notifyDataSetChanged();
            listAll.clear();
            listAll.addAll(list);

            progressDialog.dismiss();
            deleteDialog.dismiss();
            Toasty.success(context, "Missing Person Deleted", Toast.LENGTH_LONG, true).show();

        },error -> {
            error.printStackTrace();
            progressDialog.dismiss();

            try {
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
            } catch (Exception ignored) {
                Toasty.error(context, "No internet/data connection detected", Toast.LENGTH_SHORT, true).show();
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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }


    private void openUserContactDialog() {
        userContactDialog = new Dialog(context);
        userContactDialog.setContentView(R.layout.dialog_user_contact);
        userContactDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userContactDialog.setCancelable(true);

        CircleImageView ivUser = userContactDialog.findViewById(R.id.ivUser);
        TextView txtUserName = userContactDialog.findViewById(R.id.txtUserName);
        TextView txtEmail = userContactDialog.findViewById(R.id.txtEmail);
        TextView txtPhoneNo = userContactDialog.findViewById(R.id.txtPhoneNo);
        Button btnExit = userContactDialog.findViewById(R.id.btnExit);

        Picasso.get().load(mMissingPersonObj.getUserPicturePath()).fit().error(R.drawable.user).into(ivUser);
        txtUserName.setText(mMissingPersonObj.getUserName());
        txtEmail.setText(mMissingPersonObj.getEmail());
        txtPhoneNo.setText(mMissingPersonObj.getPhoneNo());
        btnExit.setOnClickListener(v -> userContactDialog.dismiss());

        userContactDialog.show();
    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MissingPerson> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (MissingPerson missingPersonObj : listAll){
                    if(missingPersonObj.getUserName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || missingPersonObj.getMissingName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || missingPersonObj.getReportType().toLowerCase().contains(constraint.toString().toLowerCase())
                            || missingPersonObj.getLastSeen().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(missingPersonObj.getAge()).contains(constraint.toString().toLowerCase())
                            || String.valueOf(missingPersonObj.getHeight()).contains(constraint.toString().toLowerCase())
                            || String.valueOf(missingPersonObj.getWeight()).contains(constraint.toString().toLowerCase())
                            || missingPersonObj.getEyes().toLowerCase().contains(constraint.toString().toLowerCase())
                            || missingPersonObj.getHair().toLowerCase().contains(constraint.toString().toLowerCase())
                            || missingPersonObj.getUniqueSign().toLowerCase().contains(constraint.toString().toLowerCase())
                            || missingPersonObj.getImportantInfo().toLowerCase().contains(constraint.toString().toLowerCase())){
                        filteredList.add(missingPersonObj);
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
            list.addAll((Collection<? extends MissingPerson>) results.values);
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

    public class MissingPersonsHolder extends RecyclerView.ViewHolder {
        private final LinearLayout linLayAdmin;
        private final CircleImageView circIvUserPic;
        private final ImageButton imgBtnComment, imgBtnOption;
        private final ImageView ivMissingPicture;
        private final TextView txtUserName, txtCreatedAt, txtMissingName, txtHeight, txtWeight, txtEyes, txtHair, txtAge, txtUniqueSign, txtImportantInfo, txtLastSeen, txtCommentCount, txtStatus, txtAdminMessage, txtRespondedAt;
        public MissingPersonsHolder(@NonNull View itemView) {
            super(itemView);

            linLayAdmin = itemView.findViewById(R.id.linLayAdmin);

            circIvUserPic = itemView.findViewById(R.id.circIvUserImage);
            imgBtnComment = itemView.findViewById(R.id.btnComment);
            imgBtnOption = itemView.findViewById(R.id.btnOption);
            ivMissingPicture = itemView.findViewById(R.id.ivMissingPicture);

            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtMissingName = itemView.findViewById(R.id.txtMissingName);

            txtHeight = itemView.findViewById(R.id.txtHeight);
            txtWeight = itemView.findViewById(R.id.txtWeight);
            txtEyes = itemView.findViewById(R.id.txtEyes);
            txtHair = itemView.findViewById(R.id.txtHair);
            txtAge = itemView.findViewById(R.id.txtAge);

            txtUniqueSign = itemView.findViewById(R.id.txtSign);
            txtImportantInfo = itemView.findViewById(R.id.txtInformation);
            txtLastSeen = itemView.findViewById(R.id.txtLastSeen);
            txtCommentCount = itemView.findViewById(R.id.txtCommentsCount);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAdminMessage = itemView.findViewById(R.id.txtAdminMessage);
            txtRespondedAt = itemView.findViewById(R.id.txtUpdatedAt);
        }
    }
}
