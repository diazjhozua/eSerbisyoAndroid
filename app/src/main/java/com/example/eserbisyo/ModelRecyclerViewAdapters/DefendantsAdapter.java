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
import android.widget.ImageView;
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
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.ModelActivities.ComplaintEditActivity;
import com.example.eserbisyo.Models.Announcement;
import com.example.eserbisyo.Models.Complainant;
import com.example.eserbisyo.Models.Defendant;
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

import es.dmoral.toasty.Toasty;

public class DefendantsAdapter extends RecyclerView.Adapter<DefendantsAdapter.DefendantsHolder>{
    private final Context context;
    private final ArrayList<Defendant> list;
    private final ArrayList<Defendant> listAll;

    private final SharedPreferences sharedPreferences;


    /* ID FOR REFERENCE IN UPDATE|DELETE CERTIFICATE */
    private int id;
    private String name;
    private Defendant selDefendant;
    private int selectedPosition;

    private JSONObject errorObj = null;

    private ProgressDialog progressDialog;

    private Dialog dialogDel;
    private TextView dialogDelTitle;
    private Button btnDialogDelCancel;

    /* Defendant Dialog Variable */
    private Dialog dialogDefendant;
    private ImageView ivDefDiaOperation;
    private TextView txtDefDiaTitle;
    private TextInputLayout layoutDefDiaName;
    private TextInputEditText inputDefDiaName;
    private Button btnDefDiaCancel, btnDefDiaSubmit;


    public DefendantsAdapter(Context context, ArrayList<Defendant> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public DefendantsAdapter.DefendantsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_defendant, parent, false);
        return new DefendantsAdapter.DefendantsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DefendantsAdapter.DefendantsHolder holder, @SuppressLint("RecyclerView") int position) {

        Defendant mDefendant = list.get(position);

        holder.txtName.setText(mDefendant.getName());

        if (!mDefendant.isModifiable()) {
            holder.btnOption.setVisibility(View.GONE);
        } else {
            holder.btnOption.setVisibility(View.VISIBLE);
            holder.btnOption.setOnClickListener(v -> {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popupMenu = new PopupMenu(wrapper, holder.btnOption);
                popupMenu.inflate(R.menu.model_menu);

                popupMenu.setOnMenuItemClickListener(item -> {
                    selectedPosition = position;
                    selDefendant = mDefendant;
                    switch (item.getItemId()) {
                        case R.id.item_edit: {
                            if (selDefendant.isCreating()) {
                                openEditDialog();
                            } else {
                                getEditData();
                            }

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
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void openDeleteDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        dialogDel = new Dialog(context);
        dialogDel.setContentView(R.layout.dialog_confirmation);
        dialogDel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDel.setCancelable(true);

        dialogDelTitle= dialogDel.findViewById(R.id.txtDialogConfirmationTitle);
        Button delete = dialogDel.findViewById(R.id.btnDelete);
        btnDialogDelCancel = dialogDel.findViewById(R.id.btnCancel);

        dialogDelTitle.setText("DELETE DEFENDANT");

        btnDialogDelCancel.setOnClickListener(v -> dialogDel.dismiss());

        delete.setOnClickListener(v -> {

            if (selDefendant.isCreating()) {
                progressDialog.setMessage("Deleting defendant.....");
                progressDialog.show();

                Toasty.success(context, "Defendant Deleted", Toasty.LENGTH_LONG, true).show();
                progressDialog.hide();
                dialogDel.hide();

                list.remove(selectedPosition);
                notifyItemRemoved(selectedPosition);
                notifyDataSetChanged();
                ComplaintAddActivity.txtDefendantCount.setText("Defendant: " + getItemCount() + " (Total)");
            } else {
                deleteData();
            }

        });

        dialogDel.show();
    }

    private void deleteData() {
        progressDialog.setMessage("Deleting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.DELETE, Api.DEFENDANTS + selDefendant.getId(), response -> {

            list.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            notifyDataSetChanged();
            ComplaintEditActivity.txtDefendantCount.setText("Defendant: " + getItemCount() + " (Total)");

            progressDialog.dismiss();
            dialogDel.dismiss();
            Toasty.success(context, "Defendant Deleted", Toast.LENGTH_LONG, true).show();

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

    private void getEditData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.DEFENDANTS + selDefendant.getId() + Api.EDIT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject commentObject = object.getJSONObject("data");

                id = commentObject.getInt("id");
                name = commentObject.getString("name");

                progressDialog.dismiss();

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

        dialogDefendant = new Dialog(context);
        dialogDefendant.setContentView(R.layout.dialog_defendant);
        dialogDefendant.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDefendant.setCancelable(true);

        ivDefDiaOperation = dialogDefendant.findViewById(R.id.ivDialogOperation);
        txtDefDiaTitle = dialogDefendant.findViewById(R.id.txtDialogOperation);
        layoutDefDiaName = dialogDefendant.findViewById(R.id.txtLayoutDialogName);
        inputDefDiaName = dialogDefendant.findViewById(R.id.inputTxtDialogName);
        btnDefDiaCancel = dialogDefendant.findViewById(R.id.btnDialogCancel);
        btnDefDiaSubmit= dialogDefendant.findViewById(R.id.btnDialogSubmit);

        Picasso.get().load(R.drawable.modify).fit().into(ivDefDiaOperation);
        txtDefDiaTitle.setText("Edit Defendant");
        btnDefDiaSubmit.setText("Update");

        if (selDefendant.isCreating()) {
            inputDefDiaName.setText(selDefendant.getName());
        } else {
            inputDefDiaName.setText(name);
        }

        btnDefDiaCancel.setOnClickListener(v -> dialogDefendant.dismiss());

        btnDefDiaSubmit.setOnClickListener(v -> {
            if (validateDefendant()) {
                if (selDefendant.isCreating()) {
                    progressDialog.setMessage("Updating defendant.....");
                    progressDialog.show();

                    Defendant mDefendant = new Defendant(1, 1, true, true, Objects.requireNonNull(inputDefDiaName.getText()).toString().trim());

                    // Set the updateGenre to the array list
                    list.set(selectedPosition, mDefendant);
                    // Notify the changes
                    notifyItemChanged(selectedPosition);
                    notifyDataSetChanged();

                    Toasty.success(context, "Defendant Updated", Toast.LENGTH_SHORT, true).show();
                    progressDialog.hide();
                    dialogDefendant.hide();
                } else {
                    updateData();
                }
            }
        });

        inputDefDiaName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputDefDiaName.getText()).toString().length()>=5 && Objects.requireNonNull(inputDefDiaName.getText()).toString().length()<=150){
                    layoutDefDiaName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        dialogDefendant.show();
    }

    private void updateData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Updating the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.DEFENDANTS + selDefendant.getId(), response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject defendantJSONObject = object.getJSONObject("data");

                Defendant mDefendant = new Defendant(
                        defendantJSONObject.getInt("id"),
                        defendantJSONObject.getInt("complaint_id"),
                        true, false,
                        defendantJSONObject.getString("name")
                );

                // Set the updateGenre to the array list
                list.set(selectedPosition, mDefendant);
                // Notify the changes
                notifyItemChanged(selectedPosition);
                notifyDataSetChanged();

                Toasty.success(context, "Defendant Updated", Toast.LENGTH_SHORT, true).show();

                progressDialog.dismiss();
                dialogDefendant.dismiss();
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

            // add params
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> map = new HashMap<>();
                map.put("id", String.valueOf(selDefendant.getId()));
                map.put("complaint_id", String.valueOf(selDefendant.getComplaintId()));
                map.put("name", inputDefDiaName.getText().toString().trim());
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


    private boolean validateDefendant() {
        if (Objects.requireNonNull(inputDefDiaName.getText()).toString().isEmpty()) {
            layoutDefDiaName.setErrorEnabled(true);
            layoutDefDiaName.setError("Defendant name is required");
            return false;
        }

        if (inputDefDiaName.getText().length() < 5) {
            layoutDefDiaName.setErrorEnabled(true);
            layoutDefDiaName.setError("Required at least 5");
            return false;
        }

        if (Objects.requireNonNull(inputDefDiaName.getText()).toString().length()> 150){
            layoutDefDiaName.setErrorEnabled(true);
            layoutDefDiaName.setError("Required no more than  150 characters");
            return false;
        }
        return true;
    }

    public ArrayList<Defendant> getList() {
        return list;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class DefendantsHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final ImageButton btnOption;
        public DefendantsHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtName);
            btnOption = itemView.findViewById(R.id.btnOption);
        }
    }
}
