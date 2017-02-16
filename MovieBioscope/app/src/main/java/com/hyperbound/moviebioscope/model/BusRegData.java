package com.hyperbound.moviebioscope.model;

/**
 * Created by aarokiax on 2/16/2017.
 */

public class BusRegData {

    private String code;
    private String message;
    private BusDetails data[];

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BusDetails[] getData() {
        return data;
    }

    public void setData(BusDetails[] data) {
        this.data = data;
    }
}
