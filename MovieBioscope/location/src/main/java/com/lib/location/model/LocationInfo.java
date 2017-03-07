package com.lib.location.model;

/**
 * Created by Aron on 15-02-2017.
 */

public class LocationInfo {

    private String source;
    private String destination;
    private String currentLocation;


    private String city;
    private String totalDistance;
    private String totalJourneyTime;
    private String distanceToSource;
    private String distanceToDestination;
    private String timeToDestination;
    private String lastSyncTime;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalJourneyTime() {
        return totalJourneyTime;
    }

    public void setTotalJourneyTime(String totalJourneyTime) {
        this.totalJourneyTime = totalJourneyTime;
    }

    public String getDistanceToSource() {
        return distanceToSource;
    }

    public void setDistanceToSource(String distanceToSource) {
        this.distanceToSource = distanceToSource;
    }

    public String getDistanceToDestination() {
        return distanceToDestination;
    }

    public void setDistanceToDestination(String distanceToDestination) {
        this.distanceToDestination = distanceToDestination;
    }

    public String getTimeToDestination() {
        return timeToDestination;
    }

    public void setTimeToDestination(String timeToDestination) {
        this.timeToDestination = timeToDestination;
    }

    public String getLastSyncTime() {
        return lastSyncTime;
    }

    public void setLastSyncTime(String lastSyncTime) {
        this.lastSyncTime = lastSyncTime;
    }
}
