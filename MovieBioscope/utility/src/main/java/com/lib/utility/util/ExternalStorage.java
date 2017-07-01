package com.lib.utility.util;


import android.content.Context;
import android.os.Environment;

import java.io.File;

public class ExternalStorage {
    private static final int SD_CARD_STORAGE_INDEX = 1;

    public static File getPath(Context context) {
        if (null != context) {
            File[] extMounts = context.getExternalFilesDirs(null);
            if (null != extMounts && extMounts.length > 1) {
                return extMounts[SD_CARD_STORAGE_INDEX];
            } else {
                Environment.getExternalStorageDirectory();
            }
        }
        return Environment.getExternalStorageDirectory();
    }
}
