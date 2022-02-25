package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
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
import com.example.eserbisyo.ModelActivities.CommentActivity;
import com.example.eserbisyo.ModelActivities.ComplaintViewActivity;
import com.example.eserbisyo.Models.Complaint;
import com.example.eserbisyo.Models.Order;
import com.example.eserbisyo.Models.Ordinance;
import com.example.eserbisyo.OrderActivity.OrderViewActivity;
import com.example.eserbisyo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersHolder> {

    private final Context context;
    private final ArrayList<Order> list;
    private final ArrayList<Order> listAll;

    private int id, selectedPosition;

    private JSONObject errorObj = null;
    private final SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    private Dialog deleteDialog;
    private Button cancel;
    private TextView dialogTitle;


    public OrdersAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public OrdersAdapter.OrdersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order, parent, false);
        return new OrdersAdapter.OrdersHolder(view);
    }
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.OrdersHolder holder, @SuppressLint("RecyclerView") int position) {
        Order mOrder = list.get(position);

        holder.txtID.setText("Order # " + mOrder.getId());
        holder.txtCreatedAt.setText(mOrder.getCreatedAt());
        holder.txtOrderType.setText("Order type: " + mOrder.getOrderType());
        holder.txtOrderStatus.setText("Order status: " + mOrder.getOrderStatus());

        holder.txtReceiveDate.setText("Received Date: " + ((mOrder.getReceivedAt().equals("null")) ? "Not yet set" : mOrder.getReceivedAt()));
        holder.txtPickupDate.setText("Pickup Date: " + ((mOrder.getPickupAt().equals("null")) ?  "Not yet set" : mOrder.getPickupAt()));


        if (mOrder.getApplicationStatus().equals("Denied")) {
            holder.txtOverallStatus.setText("DENIED");
            holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.firebrick));
        } else if (mOrder.getApplicationStatus().equals("Approved")) {
            if (mOrder.getOrderStatus().equals("DNR")) {
                holder.txtOverallStatus.setText("You did not receive this certificate (DNR)");
                holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.firebrick));
            } else if (mOrder.getOrderStatus().equals("Received")) {
                holder.txtOverallStatus.setText("You have receive this document (Received Date: " + mOrder.getReceivedAt() + ")");
                holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
            } else if (mOrder.getOrderType().equals("Pickup")) {
                holder.txtOverallStatus.setText("APPROVED \n (RECEIVED THE ITEM AT THE BARANGAY AFTER OR ON THE SPECIFIED DATE:  " + mOrder.getPickupAt() + ")");
                holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
            } else if (mOrder.getOrderType().equals("Delivery")) {
                if (mOrder.getmBiker() == null || mOrder.getOrderStatus().equals("Waiting")) {
                    holder.txtOverallStatus.setText("APPROVED \n (WAITING FOR ANY BIKER TO PICKUP YOUR ORDER). (YOU WILL RECEIVE THE ITEM AFTER OR ON THE SPECIFIED DATE: " + mOrder.getPickupAt() + ")" );
                    holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
                } else if (mOrder.getOrderStatus().equals("Accepted")){
                    holder.txtOverallStatus.setText("ORDER ACCEPTED TO DELIVER BY THE BIKER (YOU WILL RECEIVE THE ITEM AFTER OR ON THE SPECIFIED DATE: " + mOrder.getPickupAt() + ")" );
                } else if (mOrder.getOrderStatus().equals("On-Going")) {
                    holder.txtOverallStatus.setText("BIKER IS DELIVERING YOUR ORDER (YOU WILL RECEIVE THE ITEM AFTER OR ON THE SPECIFIED DATE: "+ mOrder.getPickupAt() + ")" );
                    holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.infoColor));
                }
            }
        }

        holder.txtTotalPrice.setText("Total Price:  ₱ " + mOrder.getTotalPrice());

        if (mOrder.getOrderType().equals("Delivery")) {
            holder.txtDeliveryFee.setText("Delivery Fee:  ₱ " + mOrder.getDeliveryFee());
        } else {
            holder.txtDeliveryFee.setVisibility(View.GONE);
        }

        switch (mOrder.getApplicationStatus()) {
            case "Pending":
                holder.txtApplicationStatus.setTextColor(context.getResources().getColor(R.color.warningColor));
                holder.txtAdminMessage.setVisibility(View.GONE);
                holder.txtUpdatedAt.setVisibility(View.GONE);
                break;
            case "Denied":
                holder.txtApplicationStatus.setTextColor(context.getResources().getColor(R.color.firebrick));
                break;
            case "Approved":
                holder.txtApplicationStatus.setTextColor(context.getResources().getColor(R.color.primaryColor));
                break;
            case "Cancelled":
                holder.txtApplicationStatus.setTextColor(context.getResources().getColor(R.color.teal_700));
                break;
        }

        if (!mOrder.getApplicationStatus().equals("Pending")) {
            holder.imgBtnOption.setVisibility(View.GONE);
        }
        holder.imgBtnOption.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, holder.imgBtnOption);
            popupMenu.inflate(R.menu.model_menu3);

            popupMenu.setOnMenuItemClickListener(item -> {
                id = mOrder.getId();
                selectedPosition = position;
                switch (item.getItemId()) {
                    case R.id.item_delete: {
                        openDeleteDialog();
                        return true;
                    }
                }
                return false;
            });
            popupMenu.show();
        });

        holder.txtApplicationStatus.setText(mOrder.getApplicationStatus());
        holder.txtAdminMessage.setText("Admin Message: " + mOrder.getAdminMessage());
        holder.txtUpdatedAt.setText("Responded At: " + mOrder.getUpdatedAt());

        holder.card.setOnClickListener(view -> {
            id = mOrder.getId();
            selectedPosition = position;
            getData();
        });
    }

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

        dialogTitle.setText("DELETE ORDER");

        cancel.setOnClickListener(v -> deleteDialog.dismiss());

        delete.setOnClickListener(v -> {
            progressDialog.setMessage("Deleting order.....");
            progressDialog.show();
            deleteData();
        });

        deleteDialog.show();
    }

    private void deleteData() {
        StringRequest request = new StringRequest(Request.Method.DELETE, Api.ORDERS + "/" +id, response -> {

            list.remove(selectedPosition);
            notifyItemRemoved(selectedPosition);
            notifyDataSetChanged();
            listAll.clear();
            listAll.addAll(list);

            progressDialog.dismiss();
            deleteDialog.dismiss();
            Toasty.success(context, "Order Deleted", Toast.LENGTH_LONG, true).show();

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



    private void getData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.ORDERS +"/"+ id, response -> {
            try {
                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject jsonObject = object.getJSONObject("data");
                Intent intent = new Intent(context, OrderViewActivity.class);
                intent.putExtra(Extra.JSON_OBJECT, jsonObject.toString());
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

        request.setRetryPolicy(new DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(context));
        queue.add(request);
    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Order> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Order mOrder : listAll){
                    if(mOrder.getCreatedAt().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mOrder.getOrderType().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(mOrder.getId()).contains(constraint.toString().toLowerCase())
                            || mOrder.getAdminMessage().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mOrder.getOrderStatus().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mOrder.getApplicationStatus().toLowerCase().contains(constraint.toString().toLowerCase())
                            || mOrder.getUpdatedAt().toLowerCase().contains(constraint.toString().toLowerCase())
                    ){
                        filteredList.add(mOrder);
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
            list.addAll((Collection<? extends Order>) results.values);
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

    public class OrdersHolder extends RecyclerView.ViewHolder {
        private final CardView card;
        private final TextView txtID, txtCreatedAt, txtOrderType, txtOrderStatus,
                txtPickupDate, txtReceiveDate, txtTotalPrice, txtDeliveryFee, txtApplicationStatus, txtAdminMessage, txtUpdatedAt;

        private final ImageButton imgBtnOption;

        private final TextView txtOverallStatus;

        public OrdersHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardOrder);

            txtID = itemView.findViewById(R.id.txtID);

            imgBtnOption = itemView.findViewById(R.id.btnOption);

            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtOrderType = itemView.findViewById(R.id.txtOrderType);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtPickupDate = itemView.findViewById(R.id.txtPickupDate);
            txtReceiveDate = itemView.findViewById(R.id.txtReceivedDate);
            txtTotalPrice = itemView.findViewById(R.id.txtPrice);
            txtDeliveryFee = itemView.findViewById(R.id.txtDeliveryFee);
            txtApplicationStatus = itemView.findViewById(R.id.txtApplicationStatus);
            txtAdminMessage = itemView.findViewById(R.id.txtAdminMessage);
            txtUpdatedAt = itemView.findViewById(R.id.txtUpdatedAt);

            txtOverallStatus = itemView.findViewById(R.id.txtOverallStatus);

        }
    }
}
