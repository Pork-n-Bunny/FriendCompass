package com.porknbunny.friendcompass;

import android.location.Location;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: pigsnowball
 * Date: 20/11/2011
 * Time: 04:06
 * To change this template use File | Settings | File Templates.
 */
public class Friend implements Serializable {
    private String userid,friend,bizID;
    private double lat,longi;
    private int time;

    public Friend(String userid, String friend, String bizID, double lat, double longi, int time) {
        this.userid = userid;
        this.friend = friend;
        this.bizID = bizID;
        this.lat = lat;
        this.longi = longi;
        this.time = time;
    }

    public Location getLocation(){
        Location location = new Location("SAPI");
        location.setLatitude(lat);
        location.setLongitude(longi);
        return location;
        
    }
    
    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getBizID() {
        return bizID;
    }

    public void setBizID(String bizID) {
        this.bizID = bizID;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
