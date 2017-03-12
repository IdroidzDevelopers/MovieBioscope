

package com.hyperbound.moviebioscope.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hyperbound.moviebioscope.R;
import com.hyperbound.moviebioscope.app.BioscopeApp;
import com.hyperbound.moviebioscope.ui.MainActivity;
import com.hyperbound.moviebioscope.util.AppInterface;
import com.hyperbound.moviebioscope.util.AppTaskHandler;
import com.lib.firebase.util.FirebaseUtil;
import com.lib.utility.util.CustomIntent;

public class BusFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]


        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (remoteMessage.getData().containsKey(AppInterface.APP_KEY)
                    && remoteMessage.getData().containsKey(AppInterface.DATA_KEY)
                    && remoteMessage.getData().containsKey(AppInterface.ACTION_KEY)) {
                String app = remoteMessage.getData().get(AppInterface.APP_KEY);
                String data = remoteMessage.getData().get(AppInterface.DATA_KEY);
                String action = remoteMessage.getData().get(AppInterface.ACTION_KEY);
                long sentTime = remoteMessage.getSentTime();
                Uri uri = FirebaseUtil.insertFirebaseData(BioscopeApp.getContext(), app, data, sentTime);
                switch(action) {
                    case AppInterface.COMMAND_DOWNLOAD:{
                        switch (app) {
                            case AppInterface.TYPE_VIDEO: {
                                LocalBroadcastManager.getInstance(BioscopeApp.getContext()).sendBroadcast(new Intent(CustomIntent.ACTION_VIDEO_DATA_RECEIVED).putExtra(CustomIntent.EXTRAS.URI_KEY, uri.toString()));
                                break;
                            }
                        }
                        break;
                    }
                    case AppInterface.COMMAND_REFRESH:{
                        Message msg = Message.obtain();
                        msg.what = AppInterface.HANDLE_REFRESH;
                        Bundle lBundle=new Bundle();
                        lBundle.putString("data",data);
                        msg.setData(lBundle);
                        AppTaskHandler.getInstance().sendMessage(msg);
                        break;
                    }
                    case AppInterface.COMMAND_UPDATE:{
                        LocalBroadcastManager.getInstance(BioscopeApp.getContext()).sendBroadcast(new Intent(CustomIntent.ACTION_VIDEO_DATA_RECEIVED).putExtra(CustomIntent.EXTRAS.URI_KEY, uri.toString()));
                        break;
                    }
                    case AppInterface.COMMAND_DELETE:{
                        LocalBroadcastManager.getInstance(BioscopeApp.getContext()).sendBroadcast(new Intent(CustomIntent.ACTION_VIDEO_DATA_RECEIVED).putExtra(CustomIntent.EXTRAS.URI_KEY, uri.toString()));
                        break;
                    }
                }
            }
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());

        }
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("FCM Message")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
