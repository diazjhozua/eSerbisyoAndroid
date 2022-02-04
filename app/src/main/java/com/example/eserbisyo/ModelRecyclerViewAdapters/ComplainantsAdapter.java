package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
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
import com.example.eserbisyo.ModelActivities.CommentActivity;
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.ModelActivities.ComplaintEditActivity;
import com.example.eserbisyo.Models.Comment;
import com.example.eserbisyo.Models.Complainant;
import com.example.eserbisyo.Models.Defendant;
import com.example.eserbisyo.R;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ComplainantsAdapter extends RecyclerView.Adapter<ComplainantsAdapter.ComplainantsHolder>{
    private final Context context;
    private final ArrayList<Complainant> list;
    private final SharedPreferences sharedPreferences;

    /* ID FOR REFERENCE IN UPDATE|DELETE CERTIFICATE */
    private int id;
    private String name;
    private String filePath;
    private Complainant selComplainant;
    private int selectedPosition;

    private JSONObject errorObj = null;

    private ProgressDialog progressDialog;
    private Dialog dialogDel;
    private TextView dialogDelTitle;
    private Button btnDialogDelCancel;

    private Dialog dialogComplainant;
    private ImageView ivCompDiaOperation;
    private TextView txtCompDiaTitle;
    private SignaturePad spCompSignature;
    private ImageView ivSignature;
    private TextView txtSignatureLabel;
    private TextInputLayout layoutCompDiaName;
    private TextInputEditText inputCompDiaName;
    private Button btnCompDiaCancel, btnCompDiaSubmit;


    private Bitmap signatureBitmap;

    public ComplainantsAdapter(Context context, ArrayList<Complainant> list) {
        this.context = context;
        this.list = list;
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS, Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ComplainantsAdapter.ComplainantsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_complainant, parent, false);
        return new ComplainantsAdapter.ComplainantsHolder(v);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull ComplainantsAdapter.ComplainantsHolder holder, @SuppressLint("RecyclerView") int position) {
        Complainant mComplainant = list.get(position);

        if (mComplainant.isCreating()) {
           holder.ivSignature.setImageBitmap(mComplainant.getBitmapSignature());
        } else {
            Picasso.get().load(Api.STORAGE + mComplainant.getFilePath()).fit().error(R.drawable.no_picture).into(holder.ivSignature);
        }

        holder.txtName.setText(mComplainant.getName());

        if (!mComplainant.isModifiable()) {
            holder.btnOption.setVisibility(View.GONE);
        } else {
            holder.btnOption.setVisibility(View.VISIBLE);
            holder.btnOption.setOnClickListener(v -> {
                Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
                PopupMenu popupMenu = new PopupMenu(wrapper, holder.btnOption);
                popupMenu.inflate(R.menu.model_menu);

                popupMenu.setOnMenuItemClickListener(item -> {
                    selectedPosition = position;
                    selComplainant = mComplainant;
                    switch (item.getItemId()) {
                        case R.id.item_edit: {
                            if (selComplainant.isCreating()) {
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

        dialogDelTitle.setText("DELETE COMPLAINANT");

        btnDialogDelCancel.setOnClickListener(v -> dialogDel.dismiss());

        delete.setOnClickListener(v -> {
            progressDialog.setMessage("Deleting complainant.....");
            progressDialog.show();
            if (selComplainant.isCreating()) {
                Toasty.success(context, "Complainant Deleted", Toasty.LENGTH_LONG, true).show();
                progressDialog.hide();
                dialogDel.hide();

                list.remove(selectedPosition);
                notifyItemRemoved(selectedPosition);
                notifyDataSetChanged();
                ComplaintAddActivity.txtComplainantCount.setText("Complainant: " + getItemCount() + " (Total)");
            } else {
                deleteData();
            }

        });

        dialogDel.show();
    }

    private void deleteData() {
        progressDialog.setMessage("Deleting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.DELETE, Api.COMPLAINANTS + selComplainant.getId(), response -> {

            list.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            notifyDataSetChanged();
            ComplaintEditActivity.txtDefendantCount.setText("Complainant: " + getItemCount() + " (Total)");

            progressDialog.dismiss();
            dialogDel.dismiss();
            Toasty.success(context, "Complainant Deleted", Toast.LENGTH_LONG, true).show();

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

        StringRequest request = new StringRequest(Request.Method.GET, Api.COMPLAINANTS + selComplainant.getId() + Api.EDIT, response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject complainantObject = object.getJSONObject("data");

                id = complainantObject.getInt("id");
                name = complainantObject.getString("name");
                filePath = complainantObject.getString("file_path");

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

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    private void openEditDialog() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        dialogComplainant = new Dialog(context);
        dialogComplainant.setContentView(R.layout.dialog_complainant);
        dialogComplainant.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogComplainant.setCancelable(true);

        ivCompDiaOperation = dialogComplainant.findViewById(R.id.ivDialogOperation);
        txtCompDiaTitle = dialogComplainant.findViewById(R.id.txtDialogOperation);
        layoutCompDiaName = dialogComplainant.findViewById(R.id.txtLayoutDialogName);
        inputCompDiaName = dialogComplainant.findViewById(R.id.inputTxtDialogName);
        txtSignatureLabel = dialogComplainant.findViewById(R.id.txtSignatureLabel);
        spCompSignature = dialogComplainant.findViewById(R.id.spSignature);
        ivSignature = dialogComplainant.findViewById(R.id.ivSignature);
        btnCompDiaCancel = dialogComplainant.findViewById(R.id.btnDialogCancel);
        btnCompDiaSubmit = dialogComplainant.findViewById(R.id.btnDialogSubmit);

        Picasso.get().load(R.drawable.modify).fit().into(ivCompDiaOperation);
        txtCompDiaTitle.setText("Edit Complainant");
        btnCompDiaSubmit.setText("Update");

        if (selComplainant.isCreating()) {
            inputCompDiaName.setText(selComplainant.getName());
            spCompSignature.setSignatureBitmap(selComplainant.getBitmapSignature());
        } else {
            txtSignatureLabel.setText("Signature (Long Pressed to re-write the signature)");
            inputCompDiaName.setText(name);
            Picasso.get().load(Api.STORAGE + filePath).fit().error(R.drawable.no_picture).into(ivSignature);

            spCompSignature.setVisibility(View.GONE);
            ivSignature.setVisibility(View.VISIBLE);

            ivSignature.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    spCompSignature.setVisibility(View.VISIBLE);
                    ivSignature.setVisibility(View.GONE);
                    return false;
                }
            });
        }

        btnCompDiaCancel.setOnClickListener(v -> dialogComplainant.dismiss());

        btnCompDiaSubmit.setOnClickListener(v -> {
            if (validateComplainant()) {
                signatureBitmap = null;

                try {
                    signatureBitmap = spCompSignature.getSignatureBitmap();
                } catch (Exception ignored) {
                }

                if (selComplainant.isCreating()) {
                    progressDialog.setMessage("Updating complainant.....");
                    progressDialog.show();

                    Complainant mComplainant = new Complainant(true, true, Objects.requireNonNull(inputCompDiaName.getText()).toString().trim(), signatureBitmap);

                    // Set the updateGenre to the array list
                    list.set(selectedPosition, mComplainant);
                    // Notify the changes
                    notifyItemChanged(selectedPosition);
                    notifyDataSetChanged();

                    Toasty.success(context, "Complainant Updated", Toast.LENGTH_SHORT, true).show();
                    progressDialog.hide();
                    dialogComplainant.hide();
                } else {
                    updateData();
                }
            }
        });

        dialogComplainant.show();

        inputCompDiaName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Objects.requireNonNull(inputCompDiaName.getText()).toString().length()>=5 && Objects.requireNonNull(inputCompDiaName.getText()).toString().length()<=150){
                    layoutCompDiaName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Updating the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.COMPLAINANTS + selComplainant.getId(), response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject complainantJSONObject = object.getJSONObject("data");

                Complainant mComplainant = new Complainant(
                        complainantJSONObject.getInt("id"),
                        complainantJSONObject.getInt("complaint_id"),
                        true, false,
                        complainantJSONObject.getString("name"),
                        complainantJSONObject.getString("signature_picture"),
                        complainantJSONObject.getString("file_path")
                );

                // Set the updateGenre to the array list
                list.set(selectedPosition, mComplainant);
                // Notify the changes
                notifyItemChanged(selectedPosition);
                notifyDataSetChanged();

                Toasty.success(context, "Complainant Updated", Toast.LENGTH_SHORT, true).show();

                progressDialog.dismiss();
                dialogComplainant.dismiss();
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
                map.put("id", String.valueOf(selComplainant.getId()));
                map.put("complaint_id", String.valueOf(selComplainant.getComplaintId()));
                map.put("name", Objects.requireNonNull(inputCompDiaName.getText()).toString().trim());
                map.put("signature", bitmapToString(signatureBitmap));

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

    private boolean validateComplainant() {

        if (selComplainant.isCreating()) {
            if (signatureBitmap == null) {
                Toasty.error(context, "Signature is required", Toasty.LENGTH_LONG, true).show();
                return false;
            }
        }

        if (Objects.requireNonNull(inputCompDiaName.getText()).toString().isEmpty()) {
            layoutCompDiaName.setErrorEnabled(true);
            layoutCompDiaName.setError("Defendant name is required");
            return false;
        }

        if (inputCompDiaName.getText().length() < 5) {
            layoutCompDiaName.setErrorEnabled(true);
            layoutCompDiaName.setError("Required at least 5");
            return false;
        }

        if (Objects.requireNonNull(inputCompDiaName.getText()).toString().length()> 150){
            layoutCompDiaName.setErrorEnabled(true);
            layoutCompDiaName.setError("Required no more than  150 characters");
            return false;
        }
        return true;
    }

    public ArrayList<Complainant> getList() {
        return list;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ComplainantsHolder extends RecyclerView.ViewHolder {
        private final TextView txtName;
        private final ImageView ivSignature;
        private final ImageButton btnOption;

        public ComplainantsHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            btnOption = itemView.findViewById(R.id.btnOption);
            ivSignature = itemView.findViewById(R.id.ivSignature);
        }
    }

    private String bitmapToString(Bitmap bitmap) {
        if (bitmap!=null){
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
            byte [] array = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(array, Base64.DEFAULT);
        }

        return "";
    }

}
