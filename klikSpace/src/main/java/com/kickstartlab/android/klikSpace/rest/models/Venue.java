package com.kickstartlab.android.klikSpace.rest.models;

import com.orm.SugarRecord;

import java.util.ArrayList;

/**
 * Created by awidarto on 10/14/14.
 */
public class Venue extends SugarRecord<Venue> {

    private String mongoID;
    private String name;
    private String locationID;
    private String locationName;
    private String venueType;
    private String inDoor;
    private String outDoor;
    private int capacity;
    private int squareMeter;
    private int rentalRate;
    private String rentalCurrency;
    private String rentalUnit;
    private ArrayList<String> accessibility;
    private String PIC;
    private String picPhone;
    private String picEmail;
    private String opHoursFrom;
    private String opHoursTo;
    private ArrayList<String> auxiliaryFeature;
    private String extId;

    public Venue(){

    }

    public String getMongoID() {
        return mongoID;
    }

    public void setMongoID(String mongoID) {
        this.mongoID = mongoID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocationID() {
        return locationID;
    }

    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public String getInDoor() {
        return inDoor;
    }

    public void setInDoor(String inDoor) {
        this.inDoor = inDoor;
    }

    public String getOutDoor() {
        return outDoor;
    }

    public void setOutDoor(String outDoor) {
        this.outDoor = outDoor;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getSquareMeter() {
        return squareMeter;
    }

    public void setSquareMeter(int squareMeter) {
        this.squareMeter = squareMeter;
    }

    public int getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(int rentalRate) {
        this.rentalRate = rentalRate;
    }

    public String getRentalCurrency() {
        return rentalCurrency;
    }

    public void setRentalCurrency(String rentalCurrency) {
        this.rentalCurrency = rentalCurrency;
    }

    public String getRentalUnit() {
        return rentalUnit;
    }

    public void setRentalUnit(String rentalUnit) {
        this.rentalUnit = rentalUnit;
    }

    public ArrayList<String> getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(ArrayList<String> accessibility) {
        this.accessibility = accessibility;
    }

    public String getPIC() {
        return PIC;
    }

    public void setPIC(String PIC) {
        this.PIC = PIC;
    }

    public String getPicPhone() {
        return picPhone;
    }

    public void setPicPhone(String picPhone) {
        this.picPhone = picPhone;
    }

    public String getPicEmail() {
        return picEmail;
    }

    public void setPicEmail(String picEmail) {
        this.picEmail = picEmail;
    }

    public String getOpHoursFrom() {
        return opHoursFrom;
    }

    public void setOpHoursFrom(String opHoursFrom) {
        this.opHoursFrom = opHoursFrom;
    }

    public String getOpHoursTo() {
        return opHoursTo;
    }

    public void setOpHoursTo(String opHoursTo) {
        this.opHoursTo = opHoursTo;
    }

    public ArrayList<String> getAuxiliaryFeature() {
        return auxiliaryFeature;
    }

    public void setAuxiliaryFeature(ArrayList<String> auxiliaryFeature) {
        this.auxiliaryFeature = auxiliaryFeature;
    }

    public String getExtId() {
        return extId;
    }

    public void setExtId(String extId) {
        this.extId = extId;
    }
}
