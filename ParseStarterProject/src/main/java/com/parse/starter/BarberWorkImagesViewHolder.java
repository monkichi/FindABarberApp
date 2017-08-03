package com.parse.starter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.starter.findabarberapp.R;

/**
 * Created by chris on 6/22/17.
 */

public class BarberWorkImagesViewHolder extends RecyclerView.ViewHolder {

    public ImageView imageView;


    public BarberWorkImagesViewHolder(View itemView) {
        super(itemView);
        // Find all views ids
        this.imageView = (ImageView) itemView
                .findViewById(R.id.barberWorkImage);


    }


}
