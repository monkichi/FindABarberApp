package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;

/**
 * Created by chris on 6/5/16.
 */
public class SignUpActivity extends AppCompatActivity {

    Button signUpButton;
    String username;
    String password;
     String name;
     String email;
    EditText userUserNameEditText;
    EditText passwordEditText;
    EditText userEmailEditText;
    EditText userFullNameEditText;
    Switch barberOrUserSwitch;
    TextView userOrBarberTextView;
    String barberOrUser = "barber";
    private RelativeLayout relativeLayout;
    private ParseUser user;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);
        Toolbar actionToolbar = (Toolbar) findViewById(R.id.my_toolbar);

        setSupportActionBar(actionToolbar);
        passwordEditText = (EditText) findViewById(R.id.signUpPasswordEditText);
        userUserNameEditText = (EditText) findViewById(R.id.signUpUserNameEditText);
        userEmailEditText = (EditText) findViewById(R.id.signUpEmailEditText);
        userFullNameEditText = (EditText) findViewById(R.id.signUpNameEditText) ;
        barberOrUserSwitch = (Switch) findViewById(R.id.barberOrUserSwitch);
        userOrBarberTextView = (TextView) findViewById(R.id.barberOrUserView);
        relativeLayout = (RelativeLayout) findViewById(R.id.signRelativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);

            }
        });
        userOrBarberTextView.setText(barberOrUser);

        signUpButton =(Button) findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Sign Up", "Sign Up Button pressed");
                username = userUserNameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                name = userFullNameEditText.getText().toString();
                email = userEmailEditText.getText().toString();

                //Sign up user only if all information is inserted
                if (username.length() > 0 && password.length() > 0 && name.length() > 0 && email.length() > 0) {
                     user = new ParseUser();
                    final ParseACL userAcl= new ParseACL();
                    userAcl.setPublicWriteAccess(true);
                    userAcl.setPublicReadAccess(true);
                    user.setACL(userAcl);
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);

                    //Other fields can be set just like with a parse object
                    user.put("name", name);
                    user.put("barberOrUser",barberOrUser);
                    user.put("isFollowing", new ArrayList<String>());
                    //Create Barber if necessary
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("Parse Info", "Sign up was successful");
                                //Create the imageObject for the user
                                ParseObject userImages = new ParseObject("Images");
                                ParseACL imagesAcl = new ParseACL();
                                imagesAcl.setPublicReadAccess(true);
                                imagesAcl.setPublicWriteAccess(true);
                                userImages.setACL(imagesAcl);
                                //Set the Image objects look up key
                                userImages.put("UserObjectId",user.getObjectId());
                                userImages.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e== null){
                                            Log.i("userImageObject","Image object was sucessfully saved");
                                        }
                                        else{
                                            Log.i("userImageObect", "Error saving the image object " + e.getMessage());
                                        }
                                    }
                                });

                                ParseObject userFollowers = new ParseObject("Followers");
                                ParseACL followersACl = new ParseACL();
                                followersACl.setPublicReadAccess(true);
                                followersACl.setPublicWriteAccess(true);
                                userFollowers.setACL(followersACl);
                                userFollowers.put("followerUserId",user.getObjectId());
                                userFollowers.put("followersList", new ArrayList<String>());
                                userFollowers.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null){
                                            Log.i("userFollowersTest", "Saving Users was sucessful");
                                        }
                                        else{

                                        }
                                    }
                                });

                                if(barberOrUser.equals("barber")){
                                    //Open Barber Activity
                                    ParseObject barber = new ParseObject("Barbers");
                                    ParseACL defaultAcl = new ParseACL();
                                    defaultAcl.setPublicReadAccess(true);
                                    defaultAcl.setPublicWriteAccess(true);
                                    barber.setACL(defaultAcl);
                                    //Add the barberUsers Id to the Barbers table
                                    barber.put("barberUserId",user.getObjectId());
                                    //Add the barberUsers Name
                                    barber.put("barberName", user.getString("name"));
                                    //Add the barberUsers Username
                                    barber.put("barberUserName", user.getUsername());

                                    barber.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e==null){
                                                Log.i("BarberSaveTest","Barber save succesful");
                                            }
                                            else{
                                                Log.i("BarberSaveTest","Failed to save barber" + e.getMessage());
                                            }
                                        }
                                    });

                                    Intent userActivityIntent = new Intent(getApplicationContext(), BarberActivity.class);
                                    startActivity(userActivityIntent);
                                }
                                else{
                                    //Open user Activity

                                    Intent userActivityIntent = new Intent(getApplicationContext(), UserMapsActivity.class);
                                    startActivity(userActivityIntent);
                                }

                            } else {
                                Toast.makeText(getApplicationContext(), e.getMessage().substring(e.getMessage().indexOf(" ")), Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                } else {
                    if(name.length() == 0){
                        Toast.makeText(getApplicationContext(),"Please enter a name",Toast.LENGTH_LONG).show();
                    }
                    else if (email.length() == 0){
                        Toast.makeText(getApplicationContext(), "Please enter an email",Toast.LENGTH_LONG).show();
                    }
                    else if (username.length() == 0){
                        Toast.makeText(getApplicationContext(), "Please enter a username", Toast.LENGTH_LONG).show();
                    }
                    else if (password.length() == 0){
                        Toast.makeText(getApplicationContext(), "Please enter a passowrd", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

        barberOrUserSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    barberOrUser ="user";
                }
                else{

                    barberOrUser = "barber";

                }

                userOrBarberTextView.setText(barberOrUser);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
