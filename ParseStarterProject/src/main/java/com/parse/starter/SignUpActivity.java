package com.parse.starter;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);

        passwordEditText = (EditText) findViewById(R.id.signUpPasswordEditText);
        userUserNameEditText = (EditText) findViewById(R.id.signUpUserNameEditText);
        userEmailEditText = (EditText) findViewById(R.id.signUpEmailEditText);
        userFullNameEditText = (EditText) findViewById(R.id.signUpNameEditText) ;
        barberOrUserSwitch = (Switch) findViewById(R.id.barberOrUserSwitch);
        userOrBarberTextView = (TextView) findViewById(R.id.barberOrUserView);
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
                    ParseUser user = new ParseUser();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setEmail(email);

                    //Other fields can be set just like with a parse object
                    user.put("name", name);
                    user.put("barberOrUser",barberOrUser);
                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.i("Parse Info", "Sign up was successful");

                                if(barberOrUser.equals("barber")){
                                    //Open Barber Activty


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
