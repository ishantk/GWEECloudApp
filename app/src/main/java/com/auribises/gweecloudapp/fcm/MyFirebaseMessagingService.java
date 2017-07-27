package com.auribises.gweecloudapp.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;


import com.auribises.gweecloudapp.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

/**
 * Created by Eshaan on 05-Dec-16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    Intent intent;

    public void onMessageReceived(RemoteMessage remoteMessage) {
    Toast.makeText(MyFirebaseMessagingService.this, "Message Received", Toast.LENGTH_SHORT).show();

        if (remoteMessage == null){

            return;
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            handleNotification(remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) {
            Log.i( "Data Payload: " ,remoteMessage.getData().toString());
            Log.i("size", String.valueOf(remoteMessage.getData().size()));

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                Log.i("TRY","inTry");
                handleDataMessage(json);
            } catch (Exception e) {
                Log.i( "Exception: " , e.getMessage());
            }
        }
    }

    private void handleDataMessage(JSONObject json) {

    }

    private void handleNotification(String message) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Title")
                    .setContentText(message);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(101, notificationBuilder.build());

    }
}
