package com.app.navajhalaka.model;


import java.util.List;

public class AnalyticResponse {
    private String code;
    private List<AnalyticData> data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<AnalyticData> getData() {
        return data;
    }

    public void setData(List<AnalyticData> data) {
        this.data = data;
    }
}
