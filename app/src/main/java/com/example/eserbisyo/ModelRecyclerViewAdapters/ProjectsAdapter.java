package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.eserbisyo.ModelActivities.Profile.DocumentActivity;
import com.example.eserbisyo.ModelActivities.Profile.OrdinanceActivity;
import com.example.eserbisyo.ModelActivities.Profile.ProjectActivity;
import com.example.eserbisyo.Models.Ordinance;
import com.example.eserbisyo.Models.Project;
import com.example.eserbisyo.R;
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

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsHolder>{

    private final Context context;
    private final ArrayList<Project> list;
    private final ArrayList<Project> listAll;
    private final SharedPreferences sharedPreferences;
    private int id;

    private JSONObject errorObj = null;
    private ProgressDialog progressDialog;

    public ProjectsAdapter(Context context, ArrayList<Project> list) {
        this.context = context;
        this.list = list;
        this.listAll = new ArrayList<>(list);
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public ProjectsAdapter.ProjectsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_project, parent, false);
        return new ProjectsAdapter.ProjectsHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProjectsAdapter.ProjectsHolder holder, int position) {
        Project project = list.get(position);
        if (project.getType().getId() == 0) {
            holder.txtType.setText("Type: " + project.getCustomType());
        } else {
            holder.txtType.setText("Type: " + project.getType().getName());
        }

        holder.txtName.setText("Project: " + project.getName());
        holder.txtCost.setText("Budget Cost: ₱" + project.getCost());
        holder.txtStart.setText("Project Start: " + project.getProjectStart());
        holder.txtEnd.setText("Project End: " + project.getProjectEnd());
        holder.txtLoc.setText("Project Location: " + project.getLocation());
        holder.txtDesc.setText(project.getDescription());
        holder.txtCreatedAt.setText(project.getCreatedAt());

        holder.txtName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Intent intent= new Intent(context, ViewPDFActivity.class);
//                intent.putExtra("pdf_path", project.getFilePath());
//                context.startActivity(intent);

                Intent intent = new Intent(context, ProjectActivity.class);
                intent.putExtra(Extra.MODEL_ID, project.getId());
                context.startActivity(intent);
            }
        });
    }

    private void getData() {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);

        progressDialog.setMessage("Getting the data.....");
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.GET, Api.PROJECTS + "/" + id, response -> {
            try {

                progressDialog.dismiss();

                JSONObject object = new JSONObject(response);
                JSONObject projectJSONObj = object.getJSONObject("data");
                Intent intent = new Intent(context, ProjectActivity.class);
                intent.putExtra(Extra.JSON_OBJECT, projectJSONObj.toString());
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

    /* SEARCH FUNCTION */
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Project> filteredList = new ArrayList<>();
            if (constraint.toString().isEmpty()){
                filteredList.addAll(listAll);
            } else {
                for (Project project : listAll){
                    if(project.getType().getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getCustomType().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getName().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getProjectStart().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getProjectEnd().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getLocation().toLowerCase().contains(constraint.toString().toLowerCase())
                            || project.getDescription().toLowerCase().contains(constraint.toString().toLowerCase())
                            || String.valueOf(project.getId()).contains(constraint.toString().toLowerCase())
                            || String.valueOf(project.getCost()).contains(constraint.toString().toLowerCase())){
                        filteredList.add(project);
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
            list.addAll((Collection<? extends Project>) results.values);
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

    public class ProjectsHolder extends RecyclerView.ViewHolder {
        private final TextView txtName, txtType, txtCost, txtStart, txtEnd, txtLoc, txtDesc, txtCreatedAt;

        public ProjectsHolder(@NonNull View itemView) {
            super(itemView);

            txtName = itemView.findViewById(R.id.txtProjectName);
            txtType = itemView.findViewById(R.id.txtProjectType);
            txtCost = itemView.findViewById(R.id.txtProjectCost);
            txtStart = itemView.findViewById(R.id.txtProjectStart);
            txtEnd = itemView.findViewById(R.id.txtProjectEnd);
            txtLoc = itemView.findViewById(R.id.txtProjectLocation);
            txtDesc = itemView.findViewById(R.id.txtProjectDesc);
            txtCreatedAt = itemView.findViewById(R.id.txtProjectCreatedAt);
        }
    }
}
