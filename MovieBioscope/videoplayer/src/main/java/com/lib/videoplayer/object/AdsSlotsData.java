package com.lib.videoplayer.object;

import java.util.List;

/**
 * Created by intel on 5/13/17.
 */

public class AdsSlotsData {
    private List<Asset> assets;
    private String action;
    private String transactionID;
    private SlotData slot;

    public List<Asset> getAssets() {
        return assets;
    }

    public void setAssets(List<Asset> assets) {
        this.assets = assets;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    public SlotData getSlot() {
        return slot;
    }

    public void setSlot(SlotData slot) {
        this.slot = slot;
    }
}
