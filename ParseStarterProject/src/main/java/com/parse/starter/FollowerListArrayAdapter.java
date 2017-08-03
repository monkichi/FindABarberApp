package com.parse.starter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.parse.starter.findabarberapp.R;

import java.util.List;

/**
 * Created by chris on 6/23/16.
 */
public class FollowerListArrayAdapter extends ArrayAdapter<FollowerInfo> {
    Context appContext;
    FollowerInfo followerInfo;
    List<FollowerInfo> followerInfoList;
    int itemLayoutId;


    public FollowerListArrayAdapter(Context context, int resource, List<FollowerInfo> objects) {
        super(context, resource, objects);
        this.followerInfoList = objects;
        this.appContext=context;
        this.itemLayoutId=resource;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        followerListItem viewHolder;
        //Check if view is not inflated
        if(view == null){
            //Get inflater from system service
            LayoutInflater inflater =(LayoutInflater) appContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //inflate view layout from inflater
            view = inflater.inflate(itemLayoutId,parent,false);
            //instantiaate all the view widgets
             viewHolder = new followerListItem();
            viewHolder.followerBarberOrUserTextView = (TextView) view.findViewById(R.id.followerBarberOrUserTextView);
            viewHolder.followerUserNameTextView = (TextView) view.findViewById(R.id.followerUserName);
            viewHolder.followerProfileImage = (ImageView) view.findViewById(R.id.followerProfileImageView);

            view.setTag(viewHolder);


        }
        else{
            viewHolder = (followerListItem) view.getTag();

        }

        followerInfo = followerInfoList.get(position);
        if (viewHolder.followerProfileImage == null ){
            viewHolder.followerUserNameTextView.setText(followerInfo.followerUserName);
            viewHolder.followerBarberOrUserTextView.setText(followerInfo.followerBarberOrUser);


        }
        else{
            viewHolder.followerUserNameTextView.setText(followerInfo.followerUserName);
            viewHolder.followerBarberOrUserTextView.setText(followerInfo.followerBarberOrUser);
            viewHolder.followerProfileImage.setImageBitmap(followerInfo.followerProfileImage);
        }




        return view;
    }
    private class followerListItem{
        TextView followerUserNameTextView;
        TextView followerBarberOrUserTextView;
        ImageView followerProfileImage;

    }
}
