package gdm.darkskyweatherapp.Model;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import gdm.darkskyweatherapp.View.MainActivity;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;
    private int STORAGE_PERMISSION_CODE = 23;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    // Location information
    Location location;
    double latitude;
    double longitude;

    // The minimum distance for polling new location updates
    private static final long MIN_DISTANCE_CHANGE = 100;

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60;

    // Declaring a LocationManager
    protected LocationManager locationManager;

    // Constructor
    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {

            locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

            // Get the GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            // Also get the network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // Since nothing is enabled we won't be able to get the users location... :(
            } else {
                canGetLocation = true;

                // First get the location from the network provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE, this);
                        Log.d("Network", "Network");
                        if (locationManager != null) {
                             List<String> providers = locationManager.getProviders(true);
                            for (String provider : providers) {
                                Location l = locationManager.getLastKnownLocation(provider);
                                if (l == null) {
                                    continue;
                                }
                                if (location == null || l.getAccuracy() < location.getAccuracy()) {
                                    // Found best last known location: %s", l);
                                    location = l;
                                }
                            }                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }

                // Use GPS to get a more accurate reading if it's enabled
                if (isGPSEnabled) {
                    if (location == null) {
                       locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                             List<String> providers = locationManager.getProviders(true);
                             for (String provider : providers) {
                                Location l = locationManager.getLastKnownLocation(provider);
                                if (l == null) {
                                    continue;
                                }
                                if (location == null || l.getAccuracy() < location.getAccuracy()) {
                                    // Found best last known location: %s", l);
                                    location = l;
                                }
                            }
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    public void stopUsingGPS(){
        if (locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }
    
    public boolean canGetLocation() {
        return canGetLocation;
    }
    
    public double getLatitude(){
        return latitude;
    }
 
    public double getLongitude(){
        return longitude;
    }
    
    @Override
    public void onLocationChanged(Location location) {
    }
 
    @Override
    public void onProviderDisabled(String provider) {
    }
 
    @Override
    public void onProviderEnabled(String provider) {
    }
 
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
 
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}

