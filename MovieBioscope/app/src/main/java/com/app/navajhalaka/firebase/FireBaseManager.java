package com.app.navajhalaka.firebase;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.app.navajhalaka.R;
import com.app.navajhalaka.app.BioscopeApp;
import com.app.navajhalaka.util.BusUtil;
import com.lib.firebase.util.FirebaseUtil;

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
        List<String> topics = FirebaseUtil.getAllFireBaseTopics(BioscopeApp.getContext());
        if (null != topics && topics.size() > 0) {
            for (String topic : topics) {
                Log.d(TAG, "Subscribe to Firebaase :: " + topic);
                FirebaseMessaging.getInstance().subscribeToTopic(topic);
            }
        }
    }
}
