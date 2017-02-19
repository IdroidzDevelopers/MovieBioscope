package com.lib.videoplayer.object;


public class Data {

    private int mVideoId;
    private String mName;
    private String mDownloadUrl;
    private String mType;
    private String mLanguage;
    private String mMessage;
    private String mDownloadingId;
    private String mDownloadStatus;
    private String mPath;
    private String mLastPlayedTime;
    private int mCount;
    private String mCloudId;

    public String getDownloadUrl() {
        return mDownloadUrl;
    }

    public void setDownloadUrl(String mDownloadUrl) {
        this.mDownloadUrl = mDownloadUrl;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String mLanguage) {
        this.mLanguage = mLanguage;
    }


    public String getDownloadingId() {
        return mDownloadingId;
    }

    public void setDownloadingId(String mDownloadingId) {
        this.mDownloadingId = mDownloadingId;
    }

    public String getDownloadStatus() {
        return mDownloadStatus;
    }

    public void setDownloadStatus(String mDownloadStatus) {
        this.mDownloadStatus = mDownloadStatus;
    }


    public String getCloudId() {
        return mCloudId;
    }

    public void setCloudId(String mCloudId) {
        this.mCloudId = mCloudId;
    }


    public String getType() {
        return mType;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public void setType(String mType) {
        this.mType = mType;
    }


    public int getVideoId() {
        return mVideoId;
    }

    public void setVideoId(int mId) {
        this.mVideoId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getLastPlayedTime() {
        return mLastPlayedTime;
    }

    public void setLastPlayedTime(String mLastPlayedTime) {
        this.mLastPlayedTime = mLastPlayedTime;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
    }
}
