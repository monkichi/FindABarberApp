package com.parse.starter;

import android.graphics.Bitmap;

/**
 * Created by chris on 6/23/16.
 */
public class FollowerInfo {
    //userID
    String followerUserId;
    //userName
    String followerUserName;
    //barberOrUser
    String followerBarberOrUser;

    public Bitmap getFollowerProfileImage() {
        return followerProfileImage;
    }

    public void setFollowerProfileImage(Bitmap followerProfileImage) {
        this.followerProfileImage = followerProfileImage;
    }

    //Image Bitmap
    Bitmap followerProfileImage;

    public String getFollowerUserId() {
        return followerUserId;
    }

    public void setFollowerUserId(String followerUserId) {
        this.followerUserId = followerUserId;
    }

    public String getFollowerUserName() {
        return followerUserName;
    }

    public void setFollowerUserName(String followerUserName) {
        this.followerUserName = followerUserName;
    }

    public String getFollowerBarberOrUser() {
        return followerBarberOrUser;
    }

    public void setFollowerBarberOrUser(String followerBarberOrUser) {
        this.followerBarberOrUser = followerBarberOrUser;
    }

    public FollowerInfo(String followerUserId, String followerUserName, String followerBarberOrUser) {

        this.followerUserId = followerUserId;
        this.followerUserName = followerUserName;
        this.followerBarberOrUser = followerBarberOrUser;
    }

    public FollowerInfo() {

    }

}
