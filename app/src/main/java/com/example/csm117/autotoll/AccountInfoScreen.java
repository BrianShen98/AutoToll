package com.example.csm117.autotoll;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountInfoScreen extends AppCompatActivity {

    // Initialize variables
    EditText inputDeposit;
    String username;
    String init_balance;
    int curr_balance;
    int deposit;
    int valid;
    String errMsg;
    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_screen);

        Intent i = getIntent();
        queue = Volley.newRequestQueue(this);
        inputDeposit = (EditText) findViewById(R.id.input_log_amount);
        final TextView banner3 = (TextView) findViewById(R.id.banner_deposit);
        final TextView userBalance = (TextView) findViewById(R.id.current_balance);

        username = i.getStringExtra("username");
        init_balance = i.getStringExtra("balance");
        curr_balance = Integer.parseInt(init_balance);
        final TextView greeting = (TextView) findViewById(R.id.text_log_username);
        greeting.setText("Hello, " + username);


        // cancel button functionality: go back to main screen
        final Button button_cancel = findViewById(R.id.button_cancel3);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(nextScreen);
            }
        });
        userBalance.setText(init_balance);


        // deposit button functionality: deposit money into account
        final Button button_deposit = findViewById(R.id.button_deposit);
        button_deposit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                errMsg = ""; // error message from server side
                valid = 1; // check if login is valid or not

                //obtain input
                String strDeposit = inputDeposit.getText().toString();
                if (strDeposit.length() == 0) {
                    valid = 0;
                    errMsg = "Deposit cannot be empty";
                }
                else if(Integer.parseInt(strDeposit) <= 0){
                    valid = 0;
                    errMsg = "Deposit must be positive";
                }

                //display error messages
                if(valid == 0){
                    banner3.setText(errMsg);
                    banner3.setBackgroundColor(Color.parseColor("#FF0000"));
                }
                //update the balance and consult the server
                else {
                    deposit = Integer.parseInt(strDeposit);
                    // TODO: Add database logic here (read and write database, update balance, etc
                    String url = "http://ec2-13-59-86-172.us-east-2.compute.amazonaws.com:3000/deposit";
                    JSONObject reqContent = new JSONObject();
                    try {
                        reqContent.put("username", username);
                        reqContent.put("amount", strDeposit);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, url, reqContent, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String status = response.get("status").toString();
                                String info = response.get("info").toString();
                                if (status.equals("Failure")) {
                                    valid = 0;
                                    errMsg = info;
                                    banner3.setText(errMsg);
                                    banner3.setBackgroundColor(Color.parseColor("#FF0000"));
                                }
                                // switch activity
                                else {
                                    curr_balance += deposit;
                                    userBalance.setText("" + curr_balance);
                                    banner3.setText("Deposit successful");
                                    banner3.setBackgroundColor(Color.parseColor("#00FF00"));
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


        // refresh button functionality: update account balance if necessary
        final ImageButton button_refresh = findViewById(R.id.refresh_button);
        button_refresh.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // consult the server to update the balance
                errMsg = ""; // error message from server side
                valid = 1; // check if login is valid or not
                String url = "http://ec2-13-59-86-172.us-east-2.compute.amazonaws.com:3000/deposit";
                final JSONObject reqContent = new JSONObject();

                JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, reqContent, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String status = response.get("status").toString();

                            if (status.equals("Failure")) {
                                valid = 0;
                                errMsg = response.get("info").toString();
                                banner3.setText(errMsg);
                                banner3.setBackgroundColor(Color.parseColor("#FF0000"));
                            }
                            else {
                                curr_balance = Integer.parseInt((String)reqContent.get("amount")); // get the current balance from database
                                userBalance.setText("" + curr_balance);
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
        });

    }
}
