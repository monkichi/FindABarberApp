package com.parse.starter;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 6/17/17.
 */

public class ExploreBarbersPhotosActivity extends AppCompatActivity {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private List<ParseObject> nearbyBarberObjestList;
    private ParseQuery<ParseObject> nearestBarbersQuery;
    private ParseQuery<ParseObject> nearestBarberImagesQuery;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

//        //Set up Grid View
//        GridView gridview = (GridView) findViewById(R.id.gridview);
//        if (gridview != null) {
//            gridview.setAdapter(new ImageAdapter(this));
//            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                    Toast.makeText(ExploreBarbersPhotosActivity.this, "" + position,
//                            Toast.LENGTH_SHORT).show();
//                }
//            });
//        }



        /*
            Code for Scrollable GridView
        * */

        recyclerView = (RecyclerView)
                findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        //recyclerView.setLayoutManager(new LinearLayoutManager(ExploreBarbersPhotosActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView
                .setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));// Here 2 is no. of columns to be displayed

        // populate the list view by adding data to arraylist
        populatRecyclerView();

        //getExploreBarberImages();


    }

    // references to our images
    private Integer[] mThumbIds = {
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7,
            R.drawable.sample_0, R.drawable.sample_1,
            R.drawable.sample_2, R.drawable.sample_3,
            R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_6, R.drawable.sample_7
    };


    // populate the list view by adding data to arraylist
    private void populatRecyclerView() {
        ArrayList<ExploreBarbersImagesDataModel> arrayList = new ArrayList<>();
        for (int i = 0; i < mThumbIds.length; i++) {
            arrayList.add(new ExploreBarbersImagesDataModel("Yo",mThumbIds[i]));
        }
        RecyclerView_Adapter  adapter = new RecyclerView_Adapter(ExploreBarbersPhotosActivity.this, arrayList);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview
        adapter.notifyDataSetChanged();// Notify the adapter

    }


    private void getExploreBarberImages(){
        //Query barbers around the lates position
            // get latest position
       nearbyBarberObjestList = new ArrayList<>();
            LatLng usersLastPositionLatLng = new LatLng(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"));
            //query from lates postion
        nearestBarbersQuery = ParseQuery.getQuery("Barbers");
        nearestBarbersQuery.whereNear("barberAddress",new ParseGeoPoint(usersLastPositionLatLng.latitude,usersLastPositionLatLng.longitude));
        //Set limit of items returned near by
        nearestBarbersQuery.setLimit(150);
        nearestBarbersQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null ){
                    if (objects.size() > 0){
                        nearbyBarberObjestList = objects;
                        Log.i("nearbyBarberImagesTest", "Got Barber Object");
                        for (ParseObject barbers : nearbyBarberObjestList ) {
                            nearestBarberImagesQuery = ParseQuery.getQuery("Images");
                            nearestBarberImagesQuery.whereEqualTo("UserObjectId",barbers.get("barberUserId"));
                            nearestBarberImagesQuery.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> objects, ParseException e) {
                                    if (e == null){
                                        if (objects.size() > 0){


                                        }
                                        else{
                                            Log.i("foundNearbyTest", "Zero Image Objests from query");
                                        }
                                    }
                                    else{
                                        Log.e("nearbyImagesTest", "Got no Images " + e.toString());
                                    }
                                }
                            });

                        }
                    }
                    else{
                        Log.i("Barber Query", "No data objects returned size : " + objects.size());
                    }
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction())
//        {
//            // when user first touches the screen we get x and y coordinate
//            case MotionEvent.ACTION_DOWN:
//
//            {
//                x1 = event.getX();
//                y1 = event.getY();
//                break;
//            }
//            case MotionEvent.ACTION_UP:
//            {
//                x2 = event.getX();
//                y2 = event.getY();
//
//                //if left to right sweep event on screen
//                if (x1 < x2)
//                {
//                    Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();
//                    Intent rightSwipeViewFollowingIntent = new Intent(getApplicationContext(),UserMapsActivity.class);
//                    startActivity(rightSwipeViewFollowingIntent);
//                }
//
//                // if right to left sweep event on screen
//                if (x1 > x2)
//                {
//                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
//
//
//                }
//
//                // if UP to Down sweep event on screen
//                if (y1 < y2)
//                {
//                    Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_SHORT).show();
//                }
//
//                //if Down to UP sweep event on screen
//                if (y1 > y2)
//                {
//                    Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_SHORT).show();
//                }
//                break;
//            }
//        }
//        return false;
//    }

}
