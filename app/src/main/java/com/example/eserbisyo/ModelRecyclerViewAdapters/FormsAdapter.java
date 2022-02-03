package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.Models.Certificate;
import com.example.eserbisyo.Models.Form;
import com.example.eserbisyo.R;

import java.util.ArrayList;

public class FormsAdapter extends RecyclerView.Adapter<FormsAdapter.FormsHolder>{
    private final Context context;
    private final ArrayList<Form> list;
    private final ArrayList<Form> listAll;

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

    private void openDeleteDialog() {

    }

    private void getData() {

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
