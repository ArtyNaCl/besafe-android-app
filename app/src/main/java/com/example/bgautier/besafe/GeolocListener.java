package com.example.bgautier.besafe;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GeolocListener {
    private final String TAG = "GeolocListener";
    private Context activity;
    private String token;
    private String userId;
    public Location lastLoc;

    public GeolocListener(Activity activity, String userId, String token) {
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        this.activity = activity;
        this.userId = userId;
        this.token = token;

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 123);
        } else {
            Log.d(TAG, "Already have permissions.");
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, new LocationListener() {
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
                callApiLocation(location);
                lastLoc = location;
            }
        });
    }

    private void callApiLocation(final Location location) {
        RequestQueue queue = Volley.newRequestQueue(activity);
        String url = "http://hdaroit.fr:3000/api/appusers/" + this.userId + "?access_token=" + this.token;

        // Request a string response from the provided URL.
        StringRequest stringRequest =
            new StringRequest(Request.Method.PATCH, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i(TAG, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("That didn't work!",error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                JSONObject loc = new JSONObject();

                try {
                    loc.put("lat", location.getLatitude());
                    loc.put("lng", location.getLongitude());
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                    return params;
                }

                params.put("location", loc.toString());
                return params;
            }
        };

        queue.add(stringRequest);
    }

    public Location getLastLoc() {
        return this.lastLoc;
    }


}


