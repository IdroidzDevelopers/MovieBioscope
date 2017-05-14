package com.lib.videoplayer.object;

/**
 * Created by intel on 5/13/17.
 */

public class SlotData {

    private String slotType;
    private String slotsPerHour;
    private String adsPerSlot;

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getSlotsPerHour() {
        return slotsPerHour;
    }

    public void setSlotsPerHour(String slotsPerHour) {
        this.slotsPerHour = slotsPerHour;
    }

    public String getAdsPerSlot() {
        return adsPerSlot;
    }

    public void setAdsPerSlot(String adsPerSlot) {
        this.adsPerSlot = adsPerSlot;
    }
}
