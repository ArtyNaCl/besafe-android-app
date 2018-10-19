package com.example.bgautier.besafe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
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

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_user);
        // Set up the login form.




        Button register_button = (Button) findViewById(R.id.register_button);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText login2 = (EditText) findViewById (R.id.phone_user);
                String login = login2.getText().toString();
                EditText MDP2 = (EditText) findViewById (R.id.mdp_user);
                String mdp = MDP2.getText().toString();
                EditText prenom2 = (EditText) findViewById (R.id.first_name_user);
                String prenom = prenom2.getText().toString();
                EditText nom2 = (EditText) findViewById (R.id.last_name_user);
                String nom = nom2.getText().toString();
                callApiRegister(login , mdp,prenom,nom);
            }
        });


    }

    public void callApiRegister( final String login , final String mdp ,final String prenom , final String nom){

        RequestQueue queue = Volley.newRequestQueue(this);
        String trueUrl ="http://hdaroit.fr:3000/api/appusers";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, trueUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("toto","");

                        startIntent();

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
                params.put("phone", login);
                params.put("password", mdp);
                params.put("firstname", prenom);
                params.put("lastname", nom);

                return params;
            }
        };


// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }


    public void startIntent( ){
        Log.d("toto","");
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(intent,0);
    }



}
