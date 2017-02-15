package com.lib.location.model;

/**
 * Created by aarokiax on 2/15/2017.
 */

public class Elements {

    Distance distance;
    Duration duration;
    DurationTraffic duration_in_traffic;
    String status;

    public Distance getDistance() {
        return distance;
    }

    public void setDistance(Distance distance) {
        this.distance = distance;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public DurationTraffic getDuration_in_traffic() {
        return duration_in_traffic;
    }

    public void setDuration_in_traffic(DurationTraffic duration_in_traffic) {
        this.duration_in_traffic = duration_in_traffic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
