package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Models.Complaint;
import com.example.eserbisyo.Models.Order;
import com.example.eserbisyo.Models.Ordinance;
import com.example.eserbisyo.R;

import java.util.ArrayList;
import java.util.Collection;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersHolder> {

    private final Context context;
    private final ArrayList<Order> list;
    private final ArrayList<Order> listAll;


    public OrdersAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public OrdersAdapter.OrdersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order, parent, false);
        return new OrdersAdapter.OrdersHolder(view);
    }
    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull OrdersAdapter.OrdersHolder holder, int position) {
        Order mOrder = list.get(position);

        holder.txtID.setText("Order # " + mOrder.getId());
        holder.txtCreatedAt.setText(mOrder.getCreatedAt());
        holder.txtOrderType.setText("Order type: " + mOrder.getOrderType());
        holder.txtOrderStatus.setText("Order status: " + mOrder.getOrderStatus());

        holder.txtReceivedAt.setText(((mOrder.getOrderStatus().equals("Received")) ? "Received at: " : "Receiving at: " )+
                mOrder.getReceivedAt());

        holder.txtTotalPrice.setText("Total Price:  P " + mOrder.getTotalPrice());

        if (mOrder.getOrderType().equals("Delivery")) {
            holder.txtTotalPrice.setText("Delivery Fee:  P " + mOrder.getDeliveryFee());
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

        holder.txtApplicationStatus.setText(mOrder.getApplicationStatus());
        holder.txtAdminMessage.setText("Admin Message: " + mOrder.getAdminMessage());
        holder.txtUpdatedAt.setText("Responded At: " + mOrder.getUpdatedAt());
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
                txtReceivedAt, txtTotalPrice, txtDeliveryFee, txtApplicationStatus, txtAdminMessage, txtUpdatedAt;

        public OrdersHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.cardOrder);

            txtID = itemView.findViewById(R.id.txtID);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
            txtOrderType = itemView.findViewById(R.id.txtOrderType);
            txtOrderStatus = itemView.findViewById(R.id.txtOrderStatus);
            txtReceivedAt = itemView.findViewById(R.id.txtReceivedDate);
            txtTotalPrice = itemView.findViewById(R.id.txtPrice);
            txtDeliveryFee = itemView.findViewById(R.id.txtDeliveryFee);
            txtApplicationStatus = itemView.findViewById(R.id.txtApplicationStatus);
            txtAdminMessage = itemView.findViewById(R.id.txtAdminMessage);
            txtUpdatedAt = itemView.findViewById(R.id.txtUpdatedAt);

        }
    }
}
