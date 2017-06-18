package com.parse.starter;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class ViewFollowersActivity extends AppCompatActivity {

    //Create List of followerUserId
    List<String> followerUserIdList;
    //Create List to store the Follower info objects made
    List<FollowerInfo> followerInfoList;
    //Create the listview
    ListView followersListView;
    TextView followersTextView;
    ParseUser currentUser;
    private int i;
    RelativeLayout viewFollowersActivityLayout;
    private float x2;
    private float y1;
    private float y2;
    private float x1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_followers);

        //Inflate the widgets
        followerUserIdList = new ArrayList<String>();
        followerInfoList = new ArrayList<FollowerInfo>();
        followersTextView = (TextView) findViewById(R.id.followingYouTextView);
        followersListView = (ListView) findViewById(R.id.followersListView);

        currentUser = ParseUser.getCurrentUser();
        getFollowers();

        viewFollowersActivityLayout = (RelativeLayout) findViewById(R.id.viewFollowersActivityLayout);
        viewFollowersActivityLayout.setOnTouchListener(new View.OnTouchListener() {
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
                                Intent rightSwipeBarberSwipeIntent = new Intent(getApplicationContext(),BarberActivity.class);
                                startActivity(rightSwipeBarberSwipeIntent);
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
            }
        });

    }
    public void getFollowers(){
        //Query all the users
        ParseQuery<ParseObject> barberFollowers = ParseQuery.getQuery("Followers");
        barberFollowers.whereEqualTo("followerUserId",currentUser.getObjectId());
        barberFollowers.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    Log.i("followersTest","Followers Query was sucessful");
                    followerUserIdList = object.getList("followersList");
                    for(i = 0; i<followerUserIdList.size(); i++){
                        final ParseQuery<ParseUser> followerUserObject = ParseUser.getQuery();
                        followerUserObject.getInBackground(followerUserIdList.get(i), new GetCallback<ParseUser>() {
                            @Override
                            public void done(ParseUser object, ParseException e) {
                                if(e==null){
                                    Log.i("followerUser","Query for followerUser was successful");
                                    //Create Follower Info Object
                                    FollowerInfo followerInfo = new FollowerInfo();
                                    followerInfo.setFollowerBarberOrUser(object.getString("barberOrUser"));
                                    followerInfo.setFollowerUserName(object.getString("name"));
                                    ParseQuery<ParseObject> barberImage = ParseQuery.getQuery("Images");
                                    barberImage.whereEqualTo("UserObjectId",followerUserIdList.get(i -1));
                                    try {
                                        ParseObject userImageObject = barberImage.getFirst();
                                        ParseFile profileImageFile = userImageObject.getParseFile("ProfileImageFile");
                                        byte [] profileImageByteArray= new byte[0];
                                        if (profileImageFile != null){
                                            profileImageByteArray = profileImageFile.getData();
                                            followerInfo.setFollowerProfileImage(BitmapFactory.decodeByteArray(profileImageByteArray,0,profileImageByteArray.length));
                                        }
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    followerInfoList.add(followerInfo);
                                    Log.i("followerTest", "Got a match!");
                                   if (i  == followerUserIdList.size()  ){
                                        followersListView.setAdapter(new FollowerListArrayAdapter(getApplicationContext(),R.layout.follower_list_item,followerInfoList));
                                        Log.i("followerTest","Adapter was set with list objects " + followerInfoList.size());

                                   }
                                    else{
                                        Log.i("followerTest", "Coulndt set listAdapter " + String.valueOf(i)+" " + followerInfoList.size());
                                    }
                                }
                                else{
                                    Log.i("followerUser","There was an error getting the followerUser "
                                            + e.getMessage());
                                }
                            }
                        });
                    }

                }
                else{
                    Log.i("followersTest", "There was an error trying to get followers "
                            + e.getMessage());
                }
            }
        });

    }
}
