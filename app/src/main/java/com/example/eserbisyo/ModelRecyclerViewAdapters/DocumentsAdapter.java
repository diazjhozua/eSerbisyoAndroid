package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.example.eserbisyo.ModelActivities.Profile.AnnouncementActivity;
import com.example.eserbisyo.ModelActivities.Profile.DocumentActivity;
import com.example.eserbisyo.ModelActivities.Profile.OrdinanceActivity;
import com.example.eserbisyo.Models.Document;
import com.example.eserbisyo.Models.Feedback;
import com.example.eserbisyo.R;
import com.example.eserbisyo.ViewImageActivity;
import com.example.eserbisyo.ViewPDFActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class DocumentsAdapter extends RecyclerView.Adapter<DocumentsAdapter.DocumentsHolder>{
    private final Context context;
    private final ArrayList<Document> list;
    private final ArrayList<Document> listAll;
    private final SharedPreferences sharedPreferences;
    private int id;

    private JSONObject errorObj = null;
    private ProgressDialog progressDialog;

    public DocumentsAdapter(Context context, ArrayList<Document> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public DocumentsAdapter.DocumentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_document, parent, false);
        return new DocumentsAdapter.DocumentsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DocumentsAdapter.DocumentsHolder holder, int position) {
        Document document = list.get(position);
        if (document.getType().getId() == 0) {
            holder.txtType.setText("Document: " + document.getCustomType());
        } else {
            holder.txtType.setText("Document: " + document.getType().getName());
        }

        holder.txtType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent= new Intent(context, ViewPDFActivity.class);
//                intent.putExtra("pdf_path", document.getFilePath());
//                context.startActivity(intent);

                Intent intent = new Intent(context, DocumentActivity.class);
                intent.putExtra(Extra.MODEL_ID, document.getId());
                context.startActivity(intent);
            }
        });

        holder.txtYear.setText("Year: " + document.getYear());

        holder.txtDesc.setText(document.getDescription());
        holder.txtCreatedAt.setText(document.getCreatedAt());

    }

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Document> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Document document : listAll){
                    if(document.getType().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || document.getCustomType().toLowerCase().contains(constraint.toString().toLowerCase())
                            || document.getYear().toLowerCase().contains(constraint.toString().toLowerCase())
                            || document.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(document.getId()).contains(constraint.toString().toLowerCase())){
                        filteredList.add(document);
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
            list.addAll((Collection<? extends Document>) results.values);
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

    public static class DocumentsHolder extends RecyclerView.ViewHolder {
        private final TextView txtType, txtYear, txtDesc, txtCreatedAt;

        public DocumentsHolder(@NonNull View itemView) {
            super(itemView);
            txtType = itemView.findViewById(R.id.txtDocumentType);
            txtYear = itemView.findViewById(R.id.txtDocumentYear);
            txtDesc = itemView.findViewById(R.id.txtDocumentDesc);
            txtCreatedAt = itemView.findViewById(R.id.txtDocumentCreatedAt);
        }
    }
}
