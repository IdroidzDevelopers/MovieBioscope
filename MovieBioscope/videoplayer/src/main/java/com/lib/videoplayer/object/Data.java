package com.lib.videoplayer.object;


public class Data {
    private String assetID;
    private String name;
    private String url;
    private String type;
    private String language;
    private String mMessage;
    private String mDownloadingId;
    private String mDownloadStatus;
    private String mPath;
    private String mLastPlayedTime;
    private int mCount;
    private String mTransactionId;
    private String mPriority;

    public String getPriority() {
        return mPriority;
    }

    public Data setPriority(String priority) {
        this.mPriority = priority;
        return this;
    }

    public String getCloudTime() {
        return mCloudTime;
    }

    public void setCloudTime(String mCloudTime) {
        this.mCloudTime = mCloudTime;
    }

    public String getReceivedTime() {
        return mReceivedTime;
    }

    public void setReceivedTime(String mReceivedTime) {
        this.mReceivedTime = mReceivedTime;
    }

    private String mCloudTime;
    private String mReceivedTime;

    public String getAssetID() {
        return assetID;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String mLanguage) {
        this.language = mLanguage;
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


    public String getTransactionId() {
        return mTransactionId;
    }

    public void setTransactionId(String mTransactionId) {
        this.mTransactionId = mTransactionId;
    }


    public String getType() {
        return type;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public void setType(String mType) {
        this.type = mType;
    }

    public String getName() {
        return name;
    }

    public void setName(String mName) {
        this.name = mName;
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

    @Override
    public String toString() {
        return "Data{" +
                "assetID='" + assetID + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", language='" + language + '\'' +
                ", mMessage='" + mMessage + '\'' +
                ", mDownloadingId='" + mDownloadingId + '\'' +
                ", mDownloadStatus='" + mDownloadStatus + '\'' +
                ", mPath='" + mPath + '\'' +
                ", mLastPlayedTime='" + mLastPlayedTime + '\'' +
                ", mCount=" + mCount +
                ", mTransactionId='" + mTransactionId + '\'' +
                '}';
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
