package com.example.csm117.autotoll;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginScreen extends AppCompatActivity {

    // Initialize variables
    EditText inputUsername;
    EditText inputPassword;
    int valid;
    String errMsg;
    String user_name;
    String user_balance;
    RequestQueue queue = Volley.newRequestQueue(this);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        inputUsername = (EditText) findViewById(R.id.input_log_username);
        inputPassword = (EditText) findViewById(R.id.input_log_password);
        final TextView banner2 = (TextView) findViewById(R.id.banner_log);

        // cancel button functionality: go back to main screen
        final Button button_cancel = findViewById(R.id.button_cancel2);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(nextScreen);
            }
        });

        // login button functionality: save inputs and consult database
        final Button button_log = findViewById(R.id.button_login2);
        button_log.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), AccountInfoScreen.class);

                // obtain inputs
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                valid = 1; // check if login is valid or not

                errMsg = ""; // error message from server side

                // all fields are mandatory, so check if user left out any input
                if(username.length() == 0 || password.length() == 0) {
                    valid = 0;
                    errMsg = "All fields must be filled out";
                }

                // Consult server
                String url = "http://localhost:3000/login";
                JSONObject reqContent = new JSONObject();
                try {
                    reqContent.put("username",username);
                    reqContent.put("password",password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, reqContent, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.get("status").toString();
                            if(status == "Failure"){
                                valid = 0;
                                errMsg = response.get("info").toString();
                            }
                            else{
                                user_name = response.get("user").toString();
                                user_balance = response.get("balance").toString();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(jsObjRequest);

                // switch activity, if authentication is successful
                if(valid == 1) {
                    nextScreen.putExtra("username", username);
                    startActivity(nextScreen);
                }
                else { // display the error message
                    banner2.setText(errMsg);
                    banner2.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }
        });
    }
}
