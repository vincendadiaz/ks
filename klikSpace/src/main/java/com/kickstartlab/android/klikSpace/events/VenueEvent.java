package com.kickstartlab.android.klikSpace.events;


import com.kickstartlab.android.klikSpace.rest.models.Asset;

/**
 * Created by awidarto on 12/3/14.
 */
public class VenueEvent {

    private String action = "refresh";
    private String rackId = "";
    private Asset asset;

    public VenueEvent(String action){
        this.action = action;
    }

    public VenueEvent(String action, Asset asset){
        this.action = action;
        this.asset = asset;
    }

    public VenueEvent(String action, String rackId){
        this.action = action;
        this.rackId = rackId;
    }

    public VenueEvent(String action, Asset asset, String rackId){
        this.action = action;
        this.asset = asset;
        this.rackId = rackId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }
}
