package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.ModelActivities.ComplaintAddActivity;
import com.example.eserbisyo.Models.Certificate;
import com.example.eserbisyo.Models.Form;
import com.example.eserbisyo.OrderActivity.CreateOrderActivity;
import com.example.eserbisyo.OrderActivity.FormAddActivity;
import com.example.eserbisyo.OrderActivity.FormEditActivity;
import com.example.eserbisyo.R;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class FormsAdapter extends RecyclerView.Adapter<FormsAdapter.FormsHolder>{
    private final Context context;
    private final ArrayList<Form> list;
    private final ArrayList<Form> listAll;

    private ProgressDialog progressDialog;
    private Dialog dialogDel;
    private TextView dialogDelTitle;
    private Button btnDialogDelCancel;

    private Form selForm;
    private int selPos;

    public FormsAdapter(Context context, ArrayList<Form> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public FormsAdapter.FormsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_form, parent, false);
        return new FormsAdapter.FormsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull FormsAdapter.FormsHolder holder, @SuppressLint("RecyclerView") int position) {
        Form mForm = list.get(position);
        holder.txtCertificate.setText(mForm.getCertName() + "\n ( ₱ " + mForm.getCertPrice()+" )");

        holder.imageButton.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            PopupMenu popupMenu = new PopupMenu(wrapper, holder.imageButton);
            popupMenu.inflate(R.menu.model_menu);

            popupMenu.setOnMenuItemClickListener(item -> {
                selForm = mForm;
                selPos = position;
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

    @SuppressLint("SetTextI18n")
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

        dialogDelTitle.setText("DELETE FORM");

        btnDialogDelCancel.setOnClickListener(v -> dialogDel.dismiss());

        delete.setOnClickListener(v -> {
            progressDialog.setMessage("Deleting form.....");
            progressDialog.show();
            Toasty.success(context, "Form Deleted", Toasty.LENGTH_LONG, true).show();
            progressDialog.hide();
            dialogDel.hide();

            list.remove(selPos);
            notifyItemRemoved(selPos);
            notifyDataSetChanged();
            CreateOrderActivity.txtCertificateCount.setText("Certificate Requested: " + getItemCount() + " (Total)");
            CreateOrderActivity.txtTotalCertPrice.setText("Total Certificate Price: ₱ " + getTotalPrice());

            CreateOrderActivity.totalCertPrice = getTotalPrice();
            CreateOrderActivity.totalFee = CreateOrderActivity.totalFee - selForm.getCertPrice();
            CreateOrderActivity.txtTotalFee.setText("Total fee: ₱ " + CreateOrderActivity.totalFee);
        });

        dialogDel.show();
    }

    private void getData() {
        Intent intent = new Intent(context, FormEditActivity.class);
        intent.putExtra(Extra.MODEL_POSITION, selPos);
        intent.putExtra(Extra.MODEL, selForm);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public double getTotalPrice() {
        double totalPrice = 0.0;
        for(int i = 0; i < list.size(); i++){
            totalPrice = totalPrice + list.get(i).getCertPrice();
        }

        return totalPrice;
    }

    public boolean checkIfExists(int id) {
        for(int i = 0; i < list.size(); i++){
            if (list.get(i).getCertId() == id) {
                return true;

            }
        }
        return false;
    }

    public class FormsHolder extends RecyclerView.ViewHolder {
        private final TextView txtCertificate;
        private final ImageButton imageButton;

        public FormsHolder(@NonNull View itemView) {
            super(itemView);
            txtCertificate = itemView.findViewById(R.id.txtCertificate);
            imageButton = itemView.findViewById(R.id.btnOption);
        }
    }
}
