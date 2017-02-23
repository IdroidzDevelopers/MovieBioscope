package com.lib.firebase;

import android.content.Context;

/**
 * Created by aarokiax on 2/24/2017.
 */

public class FireBaseApplication {

    private static Context firebaseContext;

    public static Context getFirebaseContext() {
        return firebaseContext;
    }

    public static void setFirebaseContext(Context firebaseContext) {
        FireBaseApplication.firebaseContext = firebaseContext;
    }
}
