package com.app.navajhalaka.model;

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
    private List<Routes> routes;
    private CompanyDetails companyDetails;

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

    public List<Routes> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Routes> routes) {
        this.routes = routes;
    }

    public CompanyDetails getCompanyDetails() {
        return companyDetails;
    }

    public BusDetails setCompanyDetails(CompanyDetails companyDetails) {
        this.companyDetails = companyDetails;
        return this;
    }
}
