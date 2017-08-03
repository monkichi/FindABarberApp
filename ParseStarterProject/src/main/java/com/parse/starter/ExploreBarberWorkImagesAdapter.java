package com.parse.starter;

/**
 * Created by chris on 7/4/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;


import com.parse.starter.findabarberapp.R;

import java.util.ArrayList;

/**
 * Created by chris on 6/22/17.
 */

public class ExploreBarberWorkImagesAdapter  extends RecyclerView.Adapter<BarberWorkImagesViewHolder>  {
    // recyclerview adapter
    private ArrayList<ExploreBarbersImagesDataModel> arrayList;
    private Context context;

    public ExploreBarberWorkImagesAdapter(Context context,
                                   ArrayList<ExploreBarbersImagesDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    @Override
    public BarberWorkImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.barber_work_images_item_row, parent, false);
        BarberWorkImagesViewHolder listHolder = new BarberWorkImagesViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(BarberWorkImagesViewHolder holder, int position) {


        BarberWorkImagesViewHolder mainHolder = (BarberWorkImagesViewHolder) holder;// holder

        if (arrayList!= null && arrayList.size() > 0) {
            Bitmap image = arrayList.get(position).getImage();// This will convert drawbale image into
            // bitmap

            // setting title
            holder.imageView.setImageBitmap(image);
        }
        else {

        }


    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

    /*
* Code responsible for taking or selecting the picture we want to display in our
* chosen imageview
* */

}


