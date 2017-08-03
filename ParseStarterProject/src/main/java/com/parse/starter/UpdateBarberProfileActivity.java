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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.parse.starter.findabarberapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberInputStream;
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
public class UpdateBarberProfileActivity extends AppCompatActivity implements View.OnClickListener {
    int REQUEST_PHOTO_ALBUM= 1;

    ImageView barberProfileImage;

    EditText barberAddressEditText;
    EditText barberPlaceNameEditText;
    EditText barberAboutYouEditText;
    Button updateBarberProButton;


    private static final int REQUEST_IMAGE_CAPTURE = 1888;
    String googleGeocodingURL = "https://maps.googleapis.com/maps/api/geocode/json?address=";
    String key= R.string.google_maps_key +"\n";
    private float x1,x2;
    private float y1, y2;

    private boolean barberProfileImageisClicked=false;
    private RecyclerView recyclerView;

    String mProfilePhotoPath = "";
    String photoPathReturned= "";
    String absolutePath="";

    ParseObject barberObject;
    ParseObject currentUserImageObject;
    ParseUser currentUser;

    List<ParseObject> messagesReceivedList;
    List<String> messagesReceiverUserNameList;

    List<ParseFile> barberWorkImagesFileList;
    ArrayList<Bitmap> barberWorkImagesIntList;
    private BarberWorkImagesAdapter adapter;    //Adapter for default images
    MessagesListAdapter arrayAdapter;
    private boolean isDefaultImageList= false;
    private ParseFile barberWorkImageFile;
    List<ParseObject> barberMessagesObjectsList;
    List<MessagesDataModel> barberMessagesInfoObjectList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.barber_layout);

        //Set up toolabar
        Toolbar actionToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        actionToolbar.setTitle("Update Profile");
        setSupportActionBar(actionToolbar);

        //Inflate UI's for barber profile input info
        barberAddressEditText = (EditText) findViewById(R.id.barberAddressEditText);
        barberPlaceNameEditText = (EditText) findViewById(R.id.barberPlaceNameEditText);
        barberAboutYouEditText = (EditText) findViewById(R.id.barberAboutYouEditText);
        updateBarberProButton = (Button) findViewById(R.id.updateBarberProButton);

        //Set up keyboard management
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.barberRelativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        });

        //instantiate list of message and barber work files
        messagesReceivedList = new ArrayList<ParseObject>();
        barberMessagesInfoObjectList = new ArrayList<MessagesDataModel>();


        barberWorkImagesFileList = new ArrayList<ParseFile>();
        barberWorkImagesIntList = new ArrayList<Bitmap>();

        //Store the currentUSer Object
        currentUser = ParseUser.getCurrentUser();

        //Get the currentUserBarbers info
        getCurrentBarber();

        //Set up Grid view of the barbers work Images
        recyclerView = (RecyclerView) findViewById(R.id.barber_work_images_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(UpdateBarberProfileActivity.this, LinearLayoutManager.HORIZONTAL, false));
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));// Here 2 i

        //instantiate addImageButton
        Button addImageButton = (Button) findViewById(R.id.addImageButton);
        barberProfileImage =(ImageView) findViewById(R.id.barberProfileImage);
        if (barberProfileImage != null) {
            barberProfileImage.setOnClickListener(this);
        }

        //set adapter for barber Work images gridview
        adapter = new BarberWorkImagesAdapter(UpdateBarberProfileActivity.this, barberWorkImagesIntList);
        recyclerView.setAdapter(adapter);// set adapter on recyclerview

        ///Query the Images
        ParseQuery<ParseObject> imagesDownLoadQuery = ParseQuery.getQuery("Images");
        imagesDownLoadQuery.whereEqualTo("UserObjectId", currentUser.getObjectId());
        imagesDownLoadQuery.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e==null){
                    currentUserImageObject = object;
                    Log.i("ImagesQuerySuccess","Got a result " + currentUserImageObject.get("UserObjectId") );
                    try {
                        ParseFile profileImageFile = currentUserImageObject.getParseFile("ProfileImageFile");
                        byte[] profileImageByteArray = new byte[0];
                        if (profileImageFile != null) {
                            profileImageByteArray = profileImageFile.getData();
                            barberProfileImage.setImageBitmap(BitmapFactory.decodeByteArray(profileImageByteArray, 0, profileImageByteArray.length));

                            //Get list of Images
                            barberWorkImagesFileList = currentUserImageObject.getList("ImagesFileList");
                            if (barberWorkImagesFileList != null && barberWorkImagesFileList.size() > 0) {

                                for (int i=0; i< barberWorkImagesFileList.size() ; i++) {

                                    if (barberWorkImagesFileList.get(i)!= null) {
                                        ParseFile workImage = barberWorkImagesFileList.get(i);
                                        byte[] barberWorkImageByteArray = new byte[0];
                                        if (workImage != null) {
                                            barberWorkImageByteArray = workImage.getData();
                                            barberWorkImagesIntList.add(BitmapFactory.decodeByteArray(barberWorkImageByteArray, 0, barberWorkImageByteArray.length));
                                            adapter = new BarberWorkImagesAdapter(UpdateBarberProfileActivity.this, barberWorkImagesIntList);
                                            recyclerView.setAdapter(adapter);// set adapter on recyclerview
                                        }
                                    }
                                }
                                //after list has been traversed, set data adapter
                                adapter.notifyDataSetChanged();
                            }
                            //Logic for an empty list of images in barber Database
                            else {
                                //populate a list of default images to send to gridview
                                populatRecyclerView();
                            }

                        }
                    }
                    //End of try
                    catch (ParseException e1){
                        Log.i("imageUploadTest", "Error occured trying to query imageobject " + e1.getMessage());
                    }
                }//End of query exception check
                else{
                    Log.i("ImagesQuerySuccess","Error occured " + e.getMessage());
                }
            }//End of ParseQuery Result Method
        });



        //Set Up Listener for button click
        updateBarberProButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Check all info is filled out in database

                if(barberAddressEditText.getText()!= null && barberPlaceNameEditText.getText()!=null && barberAboutYouEditText.getText()!=null){
                    updateBarberInfo();
                }
                else{
                    if (barberAddressEditText.getText()==null){
                        Toast.makeText(getApplicationContext(), "Please provide an Address", Toast.LENGTH_LONG).show();
                    }

                    if (barberAboutYouEditText.getText()== null){
                        Toast.makeText(getApplicationContext(), "Please provide info about you", Toast.LENGTH_LONG).show();

                    }

                    if (barberPlaceNameEditText.getText() == null){
                        Toast.makeText(getApplicationContext(), "Please provide an barber place", Toast.LENGTH_LONG).show();

                    }
                }



            }
        });


/*
*  Code for detecting the swipe on screens
*
* */
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
                            Toast.makeText(UpdateBarberProfileActivity.this, "Left to Right Swap Performed", Toast.LENGTH_SHORT).show();
                            Intent leftSwipeViewFollowingIntent = new Intent(getApplicationContext(), ViewFollowersActivity.class);
                            startActivity(leftSwipeViewFollowingIntent);

                        }

                        // if right to left sweep event on screen
                        if (x1 > x2)
                        {
                            Toast.makeText(UpdateBarberProfileActivity.this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                            Intent rightSwipeBarberProfileIntent = new Intent(getApplicationContext(),BarberProfileActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("barberObjectId",barberObject.getObjectId());
                            rightSwipeBarberProfileIntent.putExtras(bundle);
                            startActivity(rightSwipeBarberProfileIntent);
                        }

                        // if UP to Down sweep event on screen
                        if (y1 < y2)
                        {
                            Toast.makeText(UpdateBarberProfileActivity.this, "UP to Down Swap Performed", Toast.LENGTH_SHORT).show();
                        }

                        //if Down to UP sweep event on screen
                        if (y1 > y2)
                        {
                            Toast.makeText(UpdateBarberProfileActivity.this, "Down to UP Swap Performed", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                return true;
            }
        });

        //populatRecyclerView();
        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Show dialog to determine where to bring picture
                showDialog();
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
            AlertDialog.Builder viewMessagesBuilder = new AlertDialog.Builder(UpdateBarberProfileActivity.this);
            viewMessagesBuilder.setTitle("Messages");

            arrayAdapter = new MessagesListAdapter(
                    UpdateBarberProfileActivity.this,
                    barberMessagesInfoObjectList, R.layout.messages_item_row_layout);
        

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
                            final String originalMessaSenderUserName = barberMessagesInfoObjectList.get(which).messageSenderUserName;
                            AlertDialog.Builder builderInnerViewMessage = new AlertDialog.Builder(
                                    UpdateBarberProfileActivity.this);
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
                                            AlertDialog.Builder builderInnerInnerReplyMessage = new AlertDialog.Builder(UpdateBarberProfileActivity.this);
                                            //Add senders userName
                                            builderInnerInnerReplyMessage.setTitle("Reply to:" + originalMessaSenderUserName);
                                            final EditText replyMessageEditText = new EditText(UpdateBarberProfileActivity.this);
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
                                                                Toast.makeText(getApplicationContext(),"Message was sent to: " + messagesReceivedList.get(which + 1).getString("senderUserId"),Toast.LENGTH_SHORT).show();
                                                            }
                                                            else{
                                                                Log.i("messageReplySent","Error saving message"+ e.getMessage());
                                                                Toast.makeText(getApplicationContext(),"Message was not able to send to: " + messagesReceivedList.get(which + 1).getString("senderUserId")+"\n" +"error is "+ e.getMessage(),Toast.LENGTH_SHORT).show();

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



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.i("setPicTest", "Boolean value of barberProfileImage" + barberProfileImageisClicked);
            //Get the Uri for the image data returned
            if (requestCode == REQUEST_PHOTO_ALBUM ) {
                if (barberProfileImageisClicked) {
                    barberProfileImageisClicked = false;//Get the Uri for the image data returned

                    try {
                        //Get the Uri for the image data returned
                        Uri selectedImage = data.getData();
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        barberProfileImage.setImageBitmap(bitmapImage);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }//User wants to get picture for barberWorkImages logic
                } else {

                    try {
                        //Get the Uri for the image data returned
                        Uri selectedImage = data.getData();
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        //add bitmap to list and notify adapter(update)
                        if(isDefaultImageList == false) {

                            barberWorkImagesIntList.add(bitmapImage);
                            adapter.notifyDataSetChanged();
                        }
                        else{
                            barberWorkImagesIntList.clear();
                            barberWorkImagesIntList.add(bitmapImage);
                            adapter.notifyDataSetChanged();
                        }
                         barberWorkImageFile = new ParseFile("Image" + barberWorkImagesIntList.size(),getImageViewByteArray(bitmapImage));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Log.i("setPicTest", "Trying to set pic for profileImage");
                if (barberProfileImageisClicked) {
                    barberProfileImageisClicked = false;
                    setPic(barberProfileImage, absolutePath);
                } else {
                    Log.i("setPicTest", "Trying to set pic for profileImage");
                    //add picture to list of barberWorkerImages
                    Bitmap bitmap = BitmapFactory.decodeFile(absolutePath);
                   if(isDefaultImageList == false) {

                       barberWorkImagesIntList.add(bitmap);
//                       Toast.makeText(getApplicationContext(), "After Add Image Test" + barberWorkImagesIntList.size(), Toast.LENGTH_LONG).show();
                       adapter.notifyDataSetChanged();// Notify the adapter
                   }
                   else {
                       barberWorkImagesIntList.clear();
                       barberWorkImagesIntList.add(bitmap);
//                       Toast.makeText(getApplicationContext(), "After Add Image Test" + barberWorkImagesIntList.size(), Toast.LENGTH_LONG).show();
                       adapter.notifyDataSetChanged();// Notify the adapter
                       isDefaultImageList = false;
                   }
                   barberWorkImageFile = new ParseFile("Image" + barberWorkImagesIntList.size(),getImageViewByteArray(bitmap));

                }
            }

            barberWorkImagesFileList.add(barberWorkImageFile);
        }
    }


    //Method to Take a picture
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
    //Method for getting a picture image file from picture taken
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
        for (int i = 0; i < mThumbIds.length; i++) {
            Bitmap bitmapConvert = BitmapFactory.decodeResource(getResources(),mThumbIds[i]);
            barberWorkImagesIntList.add(bitmapConvert);
        }
        Toast.makeText(getApplicationContext(),"Before Add Image Test"+ barberWorkImagesIntList.size(),Toast.LENGTH_SHORT).show();

        adapter.notifyDataSetChanged();// Notify the adapter

        isDefaultImageList = true;



    }
    public void getCurrentBarber() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Barbers");
        query.whereEqualTo("barberUserId",currentUser.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    if(object != null ){
                        barberObject = object;
                        Log.i("BarberQuery","Query was succesful with object id "+ currentUser.getObjectId() +" object " + barberObject.get("barberUserId"));

                        barberAboutYouEditText.setText(barberObject.getString("barberAboutText"));
                        barberPlaceNameEditText.setText(barberObject.getString("barberPlaceName"));
                        //barberAddressEditText.setText(barberObject.getString("barberAddress"));


                        //Query all the messages for the currentUserId which should be a barber
                        ParseQuery<ParseObject> getMessagesObject = new ParseQuery<ParseObject>("Messages");
                        getMessagesObject.whereEqualTo("receiverUserId",currentUser.getObjectId());
                        getMessagesObject.orderByDescending("createdAt");
                        getMessagesObject.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e==null){
                                    if (objects.size() > 0){
                                        Log.i("messagesQueryTest", "Query was succesful");
                                        for (ParseObject messages: objects){
                                            Log.i("messages", "Message from" + messages.getString("senderUserName")+ " " + "\n"+
                                                    "message content is " + messages.getString("messageContent"));
                                            //Message content
                                            String messageContent= messages.getString("messageContent") ;
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
                                            MessagesDataModel messageModel = new MessagesDataModel(messageContent,messageSenderUserName,messageReceiverUserName,messageReadStatus,getMessageReceiverUserId);

                                            barberMessagesInfoObjectList.add(messageModel);
                                            //Add the messages for currentUser to list
                                            messagesReceivedList.add(messages);

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
    public byte[] getImageViewByteArray(Bitmap prof){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // Compress image to lower quality scale 1 - 100
        prof.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        return image;
    }

    public void updateBarberInfo(){

        DownloadTask task = new DownloadTask();

        //Get the address string and parse
        String addressString = barberAddressEditText.getText().toString();

        String [] addressStringArray = addressString.split(" ");

        String addressStreetNumber = addressStringArray[0];
        String addressStreetName = addressStringArray[1] + "+"+ addressStringArray[2] +",";
        String addressStreetCity = addressStringArray[3];
        String addressStreetState = addressStringArray[4];

        try {

            //Get the address in the background from string
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
                // Create a column named "ImageFile" and insert the image
                currentUserImageObject.put("ProfileImageFile", profileImageFile);
                //Get list of ParseFiles

                barberWorkImagesFileList.remove(null);
                currentUserImageObject.put("ImagesFileList",barberWorkImagesFileList);

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
                            Intent goBackToMainPageActivity = new Intent(getApplicationContext(), BarberMainActivity.class);
                            startActivity(goBackToMainPageActivity);
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
