package com.lib.route.objects;


public class DownloadData {
    private String mPath;
    private int mDownloadStatus;

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public int getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setDownloadStatus(int mDownloadStatus) {
        this.mDownloadStatus = mDownloadStatus;
    }

    public DownloadData(String mPath, int mDownloadStatus) {
        this.mPath = mPath;
        this.mDownloadStatus = mDownloadStatus;
    }

    @Override
    public String toString() {
        return "DownloadData{" +
                "mPath='" + mPath + '\'' +
                ", mDownloadStatus=" + mDownloadStatus +
                '}';
    }
}
