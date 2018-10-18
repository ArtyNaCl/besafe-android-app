package com.example.bgautier.besafe;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GeolocListener {
    private final String TAG = "GeolocListener";
    private LocationManager locationManager;

    public GeolocListener(Context context) throws SecurityException {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(TAG, "Status changed : " + provider + "(" + String.valueOf(status) + ")");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.d(TAG, "Provider enabled : " + provider);
            }


            @Override
            public void onProviderDisabled(String provider) {
                Log.d(TAG, "Provider disabled : " + provider);
            }


            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Latitude " + location.getLatitude() + ", longitude " + location.getLongitude());
                // TODO : Update API
            }
        });
    }

    public Location getLastGeoloc() throws SecurityException {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {
            return location;
        }

        return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
}


