package com.lib.videoplayer.util;


import java.io.File;

public class FileUtil {
    /**
     * Method to check file exist of the given path
     *
     * @param lPath
     * @return
     */
    public static boolean isFileExist(String lPath) {
        File lFile = new File(lPath);
        return lFile.exists();
    }
}
