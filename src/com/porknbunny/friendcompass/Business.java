package com.porknbunny.friendcompass;

import android.location.Location;

/**
 * Created by IntelliJ IDEA.
 * User: pigsnowball
 * Date: 19/11/2011
 * Time: 20:37
 * To change this template use File | Settings | File Templates.
 */
public class Business {
    public String name;
    public Location location;
    public String addressLine;
    public String suburb;
    public String phoneNumber;
    public String id;
    public String category;

    public Business(String name, Location location, String addressLine, String suburb, String id, String category) {
        this.name = name;
        this.location = location;
        this.addressLine = addressLine;
        this.suburb = suburb;
        this.id = id;
        this.phoneNumber = "";
        this.category = category;
    }

    public String getCategory() {
        return category;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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
