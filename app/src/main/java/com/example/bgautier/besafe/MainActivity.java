package com.example.bgautier.besafe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String CHANNEL_ID = "channel_id";
    Intent intent;
    GeolocListener geolocListener;
    SocketIO socketIO;
    String alertId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        String userId = getUserId();
        String token = getToken();
        geolocListener = new GeolocListener(this, userId , token);
        try {
            try {
                socketIO = new SocketIO("http://hdaroit.fr:3000", userId, token, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d("SocketIO", "Alert");
                        JSONObject alert = (JSONObject) args[0];
                        try {
                            onAlert(
                                    alert.getString("responseId"),
                                    alert.getString("appUserId"),
                                    alert.getString("msg"),
                                    alert.getDouble("distance"),
                                    alert.getString("address")
                            );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d("SocketIO", "Missed alerts");
                        sendNotification("Vous avez manqué " + String.valueOf((int) args[0]) + " alertes durant votre absence.");
                    }
                }, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.d("SocketIO", "New response.");
                        sendNotification(String.valueOf((int) args[0]) + " personnes ont répondu à votre alerte.");
                        TextView et1 = findViewById(R.id.textView4);
                        et1.setText(String.valueOf((int) args[0])+ " personnes viennent à votre secours !");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        Button alert_button = (Button) findViewById(R.id.alert_button);
        alert_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendAlert();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void sendNotification(String textContent){

        int  notificationId = 2;
        // set notification
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Alert de proximité")
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_MAX);
        createNotificationChannel();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, mBuilder.build());


    }


    //set notification channel for version <8.0.0
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void terminateAlert(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://hdaroit.fr:3000/api/appusers/" + getUserId() + "/alerts/" + alertId + "?access_token=" + getToken();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("ALERT", "Alert resolved : " + response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ALERT", error.toString());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("resolved", String.valueOf(true));
                return params;
            }
        };


// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void sendAlert( ){

        RequestQueue queue = Volley.newRequestQueue(this);
        String trueUrl ="http://hdaroit.fr:3000/api/appusers/"+getUserId()+"/alerts?access_token="+getToken();

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, trueUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.substring(0,500));
                        Log.d("ALERT", "Created : " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            alertId = jsonObject.getString("id");
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ALERT",error.toString());
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<String, String>();
                JSONObject loc = new JSONObject();
                Location location = geolocListener.getLastLoc();

                if (location == null) {
                    return params;
                }

                try {
                    loc.put("lat", location.getLatitude());
                    loc.put("lng", location.getLongitude());
                } catch (JSONException e) {
                    Log.e("toto", e.toString());
                    return params;
                }

                params.put("location", loc.toString());

                return params;
            }
        };


// Add the request to the RequestQueue.
        queue.add(stringRequest);
        setContentView(R.layout.alert_responder);
        Button alert_resolve = (Button) findViewById(R.id.finish_alert_button);
        alert_resolve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                terminateAlert();
            }
        });
    }

    public String getUserId(){

          String userId = intent.getStringExtra("userId");
            return userId;
    }

    public String getToken(){

        String token = intent.getStringExtra("id");
        return token;
    }

    private void onAlert(String responseId, String appUserId, String msg, double distance, String address) {
        sendNotification(msg);

    }
}
