package com.pahuza.pahuza.models;

import android.location.Location;


/**
 * Created by baryariv on 27/12/2016.
 */

public class MyLocation {

    private Location location;
    private String jobId;
    private String description;


    public MyLocation() {
    }

    public MyLocation(Location location, String jobId, String description) {
        this.location = location;
        this.jobId = jobId;
        this.description = description;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
