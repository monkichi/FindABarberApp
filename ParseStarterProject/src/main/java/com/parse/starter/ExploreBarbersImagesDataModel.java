package com.parse.starter;

/**
 * Created by chris on 6/18/17.
 */

public class ExploreBarbersImagesDataModel {

    // Getter and Setter model for recycler view items
    private String title;
    private int image;

    public ExploreBarbersImagesDataModel(String title,  int image) {

        this.title = title;

        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }

}
