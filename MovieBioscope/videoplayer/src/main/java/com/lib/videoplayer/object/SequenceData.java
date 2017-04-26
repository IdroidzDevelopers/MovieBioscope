package com.lib.videoplayer.object;


public class SequenceData {

    private String mSequenceType;
    private String mVideoType;
    private String mSequenceOrder;
    private int mSelection;

    public String getSequenceType() {
        return mSequenceType;
    }

    public SequenceData setSequenceType(String mSequenceType) {
        this.mSequenceType = mSequenceType;
        return this;
    }

    public String getVideoType() {
        return mVideoType;
    }

    public SequenceData setVideoType(String mVideoType) {
        this.mVideoType = mVideoType;
        return this;
    }

    public String getSequenceOrder() {
        return mSequenceOrder;
    }

    public SequenceData setSequenceOrder(String mSequenceOrder) {
        this.mSequenceOrder = mSequenceOrder;
        return this;
    }

    public int getSelection() {
        return mSelection;
    }

    public SequenceData setSelection(int mPlayingState) {
        this.mSelection = mPlayingState;
        return this;
    }
}
