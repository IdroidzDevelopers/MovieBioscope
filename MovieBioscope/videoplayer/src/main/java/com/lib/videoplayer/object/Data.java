package com.lib.videoplayer.object;

/**
 * Created by Aron on 04-02-2017.
 */

public class Data {

    private int mId;
    private String mName;
    private String mPath;
    private String mType;

    public String getType() {
        return mType;
    }

    public void setType(String mType) {
        this.mType = mType;
    }

    private String mLastPlayedTime;
    private int mCount;

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public String getLastPlayedTime() {
        return mLastPlayedTime;
    }

    public void setLastPlayedTime(String mLastPlayedTime) {
        this.mLastPlayedTime = mLastPlayedTime;
    }

    public int getCount() {
        return mCount;
    }

    public void setCount(int mCount) {
        this.mCount = mCount;
    }
}
