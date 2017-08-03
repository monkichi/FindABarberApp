package com.parse.starter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.starter.findabarberapp.R;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by chris on 6/21/16.
 */
public class BarberListArrayAdapter extends ArrayAdapter<BarberInfo> {

    //Context
    Context appContext;
    int listLayoutId;
    List<BarberInfo> barberInfoList;
    public BarberListArrayAdapter(Context context, int resource, List<BarberInfo> objects) {
        super(context, resource, objects);
        appContext = context;
        listLayoutId = resource;
        barberInfoList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ListViewItemholder  viewHolder;
        //Check if view is inflated
        View view = convertView;
        if (view == null){
            //Inflater
            LayoutInflater inflater = (LayoutInflater) appContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view
            view = inflater.inflate(listLayoutId,parent,false);

            viewHolder = new ListViewItemholder();
            viewHolder.mBarberName = (TextView) view.findViewById(R.id.barberName);
            viewHolder.mBarberAddress = (TextView) view.findViewById(R.id.barberAddress);
            viewHolder.mDistanceFromBarber = (TextView) view.findViewById(R.id.distanceFromBarber);


            view.setTag(viewHolder);
        }
        else{
            viewHolder = (ListViewItemholder) view.getTag();
        }
        BarberInfo info = barberInfoList.get(position);
        //Update the UI
        viewHolder.mBarberName.setText(info.barberName);
        viewHolder.mBarberAddress.setText(info.barberPlaceName);
        BigDecimal bd = new BigDecimal(info.distanceFromBarber).setScale(2, RoundingMode.HALF_EVEN);
        Double roundedDistance = bd.doubleValue();
        viewHolder.mDistanceFromBarber.setText(String.valueOf(roundedDistance) +" miles");

        return view;
    }
    private class ListViewItemholder{
        TextView mBarberName;
        TextView mBarberAddress;
        TextView mDistanceFromBarber;

    }
}
