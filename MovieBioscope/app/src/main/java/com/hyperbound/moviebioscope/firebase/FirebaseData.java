package com.hyperbound.moviebioscope.firebase;

import com.hyperbound.moviebioscope.model.Url;

import java.util.List;

/**
 * Created by aarokiax on 2/17/2017.
 */

public class FirebaseData {
    private String transactionId;
    private String data;
    private String sentTime;
    private String receivedTime;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSentTime() {
        return sentTime;
    }

    public void setSentTime(String sentTime) {
        this.sentTime = sentTime;
    }

    public String getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }
}
