package com.lib.location.model;

/**
 * Created by aarokiax on 2/15/2017.
 */

public class DistanceData {

    String[] origin_addresses;
    String[] destination_addresses;
    Rows[] rows;
    String status;

    public String[] getOrigin_addresses() {
        return origin_addresses;
    }

    public void setOrigin_addresses(String[] origin_addresses) {
        this.origin_addresses = origin_addresses;
    }

    public String[] getDestination_addresses() {
        return destination_addresses;
    }

    public void setDestination_addresses(String[] destination_addresses) {
        this.destination_addresses = destination_addresses;
    }

    public Rows[] getRows() {
        return rows;
    }

    public void setRows(Rows[] rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
