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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
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
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.HomeFragments.AnnouncementFragment;
import com.example.eserbisyo.ModelActivities.CommentActivity;
import com.example.eserbisyo.ModelActivities.ComplaintEditActivity;
import com.example.eserbisyo.ModelActivities.ComplaintViewActivity;
import com.example.eserbisyo.ModelActivities.MissingPersonEditActivity;
import com.example.eserbisyo.Models.Announcement;
import com.example.eserbisyo.Models.Complaint;
import com.example.eserbisyo.Models.MissingPerson;
import com.example.eserbisyo.Models.Report;
import com.example.eserbisyo.R;

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

public class ComplaintsAdapter extends RecyclerView.Adapter<ComplaintsAdapter.ComplaintsHolder> {
    private final Context context;
    private final ArrayList<Complaint> list;
    private final ArrayList<Complaint> listAll;
    private final SharedPreferences sharedPreferences;

    /* LOADING */
    private ProgressDialog progressDialog;
    private Dialog dialog;
    private TextView dialogTitle;
    private Button cancel;

    private int id;
    private String comment;
    private int selectedPosition;


    private JSONObject errorObj = null;


    public ComplaintsAdapter(Context context, ArrayList<Complaint> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ComplaintsAdapter.ComplaintsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_complaint, parent, false);
        return new ComplaintsAdapter.ComplaintsHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull ComplaintsAdapter.ComplaintsHolder holder, @SuppressLint("RecyclerView") int position) {
        Complaint mComplaint = list.get(position);

        holder.txtID.setText("Complaint # " +mComplaint.getId());
        holder.txtCreatedAt.setText(mComplaint.getCreatedAt());

        if (mComplaint.getType().getId() == 0) {
            holder.txtType.setText("Complaint type: " + mComplaint.getCustomType());
        } else {
            holder.txtType.setText("Complaint type: " + mComplaint.getType().getName());
        }

        String reason;
        if (mComplaint.getReason().length() > 100) {
            reason = mComplaint.getReason().substring(0, 100) + "...";
        } else {
            reason = mComplaint.getReason();
        }

        String action;
        if (mComplaint.getAction().length() > 100) {
            action = mComplaint.getAction().substring(0, 100) + "...";
        } else {
            action = mComplaint.getAction();
        }

        holder.txtReason.setText("Reason: " + reason);
        holder.txtAction.setText("Action: " + action);

        switch (mComplaint.getStatus()) {
            case "Pending":
                holder.txtStatus.setTextColor(context.getResources().getColor(R.color.warningColor));
                holder.txtAdminMessage.setVisibility(View.GONE);
                holder.txtUpdatedAt.setVisibility(View.GONE);
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

        holder.txtStatus.setText(mComplaint.getStatus());
        holder.txtAdminMessage.setText("Admin Message: " + mComplaint.getAdminMessage());
        holder.txtUpdatedAt.setText("Responded At: " + mComplaint.getUpdatedAt());

        holder.imgBtnOption.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, holder.imgBtnOption);
            popupMenu.inflate(R.menu.model_menu2);

            popupMenu.setOnMenuItemClickListener(item -> {
                id = mComplaint.getId();
                selectedPosition = position;
                switch (item.getItemId()) {
                    case R.id.item_view: {
                        getData();
                        return true;
                    }
                    case R.id.item_edit: {
                        if (sharedPreferences.getInt(Pref.IS_VERIFIED, 0) != 1) {
                            Toasty.error(context, "This function is for verified user only", Toast.LENGTH_LONG, true).show();
                        } else {
                            if (mComplaint.getStatus().equals("Pending") || mComplaint.getStatus().equals("Denied")) {
                                getEditData();
                            } else {
                                Toasty.error(context, "You cannot edit this complaint once the complaint status is in Approved or Resolved", Toast.LENGTH_LONG, true).show();
                            }
                        }
                        return true;
                    }
                    case R.id.item_delete: {
                        if (sharedPreferences.getInt(Pref.IS_VERIFIED, 0) != 1) {
                            Toasty.error(context, "This function is for verified user only", Toast.LENGTH_LONG, true).show();
                        } else {
                            if (mComplaint.getStatus().equals("Pending") || mComplaint.getStatus().equals("Denied")) {
                                openDeleteDialog();
                            } else {
                                Toasty.error(context, "You cannot delete the complaint once the complaint status is in Approved or Resolved", Toast.LENGTH_LONG, true).show();
                            }
                        }
                        return true;
                    }
                }
                return false;
            });
            popupMenu.show();
        });
    }

    private void getEditData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.COMPLAINTS + id + Api.EDIT, response -> {
            try {
                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject complaintJSONObj = object.getJSONObject("data");
                JSONArray typeJSONArray = new JSONArray(object.getString("types"));
                Intent intent = new Intent(context, ComplaintEditActivity.class);
                intent.putExtra(Extra.JSON_OBJECT, complaintJSONObj.toString());
                intent.putExtra(Extra.TYPE_JSON_ARRAY, typeJSONArray.toString());
                intent.putExtra(Extra.MODEL_POSITION, selectedPosition);
                context.startActivity(intent);

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

    private void getData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.COMPLAINTS + id, response -> {
            try {
                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject complaintJSONObj = object.getJSONObject("data");
                Intent intent = new Intent(context, ComplaintViewActivity.class);
                intent.putExtra(Extra.JSON_OBJECT, complaintJSONObj.toString());
                intent.putExtra(Extra.MODEL_POSITION, selectedPosition);
                context.startActivity(intent);

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

        dialogTitle.setText("DELETE COMPLAINT");

        cancel.setOnClickListener(v -> dialog.dismiss());

        delete.setOnClickListener(v -> {
            progressDialog.setMessage("Deleting complaint.....");
            progressDialog.show();
            deleteData();
        });

        dialog.show();
    }

    private void deleteData() {
        StringRequest request = new StringRequest(Request.Method.DELETE, Api.COMPLAINTS + id, response -> {
            list.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            notifyDataSetChanged();
            listAll.clear();
            listAll.addAll(list);

            dialog.dismiss();
            progressDialog.dismiss();
            Toasty.success(context, "Complaint Deleted", Toast.LENGTH_LONG, true).show();

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


    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Complaint> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Complaint mComplaint : listAll){
                    if(mComplaint.getType().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mComplaint.getStatus().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(mComplaint.getId()).contains(constraint.toString().toLowerCase())
                            || mComplaint.getAdminMessage().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mComplaint.getAction().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mComplaint.getReason().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mComplaint.getCreatedAt().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mComplaint.getUpdatedAt().toLowerCase().contains(constraint.toString().toLowerCase())
                    ){
                        filteredList.add(mComplaint);
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
            list.addAll((Collection<? extends Complaint>) results.values);
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

    public class ComplaintsHolder extends RecyclerView.ViewHolder {
        private final ImageButton imgBtnOption;
        private final TextView txtID, txtCreatedAt, txtType, txtReason, txtAction, txtStatus, txtAdminMessage, txtUpdatedAt;
        public ComplaintsHolder(@NonNull View itemView) {
            super(itemView);

            imgBtnOption = itemView.findViewById(R.id.btnOption);

            txtID = itemView.findViewById(R.id.txtID);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtType = itemView.findViewById(R.id.txtType);
            txtReason = itemView.findViewById(R.id.txtReason);
            txtAction = itemView.findViewById(R.id.txtAction);
            txtStatus = itemView.findViewById(R.id.txtStatus);
            txtAdminMessage = itemView.findViewById(R.id.txtAdminMessage);
            txtUpdatedAt = itemView.findViewById(R.id.txtUpdatedAt);
        }
    }

}
