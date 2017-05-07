package com.lib.videoplayer.object;


public class Asset {
    private String assetID;
    private String name;
    private String url;
    private String type;
    private String language;
    private String priority;

    public String getPriority() {
        return priority;
    }

    public Asset setPriority(String priority) {
        this.priority = priority;
        return this;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "assetID='" + assetID + '\'' +
                ", name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", language='" + language + '\'' +
                '}';
    }

    public String getAssetID() {
        return assetID;
    }

    public void setAssetID(String assetID) {
        this.assetID = assetID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
