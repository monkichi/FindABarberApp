package com.parse.starter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.findabarberapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by chris on 7/5/17.
 */

public class BarberMainActivity extends AppCompatActivity implements LocationListener, View.OnTouchListener {


    private List<ParseObject> nearbyBarberObjectsList;
    private ParseQuery<ParseObject> nearestBarbersQuery;
    private ParseQuery<ParseObject> nearestBarberImagesQuery;
    private ArrayList<ExploreBarbersImagesDataModel> exploreBarberImagesRandomList;
    private List<ParseFile> currentBarberImageList;
    private ExploreBarberWorkImagesAdapter adapter;

    //List to store barberMessagesReceivedIds
    List<MessagesDataModel> barberMessagesInfoObjectList;

    //BarberObject for his/her info
    ParseObject barberObject;

    //Current User object
    private ArrayList<ParseObject> barberMessagesObjectsList;
    ParseUser currentUser;
    private float x1;
    private float y1;
    private float x2;
    private float y2;
    private Bundle barberUserIdBundle;
    private ParseObject barber;
    private ParseObject barberFollowerObject;
    private String barberUserId;
    private String currentUserId;
    private MessagesListAdapter arrayAdapter;
    private Location userLastLocation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.barber_main_layout);

        //Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("Welcome Barber");
        setSupportActionBar(toolbar);
        //Get the barber user for the barber Main activity
        currentUser = ParseUser.getCurrentUser();

        currentUserId= currentUser.getObjectId();


        //Query the barberObject
        ParseQuery<ParseObject> currentBarberQuery = ParseQuery.getQuery("Barbers");
        currentBarberQuery.whereEqualTo("barberUserId", currentUser.getObjectId());
        currentBarberQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    barberObject = object;

                    barberUserIdBundle = new Bundle();
                    barberUserIdBundle.putString("barberObjectId", barberObject.getObjectId());
                    barberUserId = object.getString("barberUserId");
                    Log.i("barberMessages","Got the barber object to check for its messages");
                }
                else{
                    Log.e("barberMessagesError", "Was not able to get the current users barber profile error is = " + e.toString());
                }
            }
        });


        //List for messages
        barberMessagesObjectsList =new ArrayList<ParseObject>();
        barberMessagesInfoObjectList = new ArrayList<MessagesDataModel>();



        //Start getting location updates
        if (ActivityCompat.checkSelfPermission(BarberMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(BarberMainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("Permissions Test", "Permissions are not set");
            ActivityCompat.requestPermissions(BarberMainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},32);

        }
        else{
            LocationManager mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String provider = mLocationManager.getBestProvider(new Criteria(), false);

            //Get the last know users location from Device
             userLastLocation = mLocationManager.getLastKnownLocation(provider);
            //When device does not know last location, request location
            if (userLastLocation == null) {
                mLocationManager.requestLocationUpdates(provider, 400, 1, this);
                //Get the last know users location
                userLastLocation = mLocationManager.getLastKnownLocation(provider);
            }
            else{
                getExploreBarberImages(userLastLocation);
            }

        }




        RecyclerView recyclerView = (RecyclerView)
                findViewById(R.id.barber_work_images_recycler_view);
        recyclerView.setHasFixedSize(true);

//        recyclerView.setLayoutManager(new LinearLayoutManager(ExploreBarbersPhotosActivity.this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));// Here 2 is no. of columns to be displayed

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    // when user first touches the screen we get x and y coordinate
                    case MotionEvent.ACTION_DOWN:
                    {
                        x1 = event.getX();
                        y1 = event.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        x2 = event.getX();
                        y2 = event.getY();

                        //if left to right sweep event on screen
                        if (x1 < x2)
                        {
                            Toast.makeText(BarberMainActivity.this, "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();


                            Intent rightSwipeActivityIntent = new Intent(getApplicationContext(),ViewFollowersActivity.class);
                            startActivity(rightSwipeActivityIntent);



                        }

                        // if right to left sweep event on screen
                        if (x1 > x2)
                        {

                            Toast.makeText(BarberMainActivity.this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                            Intent leftSwipeBarberActivityIntent = new Intent(getApplicationContext(),BarberProfileActivity.class);
                            leftSwipeBarberActivityIntent.putExtras(barberUserIdBundle);
                            startActivity(leftSwipeBarberActivityIntent);

                        }

                        // if UP to Down sweep event on screen
                        if (y1 < y2)
                        {
                            Toast.makeText(BarberMainActivity.this, "UP to Down Swap Performed", Toast.LENGTH_SHORT).show();
                        }

                        //if Down to UP sweep event on screen
                        if (y1 > y2)
                        {
                            Toast.makeText(BarberMainActivity.this, "Down to UP Swap Performed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                return false;
            }
        });


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

        //list to hold all images of random barbers
        exploreBarberImagesRandomList = new ArrayList<ExploreBarbersImagesDataModel>();
        currentBarberImageList = new ArrayList<ParseFile>();

        adapter = new ExploreBarberWorkImagesAdapter(BarberMainActivity.this,  exploreBarberImagesRandomList);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview


        //Call method to get all the images for this explore barber work images activity in backgroud

        getBarberFollewersFromDB();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.barber_main_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId ==R.id.logOut_action) {
            logOut();
        }
        else if (itemId == R.id.check_messages_action) {
            //Create the Dialog to view the messages
            Log.i("checkMessagesCheck","Check messages icon has been pressed");


            AlertDialog.Builder viewMessagesBuilder = new AlertDialog.Builder(BarberMainActivity.this);
            viewMessagesBuilder.setTitle("View Messages");

             arrayAdapter = new MessagesListAdapter(BarberMainActivity.this, barberMessagesInfoObjectList, R.layout.messages_item_row_layout);

            viewMessagesBuilder.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
//           /*
//              Send Current Barber Profile a New Message Code
//          */

//            viewMessagesBuilder.setPositiveButton("New Message", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //                                            //Create Alter view fro replying to message
//                    AlertDialog.Builder builderInnerInnerSendMessage = new AlertDialog.Builder(BarberMainActivity.this);
//                    //Add senders userNames
//                    builderInnerInnerSendMessage.setTitle("Send Message to:" + barberObject.getString("barberUserName"));
//                    final EditText replyMessageEditText = new EditText(BarberMainActivity.this);
//                    builderInnerInnerSendMessage.setView(replyMessageEditText);
//                    builderInnerInnerSendMessage.setPositiveButton("Send", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            //Get the message from EditText
//                            String replyMessage = replyMessageEditText.getText().toString();
//                            Log.i("gotMessageTest", "This is the message form editText: " + replyMessage);
//                            //Create the Message Object
//                            ParseObject newMessageObject = new ParseObject("Messages");
//                            //Set the Acl for the object
//                            ParseACL defaultAcl = new ParseACL();
//                            defaultAcl.setPublicWriteAccess(true);
//                            defaultAcl.setPublicReadAccess(true);
//                            newMessageObject.setACL(defaultAcl);
//                            //Store the senders data as the current user since we are sending the message
//                            newMessageObject.put("senderUserId",currentUser.getObjectId());
//                            newMessageObject.put("senderUserName",currentUser.getUsername());
//                            //Should be the barber we want to send message to
//                            newMessageObject.put("receiverUserId",barberObject.getString("barberUserId"));
//                            newMessageObject.put("receiverUserName",barberObject.getString("barberUserName"));
//                            newMessageObject.put("messageContent",replyMessage);
//                            newMessageObject.put("messageReadOrUnread",false);
//
//                            newMessageObject.saveInBackground(new SaveCallback() {
//                                @Override
//                                public void done(ParseException e) {
//                                    if(e== null){
//                                        Log.i("messageReplySent","Message was saved succesfully");
//                                        Toast.makeText(getApplicationContext(),"Message was sent to: " + barberObject.getString("barberUserName"),Toast.LENGTH_SHORT).show();
//                                    }
//                                    else{
//                                        Log.i("messageReplySent","Error saving message"+ e.getMessage());
//                                        Toast.makeText(getApplicationContext(),"Message was not able to send to: " + barberObject.getString("barberUserName")+"\n" +"error is "+ e.getMessage(),Toast.LENGTH_SHORT).show();
//
//                                    }
//                                }
//                            });
//
//                        }
//                    });
//                    builderInnerInnerSendMessage.show();
//                }
//            });

            viewMessagesBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builderInnerMessageView = new AlertDialog.Builder(
                                    BarberMainActivity.this);
                            //Update that message Parse object has been read
                            barberMessagesObjectsList.get(which).put("messageReadOrUnread", true);
                            barberMessagesObjectsList.get(which).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null){
                                        Log.i("messageReadUpdate", "Succesfully updated the database that message is read");
                                    }
                                    else{
                                        Log.e("messageReadUpdate", "Message status was NOT " + e.toString());

                                    }
                                }
                            });
                            builderInnerMessageView.setTitle("Your message from: "+ barberMessagesInfoObjectList.get(which ).getMessageSenderUserName());
                            builderInnerMessageView.setMessage(barberMessagesObjectsList.get(which).getString("messageContent"));


                            builderInnerMessageView.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            builderInnerMessageView.setPositiveButton(
                                    "Reply",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.i("messageClicked","position: "+ which);
                                            //Create Alter view fro replying to message
                                            AlertDialog.Builder builderInnerInnerReplyMessage = new AlertDialog.Builder(BarberMainActivity.this);
                                            //Add senders userName
                                            builderInnerInnerReplyMessage.setTitle("Reply to: " + barberMessagesInfoObjectList.get(which + 1).getMessageSenderUserName());
                                            final EditText replyMessageEditText = new EditText(BarberMainActivity.this);
                                            builderInnerInnerReplyMessage.setView(replyMessageEditText);

                                            builderInnerInnerReplyMessage.setNegativeButton(R.string.negative_button_text, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });

                                            builderInnerInnerReplyMessage.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, final int which) {
                                                    //Create the Message Object
                                                    //Get the message from EditText
                                                    String replyMessage = replyMessageEditText.getText().toString();
                                                    ParseObject newMessageObject = new ParseObject("Messages");
                                                    newMessageObject.put("senderUserId",currentUser.getObjectId());
                                                    newMessageObject.put("receiverUserId",barberMessagesObjectsList.get(which +1).getString("senderUserId"));
                                                    newMessageObject.put("senderUserName",currentUser.getUsername());
                                                    //Should be the users userID
                                                    newMessageObject.put("receiverUserName",barberMessagesObjectsList.get(which +1).getString("senderUserName"));
                                                    newMessageObject.put("messageContent",replyMessage);
                                                    newMessageObject.put("messageReadOrUnread",false);
                                                    newMessageObject.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if(e== null){
                                                                Log.i("messageReplySent","Message was saved succesfully");
                                                                Toast.makeText(getApplicationContext(),"Message was sent to: " + barberMessagesObjectsList.get(which + 1).getString("senderUserId"),Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{
                                                                Log.i("messageReplySent","Error saving message"+ e.getMessage());
                                                                Toast.makeText(getApplicationContext(),"Message was not able to send to: " + barberMessagesObjectsList.get(which + 1).getString("senderUserId")+"\n" +"error is "+ e.getMessage(),Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });

                                                    Log.i("gotMessageTest", "This is the message form editText: " + replyMessage);

                                                    //Let user know mesage was sent
                                                    //close dialog
                                                }
                                            });
                                            builderInnerInnerReplyMessage.show();
                                        }
                                    });
                            builderInnerMessageView.show();
                        }
                    });
            viewMessagesBuilder.show();

        }
        else if (R.id.update_profile == itemId){
            Intent updateProfile = new Intent(getApplicationContext(),UpdateBarberProfileActivity.class);

            updateProfile.putExtras(barberUserIdBundle);
            startActivity(updateProfile);
        }


        return super.onOptionsItemSelected(item);
    }

    public  void logOut(){
        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){
                    if(ParseUser.getCurrentUser() == null){
                        Log.i("LogOut Test","Log out was succesful" );
                        Intent signUpIntent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(signUpIntent);
                    }
                    else{
                        Log.i("LogOut Test","Parse user not null"+ " " +ParseUser.getCurrentUser().getUsername());
                    }

                }
                else{
                    Log.i("LogOut Test", "Log out was unsuccesful" +" "+ e.getMessage());
                }
            }
        });
    }

    private void getExploreBarberImages(Location currLocation){
        //Query barbers around the latest position
        //list of barberObjects for information purposes
        nearbyBarberObjectsList = new ArrayList<>();
        // get latest position


        //query from users latest postion nearby
        nearestBarbersQuery = ParseQuery.getQuery("Barbers");
        nearestBarbersQuery.whereNear("barberAddress",new ParseGeoPoint(currLocation.getLatitude(),currLocation.getLongitude()));
        //Set limit of items returned near by
        nearestBarbersQuery.setLimit(150);
        nearestBarbersQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null ){
                    if (objects.size() > 0){
                        nearbyBarberObjectsList = objects;
                        Log.i("nearbyBarberImagesTest", "Got Barber Objects : " + nearbyBarberObjectsList.size());
                        for (ParseObject barbers : nearbyBarberObjectsList ) {
                            barber = barbers;
                            nearestBarberImagesQuery = ParseQuery.getQuery("Images");
                            nearestBarberImagesQuery.whereEqualTo("UserObjectId", barbers.get("barberUserId"));
                            nearestBarberImagesQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject object, ParseException e) {
                                    if (e == null ) {
                                        //get image list for current barber
                                        currentBarberImageList = object.getList("ImagesFileList");
                                        if (currentBarberImageList.size() > 0) {
                                            //iterate through all images from current barber into ExploreBarberImageList
                                            if (currentBarberImageList.get(0) != null) {
                                                Log.i("nearbyBarberImagesTest", "Got Barber Images : " + currentBarberImageList.size());
                                                for (int i = 0; i < currentBarberImageList.size() ; i++) {
                                                    if (currentBarberImageList.get(i) != null) {

                                                        ParseFile workImage =  currentBarberImageList.get(i);
                                                        byte[] barberWorkImageByteArray;
                                                        if (workImage != null) {
                                                            try {
                                                                barberWorkImageByteArray = workImage.getData();
                                                                ExploreBarbersImagesDataModel model = new ExploreBarbersImagesDataModel(barber.getObjectId(), BitmapFactory.decodeByteArray(barberWorkImageByteArray, 0, barberWorkImageByteArray.length));
                                                                exploreBarberImagesRandomList.add(model);
                                                                //All Nearest Barbers Images should be in exploreBarberImagesRandomList
                                                                Log.i("nearbyBarberImagesTest", "BarberWorkImagesAdapter size : " + exploreBarberImagesRandomList.size());
                                                                Collections.shuffle(exploreBarberImagesRandomList);
                                                                adapter.notifyDataSetChanged();// Notify the adapter\
                                                            } catch (ParseException e1) {
                                                                e1.printStackTrace();
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                        //Condition when currentBarberImageList
                                        else{
                                            Log.i("nearbyBarberImagesTest", "Barber has no images in his list : "  );
                                        }
                                    }
                                    else{
                                        Log.i("foundNearbyTest", "Zero Image Objests from query" + e.toString());
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

    }//End of getBarberImages method

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction())
        {
            // when user first touches the screen we get x and y coordinate
            case MotionEvent.ACTION_DOWN:
            {
                x1 = event.getX();
                y1 = event.getY();
                break;
            }
            case MotionEvent.ACTION_UP:
            {
                x2 = event.getX();
                y2 = event.getY();

                //if left to right sweep event on screen
                if (x1 < x2)
                {
                    Toast.makeText(this, "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();


                    Intent rightSwipeActivityIntent = new Intent(getApplicationContext(),ViewFollowersActivity.class);
                    startActivity(rightSwipeActivityIntent);



                }

                // if right to left sweep event on screen
                if (x1 > x2)
                {

                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                    Intent leftSwipeBarberActivityIntent = new Intent(getApplicationContext(),BarberProfileActivity.class);
                    leftSwipeBarberActivityIntent.putExtras(barberUserIdBundle);
                    startActivity(leftSwipeBarberActivityIntent);

                }

                // if UP to Down sweep event on screen
                if (y1 < y2)
                {
                    Toast.makeText(this, "UP to Down Swap Performed", Toast.LENGTH_SHORT).show();
                }

                //if Down to UP sweep event on screen
                if (y1 > y2)
                {
                    Toast.makeText(this, "Down to UP Swap Performed", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
        return false;
    }
    private void getBarberFollewersFromDB(){
        ParseQuery<ParseObject> barberFollowersQuery = ParseQuery.getQuery("Followers");
        barberFollowersQuery.whereEqualTo("followerUserId",currentUserId);
        barberFollowersQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    Log.i("FollowersQuery", "Followers Query was successful");
                    //Store barberFollower Object
                    barberFollowerObject = object;
                    //Query the messages objects for messages for currentUserID
                    final ParseQuery<ParseObject> currentUserMessagesQuery = new ParseQuery<ParseObject>("Messages");
                    currentUserMessagesQuery.whereEqualTo("receiverUserId", currentUserId);
                    currentUserMessagesQuery.orderByDescending("createdAt");
                    currentUserMessagesQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if (e==null){
                                if (objects.size()>0){
                                    Log.i("currentUserMessages","Messages Query was succesful");
                                    for (ParseObject message: objects)
                                    {
                                       //
                                        //Message content
                                        String messageContent= message.getString("messageContent") ;
                                        //Store the date it was created

                                        //Store the sendersUserName
                                        String messageSenderUserName = message.getString("senderUserName");

                                        //Store the receiversUserName
                                        String messageReceiverUserName = message.getString("receiverUserName");

                                        //Message read or not read
                                        boolean messageReadStatus = message.getBoolean("messageReadOrUnread");

                                        //Message ReceiverObjectId
                                        String getMessageReceiverUserId = message.getString("receiverUserId");


                                        //Create the message data model to store all possible message data
                                        MessagesDataModel messageModel = new MessagesDataModel(messageContent,messageSenderUserName,messageReceiverUserName,messageReadStatus,getMessageReceiverUserId);

                                        barberMessagesInfoObjectList.add(messageModel);
                                        //Add the messages for currentUser to list
                                        barberMessagesObjectsList.add(message);
                                        //Add the messages sender userId to list



                                    }
                                }
                            }
                            else{
                                Log.i("currentUserMessages","Error getting messages for: " + currentUserId +  " error is " + e.getMessage());
                            }
                        }
                    });
                }
                else{
                    Log.i("FollowersQuery","Error getting query " + e.getMessage());
                }
            }
        });
    }
}


