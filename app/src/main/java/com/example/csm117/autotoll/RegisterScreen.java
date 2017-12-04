package com.example.csm117.autotoll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.String;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterScreen extends AppCompatActivity {

    // Initialize variables
    EditText inputNFC;
    EditText inputUsername;
    EditText inputPassword;
    int valid; // check if registration is valid or not
    String errMsg; // error message from server side
    RequestQueue queue;
    Intent nextScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);
        queue = Volley.newRequestQueue(this);
        inputNFC = (EditText) findViewById(R.id.input_reg_nfc);
        inputUsername = (EditText) findViewById(R.id.input_reg_username);
        inputPassword = (EditText) findViewById(R.id.input_reg_password);
        final TextView banner = (TextView) findViewById(R.id.banner_reg);

        // cancel button functionality: go back to main screen
        final Button button_cancel = findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Starting a new Intent
                nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(nextScreen);
            }
        });

        // register button functionality: go back to main screen, consult database on saved inputs
        final Button button_register = findViewById(R.id.button_register2);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // starting a new Intent
                nextScreen = new Intent(getApplicationContext(), MainActivity.class);

                // obtain inputs
                String nfc = inputNFC.getText().toString();
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                valid = 1;
                errMsg = "";

                // all fields are mandatory, so check if user left out any input
                if(username.length() == 0 || password.length() == 0) {
                    valid = 0;
                    errMsg = "All fields must be filled out";
                }
                // make sure NFC sticker is 4-byte hexadecimal (8 characters)
                // logic of valid hexadecimal stickers handled server-side
                else if(nfc.length() != 14) {
                    valid = 0;
                    errMsg = "NFC sticker must be an 14-character (7-byte) hexadecimal";
                }
                //response with above err to save the need to consult server
                if(valid == 0){
                    banner.setText(errMsg);
                    banner.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                else {
                    // Consult server
                    String url = "http://ec2-13-59-86-172.us-east-2.compute.amazonaws.com:3000/register";
                    JSONObject reqContent = new JSONObject();
                    try {
                        reqContent.put("NFC_ID", nfc);
                        reqContent.put("username", username);
                        reqContent.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, reqContent, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.get("status").toString();
                                String info = response.get("info").toString();
                                Log.d("status", status);
                                if (status.equals("Failure")) {// display the error message
                                    valid = 0;
                                    errMsg = info;
                                    banner.setText(errMsg);
                                    banner.setBackgroundColor(Color.parseColor("#FF0000"));
                                }
                                // switch activity
                                else{
                                    nextScreen.putExtra("registration_success", valid);
                                    startActivity(nextScreen);
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

                    queue.add(jsObjRequest);//send the request
                }
            }
        });
    }
}
