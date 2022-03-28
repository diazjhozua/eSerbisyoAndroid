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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.eserbisyo.Biker.BikerOrderActivity;
import com.example.eserbisyo.Biker.BikerTransactionFragment;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.ModelActivities.CommentActivity;
import com.example.eserbisyo.ModelActivities.Profile.DocumentActivity;
import com.example.eserbisyo.Models.Order;
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

public class BikerOrdersAdapter extends RecyclerView.Adapter<BikerOrdersAdapter.BikerOrdersHolder>{

    private final Context context;
    private final ArrayList<Order> list;
    private final ArrayList<Order> listAll;

    private Order selOrder;

    private int id, selectedPosition;

    private JSONObject errorObj = null;
    private final SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private Dialog confirmationDialog;

    public BikerOrdersAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public BikerOrdersAdapter.BikerOrdersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_biker_order, parent, false);
        return new BikerOrdersAdapter.BikerOrdersHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull BikerOrdersAdapter.BikerOrdersHolder holder, @SuppressLint("RecyclerView") int position) {
        Order mOrder = list.get(position);

        if (mOrder.getOrderStatus().equals("Received") && mOrder.getPaymentStatus().equals("Pending")) {
            holder.txtOverallStatus.setText("DELIVERED BUT NOT YET PROCESSED. PLEASE GO TO THE BARANGAY TO PROCESSED THE PAYMENT");
            holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.infoColor));
        } else if (mOrder.getOrderStatus().equals("On-Going")) {
            holder.txtOverallStatus.setText("THE ADMINISTRATOR MARKED THIS ORDER AS ON-GOING. DELIVER THIS ORDER IMMEDIATELY");
            holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.infoColor));
        } else if (mOrder.getOrderStatus().equals("Received") && mOrder.getPaymentStatus().equals("Received")) {
            holder.txtOverallStatus.setText("ORDER TRANSACTION COMPLETE");
            holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
        } else if (mOrder.getOrderStatus().equals("DNR")) {
            if (mOrder.getReturnedStatus().equals("No")) {
                holder.txtOverallStatus.setText("THE RESIDENT DID NOT RECEIVE THE DOCUMENT (RETURN THE ORDER TO THE BARANGAY)");
            } else {
                holder.txtOverallStatus.setText("THE RESIDENT DID NOT RECEIVE THE DOCUMENT (THE ORDER IS RETURNED TO THE BARANGAY OFFICE)");
            }
            holder.txtOverallStatus.setBackgroundColor(context.getResources().getColor(R.color.firebrick));
        }

        holder.txtID.setText("Order # " + mOrder.getId());
        holder.txtCreatedAt.setText(mOrder.getCreatedAt());
        holder.txtDeliveryDate.setText("Delivery Date: " + mOrder.getPickupAt());
        holder.txtTotalPrice.setText("Total Price:  ₱ " + mOrder.getTotalPrice());
        holder.txtDeliveryFee.setText("Delivery Fee:  ₱ " + mOrder.getDeliveryFee());
        holder.txtDeliveredAt.setText("Delivered At: " + mOrder.getReceivedAt());

        if (!mOrder.getBooked()) {
            holder.btnDeliver.setVisibility(View.VISIBLE);
            holder.txtOverallStatus.setVisibility(View.GONE);

            holder.btnDeliver.setOnClickListener(view -> {
                selOrder = mOrder;
                openConfirmationDialog();
            });
        } else {

            holder.card.setOnClickListener(view -> {
//                selOrder = mOrder;
//                selectedPosition = position;
//                getData();

                Intent intent = new Intent(context, BikerOrderActivity.class);
                intent.putExtra(Extra.MODEL_ID, mOrder.getId());
                context.startActivity(intent);
            });

        }

    }

    private void getData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.BIKER_GET_ORDER_DETAILS + "/" + selOrder.getId(), response -> {
            try {
                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject jsonObject = object.getJSONObject("data");
                Intent intent = new Intent(context, BikerOrderActivity.class);
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

        RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(context));
        queue.add(request);

    }

    @SuppressLint("SetTextI18n")
    private void openConfirmationDialog() {
        confirmationDialog = new Dialog(context);
        confirmationDialog.setContentView(R.layout.dialog_confirmation);
        confirmationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirmationDialog.setCancelable(true);

        TextView txtTitle = confirmationDialog.findViewById(R.id.txtDialogConfirmationTitle);
        TextView txtDesc = confirmationDialog.findViewById(R.id.txtDialogConfirmationDesc);

        Button btnConfirm = confirmationDialog.findViewById(R.id.btnDelete);
        Button btnCancel = confirmationDialog.findViewById(R.id.btnCancel);

        txtTitle.setText("Delivery Confirmation");
        txtDesc.setText("Are you sure you want to deliver this order? You cannot cancel the order once you accept this delivery request");
        btnConfirm.setText("Confirm");

        btnCancel.setOnClickListener(v -> confirmationDialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            deliverOrder();
        });

        confirmationDialog.show();

    }

    private void deliverOrder() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Booking the order please wait.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.PUT, Api.BIKER_BOOKED_ORDER + "/"+ selOrder.getId(), response -> {
            progressDialog.dismiss();
            confirmationDialog.dismiss();
            Toasty.success(context, "Order has been selected to delivery", Toast.LENGTH_LONG, true).show();
            ((HomeActivity)context).switchFragment(new BikerTransactionFragment());

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

    private void selectOrder() {

    }

    @Override
    public int getItemCount() {
        return list.size();
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
                            || String.valueOf(mOrder.getId()).contains(constraint.toString().toLowerCase())
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


    public class BikerOrdersHolder extends RecyclerView.ViewHolder {
        private final CardView card;
        private final TextView txtID, txtCreatedAt, txtDeliveryDate, txtTotalPrice, txtDeliveryFee, txtDeliveredAt;
        private final Button btnDeliver;

        private final TextView txtOverallStatus;

        public BikerOrdersHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardOrder);

            txtID = itemView.findViewById(R.id.txtID);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtDeliveryDate = itemView.findViewById(R.id.txtDeliveryDate);
            txtDeliveredAt = itemView.findViewById(R.id.txtDeliveredAt);
            txtTotalPrice = itemView.findViewById(R.id.txtPrice);
            txtDeliveryFee = itemView.findViewById(R.id.txtDeliveryFee);

            btnDeliver = itemView.findViewById(R.id.btnDeliver);

            txtOverallStatus = itemView.findViewById(R.id.txtOverallStatus);

        }
    }
}
