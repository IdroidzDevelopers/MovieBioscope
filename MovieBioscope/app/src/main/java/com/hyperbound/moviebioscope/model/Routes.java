package com.hyperbound.moviebioscope.model;

import java.util.List;

/**
 * Created by aarokiax on 2/16/2017.
 */

public class Routes {

    private String routeID;
    private String source;
    private SourceDetails sourceDetails;
    private String destination;
    private DestinationDetails destinationDetails;
    private List<Images> images;

    public String getRouteID() {
        return routeID;
    }

    public void setRouteID(String routeID) {
        this.routeID = routeID;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public SourceDetails getSourceDetails() {
        return sourceDetails;
    }

    public void setSourceDetails(SourceDetails sourceDetails) {
        this.sourceDetails = sourceDetails;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public DestinationDetails getDestinationDetails() {
        return destinationDetails;
    }

    public void setDestinationDetails(DestinationDetails destinationDetails) {
        this.destinationDetails = destinationDetails;
    }

    public List<Images> getImages() {
        return images;
    }

    public void setImages(List<Images> images) {
        this.images = images;
    }
}
