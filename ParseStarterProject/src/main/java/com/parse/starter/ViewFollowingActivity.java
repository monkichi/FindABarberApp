package com.parse.starter;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.starter.findabarberapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 6/9/16.
 */
public class ViewFollowingActivity extends AppCompatActivity {

    ListView followersListView;
    ArrayList<FollowerInfo> followersList;
    int i;
    List<String> isFollowingList;
   RelativeLayout viewFollowersLayout;
    private float y1;
    private float x1;
    private float x2;
    private float y2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_following_layout);
        Log.i("followerInfo","Opened ViewFollowers");
        followersListView = (ListView) findViewById(R.id.barberFollowersListView);
        followersList = new ArrayList<FollowerInfo>();
       viewFollowersLayout = (RelativeLayout) findViewById(R.id.followingActivityLayout);
        getFollowers();
        viewFollowersLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (MotionEventCompat.getActionMasked(event))
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
                            Toast.makeText(getApplicationContext(), "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();


                        }

                        // if right to left sweep event on screen
                        if (x1 > x2)
                        {
                            Toast.makeText(getApplicationContext(), "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                            if (ParseUser.getCurrentUser().getString("barberOrUser").equals("barber")) {
                                Intent rightSwipeBarberSwipeIntent = new Intent(getApplicationContext(),BarberMainActivity.class);
                                startActivity(rightSwipeBarberSwipeIntent);
                            }
                            else{
                                Intent rightSwipeBarberProfileIntent = new Intent(getApplicationContext(),UserMapsActivity.class);
                                startActivity(rightSwipeBarberProfileIntent);
                            }

                        }

                        // if UP to Down sweep event on screen
                        if (y1 < y2)
                        {
                            Toast.makeText(getApplicationContext(), "UP to Down Swap Performed", Toast.LENGTH_SHORT).show();
                        }

                        //if Down to UP sweep event on screen
                        if (y1 > y2)
                        {
                            Toast.makeText(getApplicationContext(), "Down to UP Swap Performed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }

                }
                return true;
                //Wasnt working
            }
        });


        followersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (followersList.get(position).followerBarberOrUser.equals("barber")){
                    //Query the Barber's barberUserID with the selected items UserId
                    ParseQuery<ParseObject> barberUserIdQuery = ParseQuery.getQuery("Barbers");
                    barberUserIdQuery.whereEqualTo("barberUserId", followersList.get(position).getFollowerUserId());
                    barberUserIdQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject object, ParseException e) {
                            if(e== null && object!= null){
                                Log.i("BarberIdQueryCheck","Query succesfull got barberId "+ object.getObjectId());
                                Intent viewBarberProfileIntent = new Intent(getApplicationContext(),BarberProfileActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("barberObjectId",object.getObjectId());
                                viewBarberProfileIntent.putExtras(bundle);
                                startActivity(viewBarberProfileIntent);

                            }
                            else{
                                Log.i("BarberIdQuery", "Query was not succesful" + e.getMessage());
                            }
                        }
                    });

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void getFollowers() {
        //Get the currentUsers isFollowing List
        isFollowingList = ParseUser.getCurrentUser().getList("isFollowing");
        if (isFollowingList.size() == 0) {
            Log.i("followerTest", ParseUser.getCurrentUser().getUsername() + " is not following anybody");

        } else {
            Log.i("followerTest", "This is the user's:" +ParseUser.getCurrentUser().getUsername()+" following list " + isFollowingList.toString());
            for ( i =0 ;i< isFollowingList.size(); i++) {
                ParseQuery<ParseUser> query = ParseUser.getQuery();
                Log.i("followerTest", isFollowingList.get(i));
                        query.getInBackground(isFollowingList.get(i), new GetCallback<ParseUser>() {
                    public void done(ParseUser user, ParseException e) {
                        if (e == null) {
                            // The query was successful.
                            // check if we got a match
                            if (user == null) {
                                // no matching user!
                                Log.i("followerTest","No matching user");
                            } else {
                                // great, get the name etc
                               FollowerInfo followerInfo = new FollowerInfo(user.getObjectId(),user.getUsername(),user.getString("barberOrUser"));
                                ParseQuery<ParseObject> profileImageQuery = ParseQuery.getQuery("Images");
                                Log.i("profileImageTest","Size of array " + isFollowingList.size() + "pointer" + i);
                                profileImageQuery.whereEqualTo("UserObjectId",isFollowingList.get(i - 1));
                                try {
                                    ParseObject userImageObject = profileImageQuery.getFirst();
                                    ParseFile profileImageFile = userImageObject.getParseFile("ProfileImageFile");
                                    byte [] profileImageByteArray= new byte[0];
                                    if (profileImageFile != null){
                                        profileImageByteArray = profileImageFile.getData();
                                        followerInfo.setFollowerProfileImage(BitmapFactory.decodeByteArray(profileImageByteArray,0,profileImageByteArray.length));
                                    }
                                } catch (ParseException e1) {
                                    e1.printStackTrace();
                                }
                                followersList.add(followerInfo);
                                Log.i("followerTest", "Got a match!");
                                if (i  == isFollowingList.size()  ){
                                    followersListView.setAdapter(new FollowerListArrayAdapter(getApplicationContext(),R.layout.follower_list_item,followersList));
                                    Log.i("followerTest","Adapter was set with list objects " + followersList.size());

                                }
                                else{
                                    Log.i("followerTest", "Coulndt set listAdapter " + String.valueOf(i)+" " + isFollowingList.size());
                                }
                            }
                        } else {
                            // Something went wrong.
                            Log.i("followerTest", " Something went wrong " + e.getMessage());
                        }
                    }
                });
            }//End of isfollowing list iterator

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


}
