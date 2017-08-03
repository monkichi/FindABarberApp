package com.parse.starter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.starter.findabarberapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chris on 6/7/16.
 */
public class BarberProfileActivity extends AppCompatActivity {

    TextView barberNameTextView;
    TextView barberAboutTextView;
    ImageView barberProfileImageView;

    Button followButton;
    Boolean isFollowing = false;

    //To display the barbers info
    ParseObject barberObject;
    private float x1,x2;
    private  float y1, y2;
     String currentUserId;
     String barberUserId;
    private List<String> isFollowingList;

    private ParseUser currentUser;
    //To get the barbersFollowers List
    private ParseObject barberFollowerObject;
    private List<Object> followersList;
    //List to store barberMessagesReceivedIds
    List<String> barberMessagesUserNamesList;
    //List to store the barberMessageObjects for messagesListView
    List<ParseObject> barberMessagesObjectsList;

    List<ParseFile> barberWorkImagesFileList;
    ArrayList<Bitmap> barberWorkImagesBitmapList;
    private RecyclerView recyclerView;
    private BarberWorkImagesAdapter adapter;    //Adapter for default images
     List<MessagesDataModel> barberMessagesInfoObjectList;
     //ParseFile workImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set view
        setContentView(R.layout.barber_profile_layout);

        //Set up Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("BarberProfile");
        setSupportActionBar(toolbar);

        //Inflate barber Info views
        barberNameTextView = (TextView) findViewById(R.id.barberNameTextView);
        barberAboutTextView =(TextView) findViewById(R.id.aboutBarberTextView);
        barberProfileImageView =(ImageView) findViewById(R.id.barberImageView);

        //Inflate follow button
        followButton =(Button) findViewById(R.id.followUnfollowButton);

        //List for messages
        barberMessagesObjectsList =new ArrayList<ParseObject>();
        barberMessagesInfoObjectList = new ArrayList<MessagesDataModel>();

        //Instantiate array list for barberWorkImages
        barberWorkImagesFileList = new ArrayList<ParseFile>();
        barberWorkImagesBitmapList = new ArrayList<Bitmap>();

        //Store current Log in user for easy access
        currentUser = ParseUser.getCurrentUser();
        //Get the userObjectId for the current user
        currentUserId = currentUser.getObjectId();

        //Get the bundle from barber activity
        Bundle bundle = getIntent().getExtras();
        //Extract the barberObjectId from
        final String barberId = bundle.getString("barberObjectId");

        Log.i("ReceivedId", " "+ barberId);


        //Query the barberObjectID sent from activity to load a barber's info object
        ParseQuery<ParseObject> barberProfileQuery = ParseQuery.getQuery("Barbers");
        barberProfileQuery.getInBackground(barberId, new GetCallback<ParseObject>() {
            public void done(final ParseObject object, ParseException e) {
                if (e == null) {
                    if(object != null){
                        Log.e("profileBarTest","Query was succesful" + object.getString("barberName"));
                        barberObject = object;

                        barberNameTextView.setText(object.getString("barberName") +"\n" +
                        object.getString("barberPlaceName"));
                        barberAboutTextView.setText(object.getString("barberAboutText"));
                        barberUserId = object.getString("barberUserId");
                        //Determine between User or Barber User, We want a user to find out whether we are following the current barber
                        if (!currentUserId.equals(barberUserId)) {

                            if(currentUser.getList("isFollowing").contains(barberUserId)){
                                Log.i("isFollowingTest","The list : " + currentUser.getList("isFollowing").toString()+ "searching barberUserId : " +
                                object.getString("barberUserName"));
                                isFollowing = true;
                                followButton.setText("Unfollow");
                            }
                            else{
                                Log.i("isNotFollowingTest","The list : " + currentUser.getList("isFollowing").toString()+ "searching barberUserId : " +barberUserId);
                                isFollowing = false;
                                followButton.setText("Follow");
                            }

                            //Logic for unfollow/follow button
                            followButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (isFollowing) {
                                        isFollowing = false;

                                        //Remove user from List
                                        isFollowingList= currentUser.getList("isFollowing");

                                        Log.i("unFollowTest", "Before : " + String.valueOf(isFollowingList.size()) );

                                        isFollowingList.remove(barberUserId);

                                        Log.i("unFollowTest", "After" + String.valueOf(isFollowingList.size()) );


                                        Log.i("unFollowTest", "Before : " + String.valueOf(followersList.size()) );

                                        followersList = barberFollowerObject.getList("followersList");
                                        followersList.remove(currentUserId);
                                        Log.i("unFollowTest", "After" + String.valueOf(followersList.size()) );

                                        currentUser.put("isFollowing",isFollowingList);
                                        barberFollowerObject.put("followersList",followersList);
                                        Log.i("followersList", "List data" + " " + currentUser.getList("isFollowing").toString() + " " + String.valueOf(currentUser.getList("isFollowing").size()));
                                        currentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null){
                                                    barberFollowerObject.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if(e == null){

                                                            }
                                                        }
                                                    });
                                                    followButton.setText("Follow");
                                                }
                                            }
                                        });

                                    } else {
                                        followButton.setText("Unfollow");
                                        isFollowing = true;
                                        //Add the usersObjectId to the user isfollowing list
                                        isFollowingList= currentUser.getList("isFollowing");
                                        followersList = barberFollowerObject.getList("followersList");

                                        isFollowingList.add(barberUserId);
                                        followersList.add(currentUserId);
                                        currentUser.put("isFollowing",isFollowingList);
                                        barberFollowerObject.put("followersList", followersList);
                                        currentUser.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {
                                                    Log.i("isFollowingTest", "List was Saved with " + barberUserId  + " data " + currentUser.getList("isFollowing").toString());
                                                } else {
                                                    Log.i("isFollowingTest", "List could not be saved" + e.getMessage());
                                                }
                                            }
                                        });
                                        barberFollowerObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if (e == null) {

                                                } else {
                                                    Log.i("FollowersListTest", "List could not be saved" + e.getMessage());
                                                }
                                            }
                                        });


                                    }
                                }//End of oncreateMethod
                            });
                        }
                        else{
                            followButton.setVisibility(View.INVISIBLE);
                        }

                        getBarberFollewersFromDB();

                        getBarberImagesFromDB();

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

        //Set up Grid view of the barbers work Images
        recyclerView = (RecyclerView) findViewById(R.id.barber_work_images_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(BarberProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
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
                            Toast.makeText(BarberProfileActivity.this, "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();

                            if (currentUser.getString("barberOrUser").equals("barber")) {
                                Intent leftSwipeBarberActivityIntent = new Intent(getApplicationContext(),BarberMainActivity.class);
                                startActivity(leftSwipeBarberActivityIntent);
                            }
                            else{
                                Intent leftSwipeBarberActivityIntent = new Intent(getApplicationContext(),UserMapsActivity.class);
                                startActivity(leftSwipeBarberActivityIntent);
                            }


                        }

                        // if right to left sweep event on screen
                        if (x1 > x2)
                        {

                            Toast.makeText(BarberProfileActivity.this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();

                        }

                        // if UP to Down sweep event on screen
                        if (y1 < y2)
                        {
                            Toast.makeText(BarberProfileActivity.this, "UP to Down Swap Performed", Toast.LENGTH_SHORT).show();
                        }

                        //if Down to UP sweep event on screen
                        if (y1 > y2)
                        {
                            Toast.makeText(BarberProfileActivity.this, "Down to UP Swap Performed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                return false;
            }
        });

        //set adapter for barber Work images gridview
        adapter = new BarberWorkImagesAdapter(BarberProfileActivity.this, barberWorkImagesBitmapList);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview

    }

    private void getBarberImagesFromDB() {
        //Query all the photos
        ParseQuery<ParseObject> barberProfileImagesQuery = ParseQuery.getQuery("Images");
        barberProfileImagesQuery.whereEqualTo("UserObjectId",barberUserId);
        barberProfileImagesQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    try {
                        ParseFile profileImageFile = object.getParseFile("ProfileImageFile");
                        byte[] profileImageByteArray = new byte[0];
                        if (profileImageFile != null) {
                            profileImageByteArray = profileImageFile.getData();
                            barberProfileImageView.setImageBitmap(BitmapFactory.decodeByteArray(profileImageByteArray, 0, profileImageByteArray.length));
                        }
                        //Get BarbersWorkImages from database
                        barberWorkImagesFileList = object.getList("ImagesFileList");
                        Log.i("barberImagesTest", "Query for barber images was sucessful: " + barberWorkImagesFileList.size() );

                        if (barberWorkImagesFileList!= null) {
                            if (barberWorkImagesFileList.size() > 0 ){
                                if (barberWorkImagesFileList.get(0)!= null){

                                    for (ParseFile workImage : barberWorkImagesFileList) {
                                            byte[] barberWorkImageByteArray = new byte[0];
                                            if (workImage != null) {
                                                barberWorkImageByteArray = workImage.getData();
                                                barberWorkImagesBitmapList.add(BitmapFactory.decodeByteArray(barberWorkImageByteArray, 0, barberWorkImageByteArray.length));
                                                adapter = new BarberWorkImagesAdapter(BarberProfileActivity.this, barberWorkImagesBitmapList);
                                                recyclerView.setAdapter(adapter);// set adapter on recyclerview
                                            }

                                        }

                                        adapter.notifyDataSetChanged();

                                }

                            }

                        }
                        else{

                        }

                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.i("barberImagesTest", "There was an error getting barberImages with "+ barberUserId+" " + e.getMessage());
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if(itemId == R.id.logOut_action) {
            logOut();
        }

        else if (itemId == R.id.check_messages_action) {
            //Create the Dialog to view the messages
            Log.i("checkMessagesCheck","Check messages icon has been pressed");


            AlertDialog.Builder viewMessagesBuilder = new AlertDialog.Builder(BarberProfileActivity.this);
            viewMessagesBuilder.setTitle("View Messages");

            final MessagesListAdapter arrayAdapter = new MessagesListAdapter(
                    BarberProfileActivity.this
                    ,barberMessagesInfoObjectList,R.layout.messages_item_row_layout);


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
//                    AlertDialog.Builder builderInnerInnerSendMessage = new AlertDialog.Builder(BarberProfileActivity.this);
//                    //Add senders userNames
//                    builderInnerInnerSendMessage.setTitle("Send Message to:" + barberObject.getString("barberUserName"));
//                    final EditText replyMessageEditText = new EditText(BarberProfileActivity.this);
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

            viewMessagesBuilder.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlertDialog.Builder builderInnerMessageView = new AlertDialog.Builder(
                                    BarberProfileActivity.this);
                            //Update that message has been read
                            barberMessagesObjectsList.get(which).put("messageReadOrUnread", true);
                            builderInnerMessageView.setTitle("Your message from: "+ barberMessagesInfoObjectList.get(which).getMessageSenderUserName());
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
                                            //Create Alter view for replying to message being currently read
                                            AlertDialog.Builder builderInnerInnerReplyMessage = new AlertDialog.Builder(BarberProfileActivity.this);
                                            //Add senders userName
                                            builderInnerInnerReplyMessage.setTitle("Reply to: " + barberMessagesInfoObjectList.get(which + 1).getMessageSenderUserName());
                                            final EditText replyMessageEditText = new EditText(BarberProfileActivity.this);
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
                                                    newMessageObject.put("messageReadOrUnread",false);
                                                    newMessageObject.put("messageContent",replyMessage);

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

    private void getBarberFollewersFromDB(){
       //Queru the barbers Follers
        ParseQuery<ParseObject> barberFollowersQuery = ParseQuery.getQuery("Followers");
        barberFollowersQuery.whereEqualTo("followerUserId",barberUserId);
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

