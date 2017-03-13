package com.app.navajhalaka.model;

/**
 * Created by aarokiax on 2/16/2017.
 */
public class DestinationDetails {

    private String formatted_address;
    private String latitude;
    private String longitude;

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
