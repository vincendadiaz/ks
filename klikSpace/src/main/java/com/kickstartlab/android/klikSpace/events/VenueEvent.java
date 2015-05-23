package com.kickstartlab.android.klikSpace.events;


import com.kickstartlab.android.klikSpace.rest.models.Asset;
import com.kickstartlab.android.klikSpace.rest.models.Venue;

/**
 * Created by awidarto on 12/3/14.
 */
public class VenueEvent {

    private String action = "refresh";
    private String locationId = "";
    private Venue venue;
//    private Asset asset;

    public VenueEvent(String action){
        this.action = action;
    }

    public VenueEvent(String action, Venue venue){
        this.action = action;
        this.venue = venue;
    }

    public VenueEvent(String action, String locationId){
        this.action = action;
        this.locationId = locationId;
    }

    public VenueEvent(String action, Venue venue, String locationId){
        this.action = action;
        this.venue = venue;
        this.locationId = locationId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
