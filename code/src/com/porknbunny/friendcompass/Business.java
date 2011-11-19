package com.porknbunny.friendcompass;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: pigsnowball
 * Date: 19/11/2011
 * Time: 20:37
 * To change this template use File | Settings | File Templates.
 */
public class Business implements Serializable {
    public String name;
    //public Location location;
    public String addressLine;
    public String suburb;
    public String phoneNumber;
    public String id;
    public String category;
    public double latitude;
    public double longitude;
    
    
    public Business(String name, double latitude, double longitude, String addressLine, String suburb, String id, String category, String phoneNumber) {
        this.name = name;
        this.addressLine = addressLine;
        this.suburb = suburb;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.category = category;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    public String getCategory() {
        return category;
    }

    public Location getLocation(){
        Location location = new Location("SAPI");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getSuburb() {
        return suburb;
    }

    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
