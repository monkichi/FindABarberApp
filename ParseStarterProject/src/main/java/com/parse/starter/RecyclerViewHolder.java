package com.parse.starter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.starter.findabarberapp.R;

/**
 * Created by chris on 6/18/17.
 */

public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    //View holder for gridview Recycler view as we use in listview
    public TextView barberImageTitle;
    public ImageView imageView;


    public RecyclerViewHolder(View itemView) {
        super(itemView);
        // Find all views ids
        this.barberImageTitle = (TextView) itemView.findViewById(R.id.barberImageTitle);
        this.imageView = (ImageView) itemView
                .findViewById(R.id.barberImage);
        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Log.d("RecyclerView", "CLICK!");
        Toast.makeText(v.getContext(),"Image Clicked",Toast.LENGTH_LONG).show();
    }
}
