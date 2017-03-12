package com.lib.videoplayer.object;

import java.util.Arrays;
import java.util.List;


public class PushData {
    private List<Asset> assets;
    private String action;
    private String content;
    private String transactionID;
    private String mCloudTime;
    private String mReceivedTime;

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

    @Override
    public String toString() {
        return "PushData{" +
                "assets=" + Arrays.asList(assets) +
                ", action='" + action + '\'' +
                ", content='" + content + '\'' +
                ", transactionID='" + transactionID + '\'' +
                '}';
    }

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }
}
