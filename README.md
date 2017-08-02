#GPS location via a service in Android 

Android Service that run in background and receive location updates on broadcast receiver.

 Get current location every few minute interval 
 
    private static final int LOCATION_INTERVAL = 1000;  // Set location interval 1000*1 = 1 Sec
    
    public static final float mGPSAccuracyLevel = 10f;  // Accuracy level set <10> meters
    
Using GoogleApiClient and LocationListener

*Google Play provides the fused location provider to retrieve the device’s last known location

*LocationListener Used for receiving notifications from the LocationManager when the location has changed. These methods are called if the LocationListener has been registered with the location manager service using the requestLocationUpdates(String, long, float, LocationListener) method. 

To use the location manager make the Google play service available via your app build.gradle file.
dependencies {
    compile 'com.google.android.gms:play-services-location:11.0.0'
    }
    
Also specify the following required permission in your manifest.
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <!-- includes permission only for NETWORK_PROVIDER. -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <!-- includes permission both for NETWORK_PROVIDER and GPS_PROVIDER. -->
    
If you want to access the GPS sensor, you need the ACCESS_FINE_LOCATION permission. 
Otherwise you need the ACCESS_COARSE_LOCATION permission
