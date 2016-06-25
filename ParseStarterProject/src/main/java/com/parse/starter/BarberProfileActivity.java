package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 6/7/16.
 */
public class BarberProfileActivity extends AppCompatActivity {

    TextView barberNameTextView;
    TextView barberAboutTextView;
    Button followButton;
    Boolean isFollowing = false;
    //To display the barbers info
    ParseObject barberObject;
    //Want to check if current user is following the barbersUserID
    ParseUser barberUserObject;

    private float x1,x2;
    private  float y1, y2;
     String currentUserId;
     String barberUserId;
    private List<String> isFollowingList;
    private ParseUser currentUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barber_profile_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        barberNameTextView = (TextView) findViewById(R.id.barberNameTextView);
        barberAboutTextView =(TextView) findViewById(R.id.aboutBarberTextView);
         currentUser = ParseUser.getCurrentUser();
        //Get the userObjectId for the current user
        currentUserId = currentUser.getObjectId();
        //Get the bundle
        Bundle bundle = getIntent().getExtras();
        //Extract the data
        final String barberId = bundle.getString("barberObjectId");

        Log.i("ReceivedId", " "+ barberId);
        followButton =(Button) findViewById(R.id.followUnfollowButton);



        ParseQuery<ParseObject> barberProfileQuery = ParseQuery.getQuery("Barbers");
        barberProfileQuery.getInBackground(barberId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if(object != null){
                        Log.e("profileBarTest","Query was succesful" + object.getString("barberName"));
                       barberObject = object;
                        barberNameTextView.setText(object.getString("barberName") +"\n" +
                        object.getString("barberPlaceName"));
                        barberAboutTextView.setText(object.getString("barberAboutText"));
                        barberUserId = object.getString("barberUserId");
                        if(currentUser.getList("isFollowing").contains(barberUserId)){
                            Log.i("isFollowingTest","The list : " + currentUser.getList("isFollowing").toString()+ "searching barberUserId : " +barberUserId);
                            isFollowing = true;
                            followButton.setText("Unfollow");
                        }
                        else{
                            Log.i("isFollowingTest","The list : " + currentUser.getList("isFollowing").toString()+ "searching barberUserId : " +barberUserId);
                            isFollowing = false;
                            followButton.setText("Follow");
                        }

                    }
                    else{
                        Log.e("profileBarTest","Object is null");
                    }
                } else {
                    // something went wrong
                    Log.e("profileBarTest", "Query was not succesful" + e.getMessage());
                }
            }
        });



        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFollowing) {
                    isFollowing = false;

                    //Remove user from List
                    isFollowingList= currentUser.getList("isFollowing");
                    isFollowingList.remove(barberUserId);
                    currentUser.put("isFollowing",isFollowingList);
                    Log.i("followersList", "List data" + " " + currentUser.getList("isFollowing").toString() + " " + String.valueOf(currentUser.getList("isFollowing").size()));
                    currentUser.saveInBackground();
                    followButton.setText("Follow");
                } else {
                    followButton.setText("Unfollow");
                    isFollowing = true;
                    //Add the usersObjectId to the user isfollowing list
                    isFollowingList= currentUser.getList("isFollowing");
                    isFollowingList.add(barberUserId);
                    currentUser.put("isFollowing",isFollowingList);

                    currentUser.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("isFollowingTest", "List was Saved with " + barberUserId + "list size " + currentUser.getList("isFollowing").size() + " data " + currentUser.getList("isFollowing").toString());
                            } else {
                                Log.i("isFollowingTest", "List could not be saved" + e.getMessage());
                            }
                        }
                    });


                }
            }//End of oncreateMethod
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logOut_action){
         logOut();
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

                }

                // if right to left sweep event on screen
                if (x1 > x2)
                {
                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                    Intent leftSwipeBarberActivityIntent = new Intent(getApplicationContext(),BarberActivity.class);
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




}

