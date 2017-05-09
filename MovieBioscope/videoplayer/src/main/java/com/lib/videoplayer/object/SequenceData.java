package com.lib.videoplayer.object;


public class SequenceData {

    private String mSequenceType;
    private String mVideoType;
    private int mSequenceOrder;
    private int mSelection;
    private int mTotalVideoCountForType;

    public int getTotalVideoCountForType() {
        return mTotalVideoCountForType;
    }

    public SequenceData setTotalVideoCountForType(int mTotalVideoCountForType) {
        this.mTotalVideoCountForType = mTotalVideoCountForType;
        return this;
    }

    public int getCurrentVideoCountForType() {
        return mCurrentVideoCountForType;
    }

    public SequenceData setCurrentVideoCountForType(int mCurrentVideoCountForType) {
        this.mCurrentVideoCountForType = mCurrentVideoCountForType;
        return this;
    }

    private int mCurrentVideoCountForType;

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

    public int getSequenceOrder() {
        return mSequenceOrder;
    }

    public SequenceData setSequenceOrder(int mSequenceOrder) {
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
