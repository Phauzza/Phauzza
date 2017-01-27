package com.pahuza.pahuza.models;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by baryariv on 26/12/2016.
 */

public class CustomPlace {

    private String address;
    private LatLng latlng;


    public CustomPlace(){

    }

    public CustomPlace(String address, LatLng latlng){
        this.address = address;
        this.latlng = latlng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}
