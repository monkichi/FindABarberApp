package com.example.chris.firebasefindservices;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created by chris on 8/5/17.
 */

public class SignUpActivity extends AppCompatActivity {

    //Sign Up user Info UI Widgets
    EditText userNameEditText;
    EditText userPasswordEditText;
    EditText userEmailEditText;

    Button registerUserButton;

    private FirebaseAuth mAuth;

    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inflateUI();

        mAuth = FirebaseAuth.getInstance();
    }

    public void inflateUI(){
        userEmailEditText = (EditText) findViewById(R.id.emailEditText);
        userPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
        registerUserButton = (Button) findViewById(R.id.registerButton);

        setUpUserRegisterButton();
    }

    public void setUpUserRegisterButton(){
        registerUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String userEmail = userEmailEditText.getText().toString();
                String userPassword = userPasswordEditText.getText().toString();
                String userName = userNameEditText.getText().toString();


               mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if (task.isSuccessful()) {
                           // Sign in success, update UI with the signed-in user's information
                           Log.d(TAG, "createUserWithEmail:success");
                           FirebaseUser user = mAuth.getCurrentUser();
                           user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       Toast.makeText(getApplicationContext(),"Email Verification email sent to " + userEmail, Toast.LENGTH_LONG).show();
                                   }
                               }
                           });

                       } else {
                           // If sign in fails, display a message to the user.
                           Log.w(TAG, "createUserWithEmail:failure", task.getException());
                           Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                   Toast.LENGTH_SHORT).show();
                       }
                   }
               });
            }
        });
    }
}
