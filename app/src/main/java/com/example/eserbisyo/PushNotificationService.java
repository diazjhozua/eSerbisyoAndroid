package com.example.eserbisyo;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

public class PushNotificationService extends FirebaseMessagingService {

    private static final String CHANNEL_ID ="1002";

    @Override
    public void onNewToken(@NonNull String mToken) {
        super.onNewToken(mToken);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("Title: ", Objects.requireNonNull(remoteMessage.getNotification()).getTitle());
        Log.d("Body: ", remoteMessage.getNotification().getBody());

        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }


    private void showNotification(String title,String message){
        //**add this line**
        int requestID = (int) System.currentTimeMillis();

        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntent = PendingIntent.getActivity(this,
                    requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        }else {
            pendingIntent = PendingIntent.getActivity(this,
                    requestID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        }

//        PendingIntent.FLAG_IMMUTABLE;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.cupang)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification th
        //
        // at you must define
        notificationManager.notify(1, builder.build());
    }


}
