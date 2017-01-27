package com.pahuza.pahuza.models;


import android.graphics.Bitmap;

import com.google.firebase.auth.FirebaseUser;

/**
 * Created by baryariv on 25/12/2016.
 */
public class Job {

    private String id;
    private FirebaseUser user;
    private FirebaseUser userResponse;
    private CustomPlace place;
    private String description;
    private String photo;
    private String status; // 0 - requested , 1 - done


    public Job() {

    }

    public Job(String id, FirebaseUser user, CustomPlace place, String description, String status) {
        this.id = id;
        this.place = place;
        this.description = description;
        this.status = status;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public void setUser(FirebaseUser user) {
        this.user = user;
    }

    public CustomPlace getPlace() {
        return place;
    }

    public void setPlace(CustomPlace place) {
        this.place = place;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public FirebaseUser getUserResponse() {
        return userResponse;
    }

    public void setUserResponse(FirebaseUser userResponse) {
        this.userResponse = userResponse;
    }
}
