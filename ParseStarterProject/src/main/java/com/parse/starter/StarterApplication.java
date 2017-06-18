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

    // Add your initialization code here
    Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
            .applicationId("testapp3245jfjdkfa88983937893789")
            .clientKey("dfjjdafdfjkc83727373")
            .server("http://testapp3245.herokuapp.com/parse/")
            .build()
    );
//    testapp3245.herokuapp.com
    //application pass word: kt8MjMdgNiqH


    /*
    * Can pause some issues
    * http://stackoverflow.com/questions/20254545/parse-for-android-parseuser-logout-doesnt-log-user-out
    * */
    //ParseUser.enableAutomaticUser();

    ParseObject score = new ParseObject("Score");
    score.put("userName", "Matt");
    score.saveInBackground(new SaveCallback() {
      @Override
      public void done(ParseException e) {
        if (e==null){
          Log.d("ParseAmazonCheck", "Data was saved succesfull");
        }
        else{
          Log.e("ParseAmazonError", "Data was not saved " + e.toString());
        }
      }
    });


    ParseACL defaultACL = new ParseACL();
    // Optionally enable public read access.
    defaultACL.setPublicReadAccess(true);
    ParseACL.setDefaultACL(defaultACL, true);






  }
}
