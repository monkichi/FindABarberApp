package com.parse.starter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.starter.findabarberapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chris on 6/17/17.
 */

public class ExploreBarbersPhotosActivity extends AppCompatActivity {
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private List<ParseObject> nearbyBarberObjectsList;
    private ParseQuery<ParseObject> nearestBarbersQuery;
    private ParseQuery<ParseObject> nearestBarberImagesQuery;
    private RecyclerView recyclerView;
    private ArrayList<ExploreBarbersImagesDataModel> exploreBarberImagesRandomList;
    private List<ParseFile> currentBarberImageList;
    private ExploreBarberWorkImagesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recyclerview);

        //Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Explore Barbers");
        setSupportActionBar(toolbar);
        /*
            Code for Scrollable Recycler View
        * */

        recyclerView = (RecyclerView)
                findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

//        recyclerView.setLayoutManager(new LinearLayoutManager(ExploreBarbersPhotosActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));// Here 2 is no. of columns to be displayed



        //Add click interface for RecyclerView
        ItemClickSupport.addTo(recyclerView)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        // do it
                        Toast.makeText(getApplicationContext(), "Item # " + position + "BarberUserId" + exploreBarberImagesRandomList.get(position).getBarberUserID() , Toast.LENGTH_SHORT).show();

                        //get the clicked items barberUserId
                        String barberUserId = exploreBarberImagesRandomList.get(position).getBarberUserID();

                        //Create intent to start BarberProfileActivity
                        Intent goToBarberProfileIntent = new Intent(getApplicationContext(), BarberProfileActivity.class);
                        //Bundle the data to send to BarberProfileActivity
                        Bundle bundle = new Bundle();
                        bundle.putString("barberObjectId",barberUserId);
                        goToBarberProfileIntent.putExtras(bundle);
                        //call method to start the new activity
                        startActivity(goToBarberProfileIntent);
                    }
                });

        // populate the list view by adding data to arraylist
        //populatRecyclerView();


        //list to hold all images of random barbers
        exploreBarberImagesRandomList = new ArrayList<ExploreBarbersImagesDataModel>();
        currentBarberImageList = new ArrayList<ParseFile>();



        //Call method to get all the images for this explore barber work images activity in backgroud
        getExploreBarberImages();

        adapter = new ExploreBarberWorkImagesAdapter(ExploreBarbersPhotosActivity.this,  exploreBarberImagesRandomList);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview




    }


    private void getExploreBarberImages(){
        //Query barbers around the latest position
       //list of barberObjects for information purposes
        nearbyBarberObjectsList = new ArrayList<>();
        // get latest position
        LatLng usersLastPositionLatLng = new LatLng(getIntent().getExtras().getDouble("latitude"), getIntent().getExtras().getDouble("longitude"));
            //query from users latest postion nearby
        nearestBarbersQuery = ParseQuery.getQuery("Barbers");
        nearestBarbersQuery.whereNear("barberAddress",new ParseGeoPoint(usersLastPositionLatLng.latitude,usersLastPositionLatLng.longitude));
        //Set limit of items returned near by
        nearestBarbersQuery.setLimit(150);
        nearestBarbersQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> objects, ParseException e) {
                if (e == null ){
                    if (objects.size() > 0){
                        nearbyBarberObjectsList = objects;
                        Log.i("exploreNearbyBarbers", "Got Nearby Barber Objects : " + nearbyBarberObjectsList.size());
                        for (final ParseObject barbers : nearbyBarberObjectsList ) {
                            nearestBarberImagesQuery = ParseQuery.getQuery("Images");
                            nearestBarberImagesQuery.whereEqualTo("UserObjectId", barbers.get("barberUserId"));
                            nearestBarberImagesQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null) {
                                        //get image list for current barber
                                        currentBarberImageList = object.getList("ImagesFileList");
                                        //iterate through all images from current barber into ExploreBarberImageList
                                        if (currentBarberImageList != null && currentBarberImageList.size() > 0) {
                                            Log.i("explNeayBarberImages", "Got Barber Images " + currentBarberImageList.size());

                                            if (currentBarberImageList.get(0) != null) {

                                                for (int i = 0; i < currentBarberImageList.size() ; i++) {
                                                    if (currentBarberImageList.get(i) != null) {

                                                        ParseFile workImage = currentBarberImageList.get(i);
                                                        byte[] barberWorkImageByteArray;
                                                        if (workImage != null) {
                                                            try {
                                                                barberWorkImageByteArray = workImage.getData();
                                                                ExploreBarbersImagesDataModel model = new ExploreBarbersImagesDataModel(barbers.getObjectId(), BitmapFactory.decodeByteArray(barberWorkImageByteArray, 0, barberWorkImageByteArray.length));
                                                                exploreBarberImagesRandomList.add(model);
                                                                //All Nearest Barbers Images should be in exploreBarberImagesRandomList
                                                                Log.i("explrImageAllTest", "BarberWorkImagesAdapter size : " + exploreBarberImagesRandomList.size());
                                                                Collections.shuffle(exploreBarberImagesRandomList);
                                                                adapter.notifyDataSetChanged();
                                                            } catch (ParseException e1) {
                                                                e1.printStackTrace();
                                                            }
                                                        }

                                                    }
                                                }//Should be looping thorough all of current barbers images
                                            }

                                        }
                                        //Condition when currentBarberImageList
                                        else{
                                            Log.i("nearbyBarberImagesTest", "Barber has no images in his list : "  );

                                        }
                                    }
                                        else{
                                            Log.i("foundNearbyTest", "Zero Image Objests from query");
                                        }
                                    }
                            });

                        }

                    }
                    else{
                        Log.i("Barber Query", "No data objects returned size : " + objects.size());
                    }


                }
                else{
                    Log.e("BarberQuery", "Barber Query Error " + e.toString());
                }

            }
        });


    }//End of getBarberImages method



}//End of ExploreBarbersPhotosActivity

 class ItemClickSupport {
    private final RecyclerView mRecyclerView;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                // ask the RecyclerView for the viewHolder of this view.
                // then use it to get the position for the adapter
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                mOnItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
        }
    };
    private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClicked(mRecyclerView, holder.getAdapterPosition(), v);
            }
            return false;
        }
    };
    private RecyclerView.OnChildAttachStateChangeListener mAttachListener
            = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            // every time a new child view is attached add click listeners to it
            if (mOnItemClickListener != null) {
                view.setOnClickListener(mOnClickListener);
            }
            if (mOnItemLongClickListener != null) {
                view.setOnLongClickListener(mOnLongClickListener);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {

        }
    };

    private ItemClickSupport(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        // the ID must be declared in XML, used to avoid
        // replacing the ItemClickSupport without removing
        // the old one from the RecyclerView
        mRecyclerView.setTag(R.id.item_click_support, this);
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener);
    }

    public static ItemClickSupport addTo(RecyclerView view) {
        // if there's already an ItemClickSupport attached
        // to this RecyclerView do not replace it, use it
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support == null) {
            support = new ItemClickSupport(view);
        }
        return support;
    }

    public static ItemClickSupport removeFrom(RecyclerView view) {
        ItemClickSupport support = (ItemClickSupport) view.getTag(R.id.item_click_support);
        if (support != null) {
            support.detach(view);
        }
        return support;
    }

    public ItemClickSupport setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
        return this;
    }

    public ItemClickSupport setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
        return this;
    }

    private void detach(RecyclerView view) {
        view.removeOnChildAttachStateChangeListener(mAttachListener);
        view.setTag(R.id.item_click_support, null);
    }

    public interface OnItemClickListener {

        void onItemClicked(RecyclerView recyclerView, int position, View v);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClicked(RecyclerView recyclerView, int position, View v);
    }
}
