package com.parse.starter;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by chris on 6/21/16.
 */
public class BarberInfo {

    String barberObjectId;
    String barberName;
    String barberUsername;
    LatLng barberLocation;
    String barberPlaceName;
    double distanceFromBarber;

    public double getDistanceFromBarber() {
        return distanceFromBarber;
    }

    public void setDistanceFromBarber(double distanceFromBarber) {
        this.distanceFromBarber = distanceFromBarber;
    }

    public BarberInfo() {

    }

    String barberAboutMessage;


    public String getBarberObjectId() {
        return barberObjectId;
    }

    public void setBarberObjectId(String barberObjectId) {
        this.barberObjectId = barberObjectId;
    }

    public BarberInfo(String barberName, String barberUsername, LatLng barberLocation, String barberShopName, String barberAboutMessage, String id, float distance) {
        this.barberName = barberName;
        this.barberUsername = barberUsername;
        this.barberLocation = barberLocation;
        this.barberPlaceName = barberShopName;
        this.barberAboutMessage = barberAboutMessage;
        this.barberObjectId = id;
        this.distanceFromBarber = distance;

    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getBarberUsername() {
        return barberUsername;
    }

    public void setBarberUsername(String barberUsername) {
        this.barberUsername = barberUsername;
    }

    public LatLng getBarberLocation() {
        return barberLocation;
    }

    public void setBarberLocation(LatLng barberLocation) {
        this.barberLocation = barberLocation;
    }

    public String getBarberPlaceName() {
        return barberPlaceName;
    }

    public void setBarberPlaceName(String barberShopName) {
        this.barberPlaceName = barberShopName;
    }

    public String getBarberAboutMessage() {
        return barberAboutMessage;
    }

    public void setBarberAboutMessage(String barberAboutMessage) {
        this.barberAboutMessage = barberAboutMessage;
    }
}
