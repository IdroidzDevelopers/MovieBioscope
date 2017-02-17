package com.hyperbound.moviebioscope.model;

import java.util.List;

/**
 * Created by aarokiax on 2/16/2017.
 */

public class BusDetails {

    private String regNo;
    private String fleetID;
    private String company;
    private String companyName;
    private List<String> topics;
    private Routes routes[];

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public String getFleetID() {
        return fleetID;
    }

    public void setFleetID(String fleetID) {
        this.fleetID = fleetID;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }

    public Routes[] getRoutes() {
        return routes;
    }

    public void setRoutes(Routes[] routes) {
        this.routes = routes;
    }
}
