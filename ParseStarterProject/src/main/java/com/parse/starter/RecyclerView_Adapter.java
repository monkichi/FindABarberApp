package com.parse.starter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.parse.starter.findabarberapp.R;

import java.util.ArrayList;

/**
 * Created by chris on 6/18/17.
 */

public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    // recyclerview adapter
    private ArrayList<ExploreBarbersImagesDataModel> arrayList;
    private Context context;
    private ExploreBarbersImagesDataModel model;

    public RecyclerView_Adapter(Context context,
                                ArrayList<ExploreBarbersImagesDataModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;

    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This method will inflate the custom layout and return as viewholder
        LayoutInflater mInflater = LayoutInflater.from(parent.getContext());

        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(
                R.layout.explore_photos_item_row, parent, false);
        RecyclerViewHolder listHolder = new RecyclerViewHolder(mainGroup);
        return listHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
         model = arrayList.get(position);

        RecyclerViewHolder mainHolder = (RecyclerViewHolder) holder;// holder

        Bitmap image = model.getImage();// This will convert drawbale image into
        // bitmap

        // setting title
        mainHolder.barberImageTitle.setText(model.getBarberUserID());

        mainHolder.imageView.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

}
