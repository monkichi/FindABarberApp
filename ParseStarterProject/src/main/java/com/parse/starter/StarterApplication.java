/*
 * Copyright (c) 2015-present, Parse, LLC.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */
package com.parse.starter;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;


public class StarterApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Enable Local Datastore.
    Parse.enableLocalDatastore(this);

//    // Add your initialization code here
//    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
//            .applicationId("testapp3245jfjdkfa88983937893789")
//            .clientKey("dfjjdafdfjkc83727373")
//            .server("http://testapp3245.herokuapp.com/parse/")
//            .build()
//    );


    // Add your initialization code here
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("6f7b68060b9a13173083d6668707d675ef078043")
            .clientKey("3bf794a59cac1dda19adc73b74cd9ffd290744b7")
            .server("http://ec2-52-41-16-205.us-west-2.compute.amazonaws.com/parse/")
            .build()
    );
//    testapp3245.herokuapp.com
    //application pass word: kt8MjMdgNiqH


    /*
    * Can pause some issues
    * http://stackoverflow.com/questions/20254545/parse-for-android-parseuser-logout-doesnt-log-user-out
    * */
    //ParseUser.enableAutomaticUser();


    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);






  }
}
