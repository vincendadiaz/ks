package com.kickstartlab.android.klikSpace.events;


import com.kickstartlab.android.klikSpace.rest.models.Location;

/**
 * Created by awidarto on 12/3/14.
 */
public class LocationEvent {

    private String action = "refresh";
    private Location location;
    private String locationId = "";

    public LocationEvent(String action){
        this.action = action;
    }

    public LocationEvent(String action, Location location){
        this.action = action;
        this.location = location;
    }

    public LocationEvent(String action, String locationId){
        this.action = action;
        this.locationId = locationId;

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
