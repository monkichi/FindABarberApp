/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.LogInCallback;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class MainActivity extends AppCompatActivity {

 EditText userNameEditText;
  EditText userPasswordEditText;
  Button  loginButton;
  TextView signUpTextView;
    RelativeLayout relativeLayout;

    public void loginButtonListener(){
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("Button test", "Login button was pressed");
                //Only log in users if all the editText are NOT empty
                if(String.valueOf(userNameEditText.getText()).length() > 0 && String.valueOf(userPasswordEditText.getText()).length() >0 ){
                    ParseUser.logInInBackground(userNameEditText.getText().toString(), userPasswordEditText.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if (user != null){
                                //User Succesfully Logged in
                                Log.i("Parse Log In", "Login  was sucessfull" );
                                Log.i("Parse Log In","Users status" + " " + user.getUsername() + " "
                                + user.getString("barberOrUser"));
                                if(user.getString("barberOrUser").equals("barber")){

                                    Intent barberActivityIntent = new Intent(getApplicationContext(),BarberActivity.class);
                                    startActivity(barberActivityIntent);
                                }
                                else{
                                    Intent userActivityIntent = new Intent(getApplicationContext(), UserMapsActivity.class);
                                    startActivity(userActivityIntent);
                                }
                            }
                            else{
                                //Login was not succesful
                                Toast.makeText(getApplicationContext(), e.getMessage() ,Toast.LENGTH_LONG).show();

                            }
                        }
                    });
                }
                //Empty editText somewhere in the code
                else{
                    if (userNameEditText.getText().toString().length() == 0){
                        Toast.makeText(getApplicationContext(),"Username cannot be empty",Toast.LENGTH_LONG).show();

                    }
                    else if (userPasswordEditText.getText().toString().length() == 0){
                        Toast.makeText(getApplicationContext(), "Password cannot be empty", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

    }

    public void signUpButtonListener() {
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Check if we are currently logged in before Signing up
                if (ParseUser.getCurrentUser() != null) {
                    // do stuff with the user
                    Toast.makeText(getApplicationContext(), "User is already logged in", Toast.LENGTH_SHORT).show();
                } else {
                    // show the signup or login screen
                    Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(signUpIntent);

                }

            }
        });
    }

        @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
            ParseAnalytics.trackAppOpenedInBackground(getIntent());
            String barberOrUser = " ";
    getSupportActionBar();
    userNameEditText = (EditText) findViewById(R.id.usernameEditText);
    userPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
    loginButton = (Button) findViewById(R.id.loginRegisterButton);
            signUpTextView = (TextView) findViewById(R.id.signUpView);
            relativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
            relativeLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    hideSoftKeyboard(MainActivity.this);
                    Toast.makeText(getApplicationContext(),"You touched outside the keyboard",Toast.LENGTH_LONG).show();
                }
            });
            loginButtonListener();
            signUpButtonListener();
            //Check if user is logged int
            ParseUser  currentUser = ParseUser.getCurrentUser();
            if (currentUser !=  null){
                Log.i("Get barberOrUser test","Not Null user" + currentUser.getUsername() + " " + currentUser.get("barberOrUser"));
                barberOrUser = currentUser.getString("barberOrUser");
                if (barberOrUser != null){
                    if(barberOrUser.equals("barber")){
                        Intent barberActityintent = new Intent(getApplicationContext(),BarberActivity.class);
                        startActivity(barberActityintent);
                    }
                    else{
                        Intent userActivity = new Intent(getApplicationContext(),UserMapsActivity.class);
                        startActivity(userActivity);
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"Cannot get barberOrUser for" + " " + currentUser.getUsername(),Toast.LENGTH_LONG).show();
                }

            }


  }



    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.logOut_action) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }
}
