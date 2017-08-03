package com.parse.starter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.findabarberapp.R;

import java.util.ArrayList;
import java.util.List;

public class UserMapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    LocationManager mLocationManager;
    Location userLastLocation;
    //Keeps track of where we are getting the location
    String provider;
    ListView barbersListView;
    ArrayList<BarberInfo> barberUserInfoList;
    BarberInfo barberInfo;
   private float x1,x2;
   private float y1, y2;

     ParseObject barberObject;
     List<String> barberMessagesUserNamesList;
     ParseUser currentUser;
    List<ParseObject> barberMessagesObjectsList;
     List<MessagesDataModel> barberMessagesInfoObjectList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);
        Toolbar actionToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        actionToolbar.setTitle("Welcome");
        setSupportActionBar(actionToolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        barbersListView = (ListView) findViewById(R.id.barbersListView);
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = mLocationManager.getBestProvider(new Criteria(), false);
        barberUserInfoList = new ArrayList<BarberInfo>();

        //Start getting location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.i("Permissions Test", "Permissions are not set");
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},32);

        }

        //Get the last know users location from Device
        userLastLocation = mLocationManager.getLastKnownLocation(provider);
        //When device does not know last location, request location
        if (userLastLocation == null) {
            mLocationManager.requestLocationUpdates(provider, 400, 1, this);
            //Get the last know users location
            userLastLocation = mLocationManager.getLastKnownLocation(provider);
        }


        //Click Listener for selecting nearest barbers list item
  barbersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          //Create intent for BarberProfileActivity
          Intent barberProfileIntent = new Intent(getApplicationContext(),BarberProfileActivity.class);
          //Get Data to send to BarberProfileActivity
         BarberInfo barberSelectedInfo = barberUserInfoList.get(position);
          //Get barberId so BarberProfile can query the barberId for barberInfo
          String barberSelectedId = barberSelectedInfo.getBarberObjectId();
          //Send data to BarberProfileActivity
          Bundle barberInfoBundle = new Bundle();
          barberInfoBundle.putString("barberObjectId", barberSelectedId);
          barberProfileIntent.putExtras(barberInfoBundle);

          startActivity(barberProfileIntent);
      }
  });


         barberMessagesObjectsList = new ArrayList<ParseObject>();
        barberMessagesInfoObjectList = new ArrayList<MessagesDataModel>();

        currentUser = ParseUser.getCurrentUser();

        getUserMessages();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case (32):{
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i("PermissionRequestTest", "Permissions Succesful added");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i("PermissionRequestTest","Permission was not added");
                }
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu,menu);
        return true;
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemId = item.getItemId();

        if (menuItemId == R.id.logOut_action){
            logOut();
        }
        else if (menuItemId == R.id.check_messages_action){
            //Create the Dialog to view the messages
            Log.i("checkMessagesCheck","Check messages icon has been pressed");
            AlertDialog.Builder viewMessagesBuilder = new AlertDialog.Builder(UserMapsActivity.this);
            viewMessagesBuilder.setTitle("View Messages");

            final MessagesListAdapter arrayAdapter = new MessagesListAdapter(
                    UserMapsActivity.this,
                  barberMessagesInfoObjectList,R.layout.messages_item_row_layout);


            viewMessagesBuilder.setNegativeButton(
                    "close",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
//           /*
//              Send Current Barber Profile a New Message Code
//          */
//
//            viewMessagesBuilder.setPositiveButton("New Message", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    //                                            //Create Alter view fro replying to message
//                    AlertDialog.Builder builderInnerInnerSendMessage = new AlertDialog.Builder(UserMapsActivity.this);
//                    //Add senders userNames
//                    builderInnerInnerSendMessage.setTitle("Send Message to:" + barberObject.getString("barberUserName"));
//                    final EditText replyMessageEditText = new EditText(UserMapsActivity.this);
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
//                            });//End of save in background call back
//
//                        }
//                    });
//                    builderInnerInnerSendMessage.show();
//                }
//            });

            viewMessagesBuilder.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Create another dialog to display the received message
                            AlertDialog.Builder builderInnerMessageView = new AlertDialog.Builder(
                                    UserMapsActivity.this);
                            //Update db messageReadorUnread that message has been read
                            barberMessagesObjectsList.get(which).put("messageReadOrUnread", true);
                            barberMessagesObjectsList.get(which).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null){
                                        Log.i("messageReadUpdate", "Updated the database that message is read");
                                    }
                                    else{
                                        Log.e("messageReadUpdate", "ERROR:" + e.toString());
                                    }
                                }
                            });
                            //Set view messages users name from who we got message from
                            builderInnerMessageView.setTitle("Your message from: "+ barberMessagesInfoObjectList.get(which).getMessageSenderUserName());
                            //Display the contents of the message from database
                            builderInnerMessageView.setMessage(barberMessagesObjectsList.get(which).getString("messageContent"));
                            //Set button listeners for InnerMessage view
                            builderInnerMessageView.setPositiveButton(
                                    "Reply",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.i("messageClicked","position: "+ which);
                                            //Create Alter view fro replying to message
                                            AlertDialog.Builder builderInnerInnerReplyMessage = new AlertDialog.Builder(UserMapsActivity.this);
                                            //Add senders userName
                                            builderInnerInnerReplyMessage.setTitle("Reply to: " + barberMessagesInfoObjectList.get(which + 1).getMessageSenderUserName());
                                            final EditText replyMessageEditText = new EditText(UserMapsActivity.this);
                                            builderInnerInnerReplyMessage.setView(replyMessageEditText);

                                            builderInnerInnerReplyMessage.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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
                                                   //Send userId to database as source of message
                                                    newMessageObject.put("senderUserId",currentUser.getObjectId());
                                                    //Send receiverBarberId
                                                    newMessageObject.put("receiverUserId",barberMessagesObjectsList.get(which +1).getString("senderUserId"));
                                                    newMessageObject.put("senderUserName",currentUser.getUsername());
                                                    //Should be the users userID
                                                    newMessageObject.put("receiverUserName",barberMessagesObjectsList.get(which +1).getString("senderUserName"));
                                                    newMessageObject.put("messageContent",replyMessage);
                                                    newMessageObject.put("messageReadOrUnread", false);
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
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (userLastLocation!= null){
            Log.i("UsersLastLocationTest","User's last location is " + userLastLocation.getLatitude() +" " +userLastLocation.getLongitude());
            // Add a marker in curr location and move camera
            LatLng userCoordinates = new LatLng(userLastLocation.getLatitude(),userLastLocation.getLongitude());
            mMap.addMarker(new MarkerOptions().position(userCoordinates).title("Your location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userCoordinates,10));
            //Trying to query the barbers around the current location
            final ParseGeoPoint barberLookUpCoordinate = new ParseGeoPoint(userLastLocation.getLatitude(),userLastLocation.getLongitude());
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Barbers");
            query.whereNear("barberAddress", barberLookUpCoordinate);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        Log.d("barberNearTest", "Retrieved " + objects.size() + " barbers");
                        for (ParseObject barbers : objects){
                            barberInfo = new BarberInfo();
                            //Set the place name
                            barberInfo.setBarberPlaceName(barbers.getString("barberPlaceName"));
                            //Set the users locations
                            ParseGeoPoint barberLocation = barbers.getParseGeoPoint("barberAddress");
                            barberInfo.setBarberLocation(new LatLng(barberLocation.getLatitude(),barberLocation.getLongitude()));
                            //Set the users message
                            barberInfo.setBarberAboutMessage(barbers.getString("barberAboutText"));
                           //Get and set the barbersName
                            barberInfo.setBarberName(barbers.getString("barberName"));
                            //Get and set the barbersUsername
                            barberInfo.setBarberUsername(barbers.getString("barberUserName"));
                            //Get and set the barberObjectId
                            barberInfo.setBarberObjectId(barbers.getObjectId());
                            //Get the GeoPoint for the barbers coordinates
                            ParseGeoPoint barbersLocation = barbers.getParseGeoPoint("barberAddress");
                            //Set the distance from the two locations user and barbers
                            barberInfo.setDistanceFromBarber(barberLookUpCoordinate.distanceInMilesTo(barbersLocation));
                            //Create Markers for barbersNear user
                            LatLng barberLoc = new LatLng(barberInfo.getBarberLocation().latitude,barberInfo.getBarberLocation().longitude);
                            mMap.addMarker(new MarkerOptions().position(barberLoc).title(barberInfo.barberPlaceName).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(barberLoc,10));
                            //Add the barberInfo object to list
                            barberUserInfoList.add(barberInfo);


                        }//End of looping through near barbers
                    barbersListView.setAdapter(new BarberListArrayAdapter(getApplicationContext(),R.layout.barber_list_item, barberUserInfoList));
                    }//End of User check
                     else {
                        Log.d("barberNearTest", "Error: " + e.getMessage());
                    }
                }//End of barbercallback
            });
        }
        else{
            Log.i("UserlocationTest","UserLastLocation is null");
        }


    }

    /*
    *
    * Method called whenever the users location changes
    */
    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        Log.i("onLocationChange","Location moved" + location.getLatitude() + " " + location.getLongitude());
        //Always update the marker in map when user moves location
        LatLng currLocationCoordinates= new LatLng(location.getLatitude(),location.getLongitude());
        // Add a marker in curr location and move camera
        mMap.addMarker(new MarkerOptions().position(currLocationCoordinates).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocationCoordinates,10));
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
    public boolean onTouchEvent(MotionEvent event) {

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
                    Intent rightSwipeViewFollowingIntent = new Intent(getApplicationContext(),ViewFollowingActivity.class);

                    startActivity(rightSwipeViewFollowingIntent);
                }

                // if right to left sweep event on screen
                if (x1 > x2)
                {
                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                    Intent leftSwipeViewFollowingIntent = new Intent(getApplicationContext(),ExploreBarbersPhotosActivity.class);
                    leftSwipeViewFollowingIntent.putExtra("lat", userLastLocation.getLatitude());
                    leftSwipeViewFollowingIntent.putExtra("long", userLastLocation.getLongitude());
                    startActivity(leftSwipeViewFollowingIntent);

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

    public void getUserMessages(){
        //Query the messages database
        ParseQuery<ParseObject> userMessagesQuery = ParseQuery.getQuery("Messages");
        userMessagesQuery.whereEqualTo("receiverUserId", currentUser.getObjectId());
        userMessagesQuery.orderByDescending("createdAt");
        userMessagesQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null ){
                    if (objects.size()>0) {
                        for (ParseObject messages : objects) {
                            //Message content
                            String messageContent = messages.getString("messageContent");
                            //Store the date it was created

                            //Store the sendersUserName
                            String messageSenderUserName = messages.getString("senderUserName");

                            //Store the receiversUserName
                            String messageReceiverUserName = messages.getString("receiverUserName");

                            //Message read or not read
                            boolean messageReadStatus = messages.getBoolean("messageReadOrUnread");

                            //Message ReceiverObjectId
                            String getMessageReceiverUserId = messages.getString("receiverUserId");


                            //Create the message data model to store all possible message data
                            MessagesDataModel messageModel = new MessagesDataModel(messageContent, messageSenderUserName, messageReceiverUserName, messageReadStatus, getMessageReceiverUserId);

                            barberMessagesInfoObjectList.add(messageModel);
                            //Add the messages for currentUser to list
                            barberMessagesObjectsList.add(messages);

                            Toast.makeText(getApplicationContext(), "Got Messages from " + messageSenderUserName + " to " + messageReceiverUserName, Toast.LENGTH_LONG).show();

                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"Error Getting Messages Error Code : " + e.toString(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
