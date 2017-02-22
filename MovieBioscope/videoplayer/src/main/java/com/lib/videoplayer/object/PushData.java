package com.lib.videoplayer.object;

import java.util.Arrays;


public class PushData {
    private Asset[] assets;
    private String action;
    private String content;
    private String transactionID;

    @Override
    public String toString() {
        return "PushData{" +
                "assets=" + Arrays.toString(assets) +
                ", action='" + action + '\'' +
                ", content='" + content + '\'' +
                ", transactionID='" + transactionID + '\'' +
                '}';
    }

    public Asset[] getAssets() {
        return assets;
    }

    public void setAssets(Asset[] assets) {
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
