package com.kickstartlab.android.klikSpace.events;


import com.kickstartlab.android.klikSpace.rest.models.City;

/**
 * Created by awidarto on 12/3/14.
 */
public class CityEvent {

    private String action = "refresh";
    private City city;
    private String cityId = "";

    public CityEvent(String action){
        this.action = action;
    }

    public CityEvent(String action, City city){
        this.action = action;
        this.city = city;
    }

    public CityEvent(String action, String cityId) {
        this.action = action;
        this.cityId = cityId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
