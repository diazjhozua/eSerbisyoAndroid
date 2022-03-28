package com.example.eserbisyo.ModelRecyclerViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eserbisyo.AccountActivities.UserVerificationActivity;
import com.example.eserbisyo.Biker.BikerOrderActivity;
import com.example.eserbisyo.Biker.BikerViewRegistrationActivity;
import com.example.eserbisyo.Constants.Api;
import com.example.eserbisyo.Constants.Extra;
import com.example.eserbisyo.Constants.Pref;
import com.example.eserbisyo.HomeActivity;
import com.example.eserbisyo.ModelActivities.ComplaintViewActivity;
import com.example.eserbisyo.ModelActivities.Profile.AnnouncementActivity;
import com.example.eserbisyo.ModelActivities.Profile.MissingItemActivity;
import com.example.eserbisyo.ModelActivities.Profile.MissingPersonActivity;
import com.example.eserbisyo.ModelActivities.Profile.OrdinanceActivity;
import com.example.eserbisyo.ModelActivities.Profile.ProjectActivity;
import com.example.eserbisyo.Models.Document;
import com.example.eserbisyo.Models.Notification;
import com.example.eserbisyo.OrderActivity.OrderViewActivity;
import com.example.eserbisyo.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsHolder> {
    private final Context context;
    private final ArrayList<Notification> list;
    private final SharedPreferences sharedPreferences;
    private Intent intent;
    public NotificationsAdapter(Context context, ArrayList<Notification> list) {
        this.context = context;
        this.list = list;
        sharedPreferences = context.getApplicationContext().getSharedPreferences(Pref.USER_PREFS,Context.MODE_PRIVATE);
    }


    @NonNull
    @Override
    public NotificationsAdapter.NotificationsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notification, parent, false);
        return new NotificationsAdapter.NotificationsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.NotificationsHolder holder, int position) {
        Notification mNotification = list.get(position);

        holder.txtMessage.setText(mNotification.getMessage());
        holder.txtCreatedAt.setText(mNotification.getCreatedAt());

        if (!mNotification.getSeenStatus().equals("Yes")) {
            holder.layoutNotification.setBackgroundColor(context.getResources().getColor(R.color.babyBlue));
        }

        switch (mNotification.getModelType()) {
            case "App\\Models\\Announcement":
                Picasso.get().load(R.drawable.informative).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\Ordinance":
                Picasso.get().load(R.drawable.rules).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\Project":
                Picasso.get().load(R.drawable.clipboard).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\Android":
                Picasso.get().load(R.drawable.android).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\Inquiry":
                Picasso.get().load(R.drawable.conversation).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\Feedback":
                Picasso.get().load(R.drawable.feedback).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\Report":
                Picasso.get().load(R.drawable.report).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\MissingPerson":
                Picasso.get().load(R.drawable.search).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\MissingItem":
                Picasso.get().load(R.drawable.lost_items).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\Complaint":
                Picasso.get().load(R.drawable.complaint).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\Order":
                Picasso.get().load(R.drawable.order).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\VerificationRequest":
                Picasso.get().load(R.drawable.user).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\BikerRequest":
                Picasso.get().load(R.drawable.biker).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\BikerDelivery":
                Picasso.get().load(R.drawable.bicycle).fit().into(holder.cirIvPicture);
                break;
            case "App\\Models\\UserReport":
            case "App\\Models\\BikerReport":
                /* FOR USER OR BIKER ORDER REPORT*/
                Picasso.get().load(R.drawable.complain).fit().into(holder.cirIvPicture);
                break;
        }


        holder.layoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeNotification(mNotification);
            }
        });
    }

    private void seeNotification(Notification mNotification) {
        Intent intent;
        switch (mNotification.getModelType()) {
            case "App\\Models\\Announcement":
                intent = new Intent(context, AnnouncementActivity.class);
                intent.putExtra(Extra.MODEL_ID, mNotification.getModelId());
                context.startActivity(intent);
                break;
            case "App\\Models\\Ordinance":
                intent = new Intent(context, OrdinanceActivity.class);
                intent.putExtra(Extra.MODEL_ID, mNotification.getModelId());
                context.startActivity(intent);
                break;
            case "App\\Models\\Project":
                intent = new Intent(context, ProjectActivity.class);
                intent.putExtra(Extra.MODEL_ID, mNotification.getModelId());
                context.startActivity(intent);
                break;
            case "App\\Models\\Android":
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(Api.URL + "downloads"));
                context.startActivity(intent);
                break;
            case "App\\Models\\Inquiry":
                intent = new Intent(context, HomeActivity.class);
                intent.putExtra(Extra.MODEL_FRAGMENT, "INQUIRY_FRAGMENT");
                context.startActivity(intent);
                break;
            case "App\\Models\\Feedback":
                intent = new Intent(context, HomeActivity.class);
                intent.putExtra(Extra.MODEL_FRAGMENT, "FEEDBACK_FRAGMENT");
                context.startActivity(intent);
                break;
            case "App\\Models\\Report":
                intent = new Intent(context, HomeActivity.class);
                intent.putExtra(Extra.MODEL_FRAGMENT, "REPORT_FRAGMENT");
                context.startActivity(intent);
                break;
            case "App\\Models\\MissingPerson":
                intent = new Intent(context, MissingPersonActivity.class);
                intent.putExtra(Extra.MODEL_ID, mNotification.getModelId());
                context.startActivity(intent);
                break;
            case "App\\Models\\MissingItem":
                intent = new Intent(context, MissingItemActivity.class);
                intent.putExtra(Extra.MODEL_ID, mNotification.getModelId());
                context.startActivity(intent);
                break;
            case "App\\Models\\Complaint":
                intent = new Intent(context, ComplaintViewActivity.class);
                intent.putExtra(Extra.MODEL_ID, mNotification.getModelId());
                context.startActivity(intent);
                break;
            case "App\\Models\\Order":
            case "App\\Models\\UserReport":
                intent = new Intent(context, OrderViewActivity.class);
                intent.putExtra(Extra.MODEL_ID, mNotification.getModelId());
                context.startActivity(intent);
                break;
            case "App\\Models\\VerificationRequest":
                context.startActivity(new Intent(context, UserVerificationActivity.class));
                break;
            case "App\\Models\\BikerRequest":
                context.startActivity(new Intent(context, BikerViewRegistrationActivity.class));
                break;
            case "App\\Models\\BikerDelivery":
            case "App\\Models\\BikerReport":
                intent = new Intent(context, BikerOrderActivity.class);
                intent.putExtra(Extra.MODEL_ID, mNotification.getModelId());
                context.startActivity(intent);
                break;
            default:
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotificationsHolder extends RecyclerView.ViewHolder {
        private final LinearLayout layoutNotification;
        private final CircleImageView cirIvPicture;
        private final TextView txtMessage, txtCreatedAt;

        public NotificationsHolder(@NonNull View itemView) {
            super(itemView);

            layoutNotification = itemView.findViewById(R.id.layoutNotification);
            cirIvPicture = itemView.findViewById(R.id.cirIvPicture);
            txtMessage = itemView.findViewById(R.id.txtMessage);
            txtCreatedAt = itemView.findViewById(R.id.txtCreatedAt);
        }
    }
}
