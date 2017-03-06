package com.hyperbound.moviebioscope.model;

/**
 * Created by Aron on 06-03-2017.
 */

public class AnalyticData {
    private String analyticId;
    private String assetId;
    private String routeId;
    private String fleetId;
    private String companyId;

    public String getAnalyticId() {
        return analyticId;
    }

    public void setAnalyticId(String analyticId) {
        this.analyticId = analyticId;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getFleetId() {
        return fleetId;
    }

    public void setFleetId(String fleetId) {
        this.fleetId = fleetId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getPlayedTime() {
        return playedTime;
    }

    public void setPlayedTime(String playedTime) {
        this.playedTime = playedTime;
    }

    private String playedTime;

}
