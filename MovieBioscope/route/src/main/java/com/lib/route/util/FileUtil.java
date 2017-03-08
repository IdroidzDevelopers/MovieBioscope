package com.lib.route.util;


import android.media.MediaScannerConnection;
import android.util.Log;

import com.lib.route.RouteApplication;
import com.lib.utility.util.Logger;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();
    private static final String FILE_NAME = "read_me.txt";

    /**
     * Method to check file exist of the given path
     *
     * @param lPath
     * @return
     */
    public static boolean isFileExist(String lPath) {
        File lFile = new File(lPath);
        Logger.debug(TAG, "isFileExist " + lPath + " lFile.exists() " + lFile.exists());
        return lFile.exists();
    }

    public static void createFolderIfRequired(String dir) {
        File folder = new File(dir);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File file = new File(dir + "/" + FILE_NAME);
        if (!file.exists()) {
            try {
                boolean status = file.createNewFile();
                Log.d(TAG, "createFolderIfRequired() :: status" + status);
                MediaScannerConnection.scanFile(RouteApplication.getRouteContext(), new String[]{file.getAbsolutePath()}, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
