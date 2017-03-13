package com.app.navajhalaka.model;


public class AnalyticData {
    private String analyticsID;
    private String assetID;
    private String fleetID;
    private String routeID;
    private String companyID;

    public String getAnalyticsID() {
        return analyticsID;
    }

    public void setAnalyticsID(String analyticsID) {
        this.analyticsID = analyticsID;
    }

    public String getAssetID() {
        return assetID;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getFleetID() {
        return fleetID;
    }

    public void setFleetID(String fleetID) {
        this.fleetID = fleetID;
    }

    public String getCompanyID() {
        return companyID;
    }

    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }

    public String getPlayedTime() {
        return playedTime;
    }

    public void setPlayedTime(String playedTime) {
        this.playedTime = playedTime;
    }

    private String playedTime;

}
