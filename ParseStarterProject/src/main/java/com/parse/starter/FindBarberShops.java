package com.parse.starter;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by chris on 7/26/17.
 */

public class FindBarberShops extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    public FindBarberShops(){

    }


    public void locateBarberShops(Location location){

        //GoogleApiClient.Builder mGoogleApiClient = new GoogleApiClient.Builder(this).addApi();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
