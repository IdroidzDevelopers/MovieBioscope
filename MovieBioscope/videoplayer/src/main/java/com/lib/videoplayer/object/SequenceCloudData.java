package com.lib.videoplayer.object;


public class SequenceCloudData {

    private String name;
    private String type;
    private String value;
    private String order;

    public String getName() {
        return name;
    }

    public SequenceCloudData setName(String name) {
        this.name = name;
        return this;
    }

    public String getType() {
        return type;
    }

    public SequenceCloudData setType(String type) {
        this.type = type;
        return this;
    }

    public String getValue() {
        return value;
    }

    public SequenceCloudData setValue(String value) {
        this.value = value;
        return this;
    }

    public String getOrder() {
        return order;
    }

    public SequenceCloudData setOrder(String order) {
        this.order = order;
        return this;
    }
}
