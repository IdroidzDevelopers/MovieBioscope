package com.lib.videoplayer.util;


import com.lib.utility.util.Logger;

import java.io.File;

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

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
}
