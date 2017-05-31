package com.lib.videoplayer.util;


import android.media.MediaScannerConnection;
import android.util.Log;

import com.lib.utility.util.Logger;
import com.lib.videoplayer.VideoApplication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
                MediaScannerConnection.scanFile(VideoApplication.getVideoContext(), new String[]{file.getAbsolutePath()}, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }

}
