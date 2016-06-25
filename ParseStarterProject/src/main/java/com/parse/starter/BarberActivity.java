package com.parse.starter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
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
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
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
    private String mProfilePhotoPath = "";
    private String mWorkPhoto1Path= "";
    private String mWorkPhoto2Path= "";
    private String mWorkPhoto3Path="";
    private String mWorkPhoto4Path="";
    private String mWorkPhoto5Path="";
    private String mWorkPhoto6Path= "";
    private String photoPathReturned= "";
    private String absolutePath="";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barber_layout);
        Toolbar actionToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(actionToolbar);
        relativeLayout = (RelativeLayout) findViewById(R.id.barberRelativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dismiss the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
            }
        });

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
                    currentUser = ParseUser.getCurrentUser();
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
                           final  LatLng address = task.execute(googleGeocodingURL + addressStreetNumber + "+" +addressStreetName + "+" + addressStreetCity + addressStreetState).get();
                            if (address!= null){
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("Barbers");
                                query.whereEqualTo("barberUserId",currentUser.getObjectId());
                                query.findInBackground(new FindCallback<ParseObject>() {
                                    @Override
                                    public void done(List<ParseObject> objects, ParseException e) {
                                        if (e == null) {
                                            if(objects != null && objects.size() > 0){
                                                ParseObject barberObject = objects.get(0);
                                                Log.i("BarberQuery","Query was succesful with object id "+ currentUser.getObjectId() +" object " + barberObject.get("barberUserId"));

                                                barberObject.put("barberAddress", new ParseGeoPoint(address.latitude,address.longitude));
                                                barberObject.put("barberAboutText", barberAboutYouEditText.getText().toString());
                                                barberObject.put("barberPlaceName",barberPlaceNameEditText.getText().toString());
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
                                        }//End of if
                                        else {
                                            // something went wrong
                                            Log.i("BarberQuery","Query not succesful "+ e.getMessage() );
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

    }//End of onCreate




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

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
                    Intent leftSwipeViewFollowingIntent = new Intent(getApplicationContext(), ViewFollowingActivity.class);
                    startActivity(leftSwipeViewFollowingIntent);

                }

                // if right to left sweep event on screen
                if (x1 > x2)
                {
                    Toast.makeText(this, "Right to Left Swap Performed", Toast.LENGTH_SHORT).show();
                    Intent rightSwipeBarberProfileIntent = new Intent(getApplicationContext(),BarberProfileActivity.class);
                    startActivity(rightSwipeBarberProfileIntent);
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
             startActivityForResult(i,1);
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

                if (requestCode == 1 && data != null) {
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
