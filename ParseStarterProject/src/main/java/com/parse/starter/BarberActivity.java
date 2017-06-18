package com.parse.starter;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.server.converter.StringToIntConverter;
import com.google.android.gms.maps.model.LatLng;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by chris on 6/7/16.
 *
 * This activity allows the barber to edit the
 * information that eventually goes into the barber profile
 * activity
 */
public class BarberActivity extends AppCompatActivity implements View.OnClickListener {
    int REQUEST_PHOTO_ALBUM= 1;
    ImageView barberProfileImage;
    ImageView  barberImageView1,barberImageView2,barberImageView3,barberImageView4,barberImageView5,barberImageView6;
    EditText barberAddressEditText;
    EditText barberPlaceNameEditText;
    EditText barberAboutYouEditText;
    Button updateBarberProButton;
    ParseUser currentUser;
    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    String googleGeocodingURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String key= R.string.google_maps_key +"\n";
    private RelativeLayout relativeLayout;
    private float x1,x2;
    private float y1, y2;
    private boolean barberProfileImageisClicked=false;
    private boolean barberWorkImage1isClicked =false;
    private boolean barberWorkImage2isClicked=false;
    private boolean barberWorkImage3isClicked=false;
    private boolean barberWorkImage4isClicked=false;
    private boolean barberWorkImage5isClicked=false;
    private boolean barberWorkImage6isClicked=false;
     String mProfilePhotoPath = "";
     String photoPathReturned= "";
     String absolutePath="";
     ParseObject barberObject;
     ParseObject currentUserImageObject;
    List<ParseObject> messagesReceivedList;
     List<String> messagesReceiverUserNameList;
     ArrayAdapter<String> arrayAdapter;

    //DownLoadBitmap

    public void getCurrentBarber() {

    ParseQuery<ParseObject> query = ParseQuery.getQuery("Barbers");
    query.whereEqualTo("barberUserId",currentUser.getObjectId());
    query.findInBackground(new FindCallback<ParseObject>() {
        @Override
        public void done(List<ParseObject> objects, ParseException e) {
            if (e == null) {
                if(objects != null && objects.size() > 0){
                    barberObject = objects.get(0);
                    Log.i("BarberQuery","Query was succesful with object id "+ currentUser.getObjectId() +" object " + barberObject.get("barberUserId"));
                    //Query all the messages for the currentUserId which should be a barber
                    ParseQuery<ParseObject> getMessagesObject = new ParseQuery<ParseObject>("Messages");
                    getMessagesObject.whereEqualTo("receiverUserId",currentUser.getObjectId());
                    getMessagesObject.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> objects, ParseException e) {
                            if(e==null){
                                if (objects.size() > 0){
                                    Log.i("messagesQueryTest", "Query was succesful");
                                    for (ParseObject messages: objects){
                                        Log.i("messages", "Message from" + messages.getString("senderUserName")+ " " + "\n"+
                                        "message content is " + messages.getString("messageContent"));
                                        messagesReceivedList.add(messages);
                                        messagesReceiverUserNameList.add(messages.getString("senderUserName"));
                                        
                                    }
                                }
                                else{
                                    Log.i("messagesQueryTest","Query has no result " + objects.size());
                                }

                            }
                            else{
                                Log.i("messagesQueryTest", "Error getting messages query " + e.getMessage());

                            }
                        }
                    });
                }
            }//End of if
            else {
                // something went wrong
                Log.i("BarberQuery","Query not succesful "+ e.getMessage() );
            }
        }
    });
}
    public byte[] getImageViewByteArray(BitmapDrawable prof){
        // Convert it to Bitmap
        Bitmap profileImagBitmap = prof.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        profileImagBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        return image;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barber_layout);
        //Set up toolabar
        Toolbar actionToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(actionToolbar);
       //Set up keyboard management
        relativeLayout = (RelativeLayout) findViewById(R.id.barberRelativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dismiss the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        });
         messagesReceivedList = new ArrayList<ParseObject>();
        messagesReceiverUserNameList=new ArrayList<String>();
        //Store the currentUSer Object
        currentUser = ParseUser.getCurrentUser();
        //Get the currentUserBarbers info
        getCurrentBarber();


        barberProfileImage =(ImageView) findViewById(R.id.barberProfileImage);
        if (barberProfileImage != null) {
            barberProfileImage.setOnClickListener(this);
        }
        barberImageView1 = (ImageView) findViewById(R.id.barberImageView1);
        if (barberImageView1 != null) {
            barberImageView1.setOnClickListener(this);
        }
        barberImageView2=(ImageView) findViewById(R.id.barberImageView2);
        if (barberImageView2 != null) {
            barberImageView2.setOnClickListener(this);
        }
        barberImageView3=(ImageView) findViewById(R.id.barberImageView3);
        if (barberImageView3 != null) {
            barberImageView3.setOnClickListener(this);
        }
        barberImageView4 =(ImageView) findViewById(R.id.barberImageView4);
        if (barberImageView4 != null) {
            barberImageView4.setOnClickListener(this);
        }
        barberImageView5=(ImageView) findViewById(R.id.barberImageView5);
        if (barberImageView5 != null) {
            barberImageView5.setOnClickListener(this);
        }
        barberImageView6=(ImageView) findViewById(R.id.barberImageView6);
        if (barberImageView6 != null) {
            barberImageView6.setOnClickListener(this);
        }
        ParseQuery<ParseObject> imagesDownLoadQuery = ParseQuery.getQuery("Images");
        imagesDownLoadQuery.whereEqualTo("UserObjectId", currentUser.getObjectId());
        imagesDownLoadQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    currentUserImageObject = object;
                    Log.i("ImagesQuerySuccess","Got a result " + currentUserImageObject.get("UserObjectId") );
                    try {
                        ParseFile profileImageFile = object.getParseFile("ProfileImageFile");
                        byte [] profileImageByteArray= new byte[0];
                        if (profileImageFile != null){
                            profileImageByteArray = profileImageFile.getData();
                            barberProfileImage.setImageBitmap(BitmapFactory.decodeByteArray(profileImageByteArray,0,profileImageByteArray.length));
                        }
                        ParseFile imageView1File = object.getParseFile("ImageView1File");
                        byte [] imageView1ByteArray = new byte[0];
                        if (imageView1File != null){
                            imageView1ByteArray = imageView1File.getData();
                            barberImageView1.setImageBitmap(BitmapFactory.decodeByteArray(imageView1ByteArray,0,imageView1ByteArray.length));
                        }
                        ParseFile imageView2File = object.getParseFile("ImageView2File");
                        byte [] imageView2ByteArray = new byte[0];
                        if (imageView2File != null){
                            imageView2ByteArray = imageView2File.getData();
                            barberImageView2.setImageBitmap(BitmapFactory.decodeByteArray(imageView2ByteArray,0,imageView2ByteArray.length));
                        }
                        ParseFile imageView3File = object.getParseFile("ImageView3File");
                        byte [] imageView3ByteArray = new byte[0];
                        if (imageView3File != null){
                            imageView3ByteArray = imageView3File.getData();
                            barberImageView3.setImageBitmap(BitmapFactory.decodeByteArray(imageView3ByteArray,0,imageView3ByteArray.length));
                        }
                        ParseFile imageView4File = object.getParseFile("ImageView4File");
                        byte [] imageView4ByteArray = new byte[0];
                        if (imageView4File != null){
                            imageView4ByteArray = imageView4File.getData();
                            barberImageView4.setImageBitmap(BitmapFactory.decodeByteArray(imageView4ByteArray,0,imageView4ByteArray.length));
                        }
                        ParseFile imageView5File = object.getParseFile("ImageView5File");
                        byte [] imageView5ByteArray = new byte[0];
                        if (imageView5File != null){
                            imageView5ByteArray = imageView5File.getData();
                            barberImageView5.setImageBitmap(BitmapFactory.decodeByteArray(imageView5ByteArray,0,imageView5ByteArray.length));
                        }
                        ParseFile imageView6File = object.getParseFile("ImageView6File");
                        byte [] imageView6ByteArray = new byte[0];
                        if (imageView1File != null){
                            imageView6ByteArray = imageView6File.getData();
                            barberImageView6.setImageBitmap(BitmapFactory.decodeByteArray(imageView6ByteArray,0,imageView6ByteArray.length));
                        }

                    }//End of try
                    catch (ParseException e1){
                        Log.i("imageUploadTest", "Error occured trying to query imageobject " + e1.getMessage());
                    }
                }//End of query exception check
                else{
                    Log.i("ImagesQuerySuccess","Error occured " + e.getMessage());
                }
            }//End of ParseQuery Result Method
        });

        barberAddressEditText = (EditText) findViewById(R.id.barberAddressEditText);
         barberPlaceNameEditText = (EditText) findViewById(R.id.barberPlaceNameEditText);
         barberAboutYouEditText = (EditText) findViewById(R.id.barberAboutYouEditText);
         updateBarberProButton = (Button) findViewById(R.id.updateBarberProButton);

        //Set Up Listener for button click
        updateBarberProButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We want to get all the users info and store in database
                if (barberProfileImage != null && barberAddressEditText.getText().length()>0 && barberPlaceNameEditText.getText().length()>0 &&
                        barberAboutYouEditText.getText().length()>0){
                    //Load the info into the database
                    if (currentUser ==  null){
                        Log.e("FindUserinBarberAct","Current user is null");
                    }
                    else{
                       //LatLng address = getLocationFromAddress(getApplicationContext(),barberAddressEditText.getText().toString());
                        DownloadTask task = new DownloadTask();
                        //Get the address string and parse
                        String addressString = barberAddressEditText.getText().toString();
                        String [] addressStringArray = addressString.split(" ");

                        String addressStreetNumber = addressStringArray[0];
                        String addressStreetName = addressStringArray[1] + "+"+ addressStringArray[2] +",";
                        String addressStreetCity = addressStringArray[3];
                        String addressStreetState = addressStringArray[4];

                        try {
                           LatLng address = task.execute(googleGeocodingURL + addressStreetNumber + "+" +addressStreetName + "+" + addressStreetCity + addressStreetState).get();
                            if (address!= null){
                                barberObject.put("barberAddress", new ParseGeoPoint(address.latitude,address.longitude));
                                barberObject.put("barberAboutText", barberAboutYouEditText.getText().toString());
                                barberObject.put("barberPlaceName",barberPlaceNameEditText.getText().toString());
                                //Get image drawable from profileImageview
                                BitmapDrawable profileImageBitMapdrawable = ((BitmapDrawable)barberProfileImage.getDrawable());
                                // Create the ParseFile
                                ParseFile profileImageFile = new ParseFile("profileImage.png", getImageViewByteArray(profileImageBitMapdrawable));
                                // Upload the image into Parse Cloud
                                profileImageFile.saveInBackground();

                                //Create the parsefile for imageview1
                                BitmapDrawable imageView1BitMapDrawable = ((BitmapDrawable) barberImageView1.getDrawable());
                                ParseFile imageView1File = new ParseFile("imageView1.png",getImageViewByteArray(imageView1BitMapDrawable));
                                imageView1File.saveInBackground();

                                //
                                BitmapDrawable imageView2BitMapDrawable = ((BitmapDrawable) barberImageView2.getDrawable());
                                ParseFile imageView2File = new ParseFile("imageView2.png",getImageViewByteArray(imageView2BitMapDrawable));
                                imageView2File.saveInBackground();

                                BitmapDrawable imageView3BitMapDrawable = ((BitmapDrawable) barberImageView3.getDrawable());
                                ParseFile imageView3File = new ParseFile("imageView3.png",getImageViewByteArray(imageView3BitMapDrawable));
                                imageView3File.saveInBackground();

                                BitmapDrawable imageView4BitMapDrawable = ((BitmapDrawable) barberImageView4.getDrawable());
                                ParseFile imageView4File = new ParseFile("imageView4.png",getImageViewByteArray(imageView4BitMapDrawable));
                                imageView4File.saveInBackground();

                                BitmapDrawable imageView5BitMapDrawable = ((BitmapDrawable) barberImageView5.getDrawable());
                                ParseFile imageView5File = new ParseFile("imageView5.png",getImageViewByteArray(imageView5BitMapDrawable));
                                imageView5File.saveInBackground();

                                BitmapDrawable imageView6BitMapDrawable = ((BitmapDrawable) barberImageView6.getDrawable());
                                ParseFile imageView6File = new ParseFile("imageView6.png",getImageViewByteArray(imageView6BitMapDrawable));
                                imageView6File.saveInBackground();

                                        // Create a column named "ImageFile" and insert the image
                                        currentUserImageObject.put("ProfileImageFile", profileImageFile);
                                        //Create the colum name imagView1File
                                        currentUserImageObject.put("ImageView1File",imageView1File);

                                        //Create the colum name imagView2File
                                        currentUserImageObject.put("ImageView2File",imageView2File);

                                        //Create the colum name imagView1File
                                        currentUserImageObject.put("ImageView3File",imageView3File);

                                        //Create the colum name imagView1File
                                        currentUserImageObject.put("ImageView4File",imageView4File);

                                        //Create the colum name imagView1File
                                        currentUserImageObject.put("ImageView5File",imageView5File);

                                        //Create the colum name imagView1File
                                        currentUserImageObject.put("ImageView6File",imageView6File);
                                        // Create the class and the columns
                                        currentUserImageObject.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                if(e==null){
                                                    Log.i("barberLoadingCheck", "All the barbers data was succcesfully update");
                                                }
                                                else{
                                                    Log.i("barLoadingCheck", "There was an error: " + e.getMessage());
                                                }
                                            }
                                        });

                                        // Show a simple toast message
                                        Toast.makeText(getApplicationContext(), "Images Uploaded",
                                                Toast.LENGTH_SHORT).show();

                                barberObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null){
                                            Log.i("BarberProfileTest", "Data has been save for user " + currentUser.getUsername() );
                                            Toast.makeText(getApplicationContext(),"Profile has been update" + " " + currentUser.getUsername(),Toast.LENGTH_SHORT).show();
                                        }else{
                                            Log.i("BarberProfileTest", "Data was not able to save " + e.getMessage() );

                                        }
                                    }
                                });



                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Address returned null", Toast.LENGTH_LONG).show();
                            }
                            //End of Null Address check
                        } //End of Try catch
                        catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please fill out all the information", Toast.LENGTH_SHORT).show();
                }
            }
        });
        relativeLayout.setOnTouchListener(new View.OnTouchListener() {
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
                            Toast.makeText(BarberActivity.this, "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();
                            Intent leftSwipeViewFollowingIntent = new Intent(getApplicationContext(), ViewFollowersActivity.class);
                            startActivity(leftSwipeViewFollowingIntent);

                        }

                        // if right to left sweep event on screen
                        if (x1 > x2)
                        {
                            Toast.makeText(BarberActivity.this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                            Intent rightSwipeBarberProfileIntent = new Intent(getApplicationContext(),BarberProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("barberObjectId",barberObject.getObjectId());
                            rightSwipeBarberProfileIntent.putExtras(bundle);
                            startActivity(rightSwipeBarberProfileIntent);
                        }

                        // if UP to Down sweep event on screen
                        if (y1 < y2)
                        {
                            Toast.makeText(BarberActivity.this, "UP to Down Swap Performed", Toast.LENGTH_SHORT).show();
                        }

                        //if Down to UP sweep event on screen
                        if (y1 > y2)
                        {
                            Toast.makeText(BarberActivity.this, "Down to UP Swap Performed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                return true;
            }
        });

    }//End of onCreate




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.logOut_action){
                logOut();
        }
        else if (item.getItemId() == R.id.check_messages_action) {
            //Create the Dialog to view the messages
            Log.i("checkMessagesCheck","Check messages icon has been pressed");

            //Create first layer of dialog
            AlertDialog.Builder viewMessagesBuilder = new AlertDialog.Builder(BarberActivity.this);
            viewMessagesBuilder.setTitle("Messages");

            arrayAdapter = new ArrayAdapter<String>(
                    BarberActivity.this,
                    android.R.layout.select_dialog_singlechoice, messagesReceiverUserNameList);
        

            viewMessagesBuilder.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            viewMessagesBuilder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String originalMessaSenderUserName = arrayAdapter.getItem(which);
                            AlertDialog.Builder builderInnerViewMessage = new AlertDialog.Builder(
                                    BarberActivity.this);
                            //Get access to the message object and update that it has been saved
                            messagesReceivedList.get(which).put("messageReadOrUnread",true);
                            messagesReceivedList.get(which).saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null){
                                        Log.i("readUpdate","Message has succesfully update to read");
                                    }
                                    else{
                                        Log.i("readUpdate","Coulnt update message to read " + e.getMessage() );
                                    }
                                }
                            });
                            builderInnerViewMessage.setTitle("Your message from:" + originalMessaSenderUserName);
                            builderInnerViewMessage.setMessage(messagesReceivedList.get(which).getString("messageContent"));
                            //Need to get the senders userName
                            builderInnerViewMessage.setPositiveButton("reply", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.i("messageClicked","position: "+ which);
                                            //Create Alter view fro replying to message
                                            AlertDialog.Builder builderInnerInnerReplyMessage = new AlertDialog.Builder(BarberActivity.this);
                                            //Add senders userName
                                            builderInnerInnerReplyMessage.setTitle("Reply to:" + originalMessaSenderUserName);
                                            final EditText replyMessageEditText = new EditText(BarberActivity.this);
                                            builderInnerInnerReplyMessage.setView(replyMessageEditText);
                                            builderInnerInnerReplyMessage.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, final int which) {
                                                  //Create the Message Object
                                                    //Get the message from EditText
                                                    String replyMessage = replyMessageEditText.getText().toString();
                                                    ParseObject newMessageObject = new ParseObject("Messages");
                                                    //Set the Acl for the object
                                                    ParseACL defaultAcl = new ParseACL();
                                                    defaultAcl.setPublicWriteAccess(true);
                                                    defaultAcl.setPublicReadAccess(true);
                                                    newMessageObject.setACL(defaultAcl);
                                                    //Add data to the new message object
                                                    newMessageObject.put("senderUserId",currentUser.getObjectId());
                                                    newMessageObject.put("receiverUserId",messagesReceivedList.get(which+1).getString("senderUserId"));
                                                    newMessageObject.put("senderUserName",currentUser.getUsername());
                                                    //Should be the users userID
                                                    newMessageObject.put("receiverUserName",messagesReceivedList.get(which + 1).getString("senderUserName"));
                                                    newMessageObject.put("messageContent",replyMessage);
                                                    newMessageObject.put("messageReadOrUnread", false);

                                                    newMessageObject.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            if(e== null){
                                                                Log.i("messageReplySent","Message was saved succesfully");
                                                                Toast.makeText(getApplicationContext(),"Message was sent to: " + messagesReceivedList.get(which + 1).getString("senderUserId"),Toast.LENGTH_SHORT);
                                                            }
                                                            else{
                                                                Log.i("messageReplySent","Error saving message"+ e.getMessage());
                                                                Toast.makeText(getApplicationContext(),"Message was not able to send to: " + messagesReceivedList.get(which + 1).getString("senderUserId")+"\n" +"error is "+ e.getMessage(),Toast.LENGTH_SHORT);

                                                            }
                                                        }
                                                    });//End of save in background
                                                    
                                                    Log.i("gotMessageTest", "This is the message form editText: " + replyMessage);
                                                }
                                            });//End of positive button
                                            builderInnerInnerReplyMessage.show();
                                        }
                                    });


                            builderInnerViewMessage.show();
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



 public String showDialog(){
     //Photo path from take picture dispath
     photoPathReturned ="";
     AlertDialog.Builder builder = new AlertDialog.Builder(this);
     builder.setMessage("Take or Select a photo from your gallery");
     builder.setPositiveButton("Take a photo", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             dispatchTakePictureIntent();
         }
     });
     builder.setNegativeButton("From albums", new DialogInterface.OnClickListener() {
         @Override
         public void onClick(DialogInterface dialog, int which) {
             Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
             startActivityForResult(i,REQUEST_PHOTO_ALBUM);
         }
     });


     builder.setCancelable(true);
    builder.create();
    builder.show();

     return photoPathReturned;
 }
    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.barberProfileImage){

            barberProfileImageisClicked = true;
            showDialog();
            Log.i("barberProfilePic","Profile image was clicked photo path: "+mProfilePhotoPath);

        }
        else if (v.getId() == R.id.barberImageView1){
            barberWorkImage1isClicked = true;
            showDialog();
            Log.i("barberProfilePic", "WorkImage1 was clicked");

        }
        else if (v.getId() == R.id.barberImageView2){
            barberWorkImage2isClicked = true;
            showDialog();
            Log.i("barberProfilePic", "WorkImage2 was clicked");

        }
        else if (v.getId() == R.id.barberImageView3){
            barberWorkImage3isClicked = true;
            showDialog();
            Log.i("barberProfilePic", "WorkImage3 was clicked");

        }
        else if(v.getId() == R.id.barberImageView4){
            barberWorkImage4isClicked = true;

            showDialog();
            Log.i("barberProfilePic", "WorkImage4 was clicked");

        }
        else if (v.getId() == R.id.barberImageView5){
            barberWorkImage5isClicked = true;

            showDialog();
            Log.i("barberProfilePic", "WorkImage5 was clicked");
        }
        else if (v.getId() == R.id.barberImageView6){
            barberWorkImage6isClicked = true;
            showDialog();
            Log.i("barberProfilePic", "WorkImage6 was clicked");

        }
    }
    /*
    * Code responsible for taking or selecting the picture we want to display in our
    * chosen imageview
    * */
    public void setPic(ImageView imageView,String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        //Final Image from Camera
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        Log.i("setPicTest","Created file with name: " +imageFileName +" \n "+"timeMade:" +timeStamp);
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //Check if the user has the camera permission on if not request it
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, 45);
            Log.i("CameraPermissionCheck","Camera permission is not set");

        }
        //Check permissions to read and write
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String []{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 14);
            Log.i("WritePermissionCheck","Write permission is not set");

        }
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                // Create the File where the photo should be saved to
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(getApplicationContext(), "Error occured" + " \n"+ex.getMessage(), Toast.LENGTH_LONG).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                //Path were photo is going to be saved too
                absolutePath = photoFile.getAbsolutePath();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.i("setPicTest","Boolean value of barberProfileImage" + barberProfileImageisClicked);
            if (barberProfileImageisClicked){
                barberProfileImageisClicked = false;

                if (requestCode == REQUEST_PHOTO_ALBUM && data != null) {
                    //Get the Uri for the image data returned
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        barberProfileImage.setImageBitmap(bitmapImage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Log.i("setPicTest","Trying to set pic for profileImage") ;
                    setPic(barberProfileImage,absolutePath);

                }
            }

            else if(barberWorkImage1isClicked){
                barberWorkImage1isClicked = false;
                if (requestCode == 1 && data != null) {

                    //Get the Uri for the image data returned
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        barberImageView1.setImageBitmap(bitmapImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Log.i("setPicTest","Trying to set pic for imageview1");
                    setPic(barberImageView1,absolutePath);

                }

            }
            else if(barberWorkImage2isClicked){
                barberWorkImage2isClicked = false;

                if (requestCode == 1 && data != null) {

                    //Get the Uri for the image data returned
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        barberImageView2.setImageBitmap(bitmapImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Log.i("setPicTest","Trying to set pic for imageview2");
                    setPic(barberImageView2,absolutePath);



                }

            }
            else if(barberWorkImage3isClicked){
                barberWorkImage3isClicked = false;

                if (requestCode == 1 && data != null) {

                    //Get the Uri for the image data returned
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        barberImageView3.setImageBitmap(bitmapImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Log.i("setPicTest","Trying to set pic for imageview3");
                    setPic(barberImageView3,absolutePath);
                }

            }
            else if(barberWorkImage4isClicked){
                barberWorkImage4isClicked = false;
                if (requestCode == 1 && data != null) {
                    //Get the Uri for the image data returned
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        barberImageView4.setImageBitmap(bitmapImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Log.i("setPicTest","Trying to set pic for imageview4");
                    setPic(barberImageView4,absolutePath);
                }

            }
            else if(barberWorkImage5isClicked){
                barberWorkImage5isClicked = false;

                if (requestCode == 1 && data != null) {

                    //Get the Uri for the image data returned
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        barberImageView5.setImageBitmap(bitmapImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Log.i("setPicTest","Trying to set pic for imageview5");
                    setPic(barberImageView5,absolutePath);
                }

            }
            else if(barberWorkImage6isClicked){
                barberWorkImage6isClicked = false;
                if (requestCode == 1 && data != null) {


                    //Get the Uri for the image data returned
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        barberImageView6.setImageBitmap(bitmapImage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    Log.i("setPicTest","Trying to set pic for imageview6");
                    setPic(barberImageView6,absolutePath);
                }

            }
            else
            {

            }


        }
        else{

        }
    }

    public class DownloadTask extends AsyncTask<String, Void, LatLng> {

        @Override
        protected LatLng doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            String latitude = "";
            String longitude = " ";
            LatLng addressLatLng = null;

            try {
                //Create url from input parameter
                url = new URL(urls[0]);

                //create HTTP connection for url
                urlConnection = (HttpURLConnection) url.openConnection();

                //Get the data returned from the http request
                InputStream in = urlConnection.getInputStream();

                //Create reader to read the Input stream
                InputStreamReader reader = new InputStreamReader(in);

                //Get the first character
                int data = reader.read();
                //Loop through the reader until we reach end
                while (data != -1) {

                    //cast the data into a char
                    char current = (char) data;
                    //concatenate to the result string
                    result += current;
                    //set pointer to next char
                    data = reader.read();

                }
                Log.i("Connection JSON result", result.toString());
                JSONObject resultsObject = new JSONObject(result);
                JSONArray resultsObjectArray = resultsObject.getJSONArray("results");
                Log.i("FirstParse", resultsObjectArray.toString());

                for (int i = 0; i < resultsObjectArray.length(); i++) {
                    JSONObject components = resultsObjectArray.getJSONObject(i);
                    Log.i("SecondParse", components.toString());
                    JSONObject location = components.getJSONObject("geometry");
                    JSONObject coordinates = location.getJSONObject("location");
                    Log.i("ThirdParse", coordinates.toString());
                     latitude = coordinates.getString("lat");
                    longitude = coordinates.getString("lng");

                    Log.i("GrandFinally", "lat:" + latitude + " lng: " + longitude);

                }//End of resultsObjectArray

                addressLatLng = new LatLng(Double.valueOf(latitude),Double.valueOf(longitude));
            }catch (Exception e) {
                e.printStackTrace();
                Log.i("GeocodeAttemptTest","Failed to connect" + e.getMessage());
            }
            return addressLatLng;
        }

    }
}
