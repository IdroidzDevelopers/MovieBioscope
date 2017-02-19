package com.hyperbound.moviebioscope.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hyperbound.moviebioscope.R;
import com.hyperbound.moviebioscope.util.BusUtil;

import java.util.List;

/**
 * Created by aarokiax on 2/17/2017.
 */

public class FireBaseManager {
    private static final String TAG = "FireBaseManager";

    public static String getFireBaseToken() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Firebase token :: " + token);
        return token;
    }

    public static void subscribeFirebaseTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("TN11K5868");
        List<String> topics = BusUtil.getAllFireBaseTopics();
        if (null != topics && topics.size() > 0) {
            for (String topic : topics) {
                Log.d(TAG, "Subscribe to Firebaase :: " + topic);
                FirebaseMessaging.getInstance().subscribeToTopic(topic);
            }
        }
    }
}
