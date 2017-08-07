package com.example.chris.firebasefindservices;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {


    //Log In Ui Widget Variable Declaration
    EditText userEmailAddressEditText;
    EditText userPasswordEditText;
    Button userSignInButton;
    TextView userSignUpTextView;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //Inflate UI variables to access widgets
        inflateUi();

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("messages");

        myRef.setValue("Hello, World!");


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            public String TAG = "FirebaseTest";

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });



    }


    public void inflateUi(){
        userEmailAddressEditText = (EditText) findViewById(R.id.userNameEmailAddressEditText);
        userPasswordEditText = (EditText) findViewById(R.id.userPasswordEditText);
        userSignInButton = (Button) findViewById(R.id.signInButton);
        userSignUpTextView = (TextView) findViewById(R.id.signUpClickableTextView);

        signUpLogic();

        signInWithEmail();
    }

    public void signInWithEmail(){
               userSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userEmail =  userEmailAddressEditText.getText().toString();
                String userPassword =  userPasswordEditText.getText().toString();

                Log.d("inputTest", userEmail + " " + userPassword);

                mAuth.signInWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Log in", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),UserMainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Log in", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });


    }

    public void signUpLogic(){
        userSignUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });
    }

}
