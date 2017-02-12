package com.hyperbound.moviebioscope.objects;


public class Route {
    private int mId;

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
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
