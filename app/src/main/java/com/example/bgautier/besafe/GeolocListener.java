package com.example.bgautier.besafe;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class GeolocListener {
    private final String TAG = "GeolocListener";
    private LocationManager locationManager;
    private Context context;

    public GeolocListener(Context context) throws SecurityException {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.context = context;
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

    public void callApiLocation(String id) {

        RequestQueue queue = Volley.newRequestQueue(this.context);
        String url = "http://hdaroit.fr:3000{}/api/appusers/"+id;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PATCH, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("That didn't work!","");
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
              //  params.put("", );
                params.put("domain", "http://itsalif.info");

                return params;
            }
        };
        queue.add(stringRequest);
    }



}


