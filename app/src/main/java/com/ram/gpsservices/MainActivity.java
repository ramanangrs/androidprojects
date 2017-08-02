package com.ram.gpsservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.ram.gpsservices.location.MyLocationService;
import com.ram.gpsservices.utils.Utility;

public class MainActivity extends AppCompatActivity {

    private MyReceiver myReceiver;
    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView currentLocationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentLocationView = (TextView) findViewById(R.id.currentLocationView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean checkPermission = Utility.checkLocationPermission(MainActivity.this);
        if (checkPermission) {
            //check GPS
            boolean isGPSCheck = Utility.checkGpsEnable(MainActivity.this);
            if (isGPSCheck) {
                //Success
                //Start Location service
                Intent intent = new Intent(MainActivity.this, MyLocationService.class);
                startService(intent);
                //Register BroadcastReceiver to receive event from our location service
                myReceiver = new MyReceiver();
                registerReceiver(myReceiver, new IntentFilter(MyLocationService.MY_LOCATION));
            } else {
                //Failure
                Toast.makeText(MainActivity.this, "GPS location services not enabled.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Location permission not enabled.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Unregister the receiver
        unregisterMyBroadcastReceiver();
        //Check the status of service
        boolean isServiceRunning = Utility.isMyServiceRunning(MyLocationService.class, MainActivity.this);
        // If service is running,stop the location service
        if (isServiceRunning)
            stopService(new Intent(MainActivity.this, MyLocationService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context arg0, Intent arg1) {
            Bundle extras = arg1.getExtras();
            if (extras != null) {
                Location currentPosition = arg1.getParcelableExtra(MyLocationService.INTENT_LOCATION_VALUE);
                Log.v(TAG, "Triggered by Service!\n" + "Data passed Lat : " + String.valueOf(currentPosition.getLatitude())
                        + "Lng:" + String.valueOf(currentPosition.getLongitude() + "Accuracy: " + currentPosition.getAccuracy()));

                currentLocationView.setText("" + "Triggered by Service!\n" + "Data passed Lat : " + String.valueOf(currentPosition.getLatitude())
                        + "Lng:" + String.valueOf(currentPosition.getLongitude() + "Accuracy: " + currentPosition.getAccuracy()));
            }
        }
    }

    private void unregisterMyBroadcastReceiver() {
        if (null != myReceiver) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
    }
}
