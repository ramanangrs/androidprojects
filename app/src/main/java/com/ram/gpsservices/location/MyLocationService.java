package com.ram.gpsservices.location;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class MyLocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    public final static String MY_LOCATION = "MY_CURRENT_LOCATION";
    public final static String INTENT_LOCATION_VALUE = "currentLocation";
    private static final int LOCATION_INTERVAL = 1000;
    public static final float mGPSAccuracyLevel = 10f;  // Accuracy level set <10> meters
    private int recordCountStatus = 0;
    Intent intent;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Service", "call GoogleAPI");
        buildGoogleApiClient();

        intent = new Intent(MY_LOCATION);
    }

    public MyLocationService() {
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i("Service", "initiate GoogleAPI");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // in StartCommand uses - we have to stop the actions
        Log.i("Service", "Check GoogleAPI Status");
        recordCountStatus = 0;

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(LOCATION_INTERVAL)        // 1 seconds, in milliseconds
                    .setFastestInterval(5000); // 5 second, in milliseconds
        }
        return START_STICKY;
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection failed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }


    private void handleNewLocation(Location location) {
        double mAccuracy = location.getAccuracy(); // Get Accuracy
        if (mAccuracy < mGPSAccuracyLevel) {
            if (recordCountStatus == 0) {    // prevent multiple calls
                recordCountStatus += 1;
                stopLocationUpdates();
            }
        }
        intent.putExtra(INTENT_LOCATION_VALUE, location);
        sendBroadcast(intent);
    }

    protected void stopLocationUpdates() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}
