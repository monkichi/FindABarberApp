package com.parse.starter;

import android.graphics.Bitmap;

/**
 * Created by chris on 6/18/17.
 */

public class ExploreBarbersImagesDataModel {

    // Getter and Setter model for recycler view items
    private String barberUserID;
    private Bitmap image;

    public ExploreBarbersImagesDataModel(String title,  Bitmap image) {

        this.barberUserID = title;

        this.image = image;
    }

    public String getBarberUserID() {
        return barberUserID;
    }

    public Bitmap getImage() {
        return image;
    }

}
