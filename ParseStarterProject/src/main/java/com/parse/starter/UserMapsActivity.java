package com.parse.starter;

import android.Manifest;
import android.content.Context;
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
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
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
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps);
        Toolbar actionToolbar = (Toolbar) findViewById(R.id.my_toolbar);
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

        //Get the last know users location
        userLastLocation = mLocationManager.getLastKnownLocation(provider);
        if (userLastLocation == null) {
            mLocationManager.requestLocationUpdates(provider, 400, 1, this);
            //Get the last know users location
            userLastLocation = mLocationManager.getLastKnownLocation(provider);
        }

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
        getMenuInflater().inflate(R.menu.menu_main,menu);
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

            ParseGeoPoint barberLookUpCoordinate = new ParseGeoPoint(userLastLocation.getLatitude(),userLastLocation.getLongitude());
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

}
