package com.lib.route.objects;


public class Route {
    private String mRouteId;

    public String getRouteId() {
        return mRouteId;
    }

    public void setRouteId(String mId) {
        this.mRouteId = mId;
    }

    private String mRouteName;
    private int mDefault;

    public String getRouteName() {
        return mRouteName;
    }

    public void setRouteName(String mRouteName) {
        this.mRouteName = mRouteName;
    }

    public int getDefault() {
        return mDefault;
    }

    public void setDefault(int mDefault) {
        this.mDefault = mDefault;
    }

    @Override
    public String toString() {
        return "Route{" +
                "mRouteName='" + mRouteName + '\'' +
                ", mDefault=" + mDefault +
                '}';
    }
}
