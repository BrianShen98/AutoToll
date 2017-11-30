package com.example.csm117.autotoll;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // register button functionality
        final Button button_reg = (Button)findViewById(R.id.button_register);
        button_reg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), RegisterScreen.class);
                startActivity(nextScreen);
            }
        });

        // login button functionality
        final Button button_log = findViewById(R.id.button_login);
        button_log.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(nextScreen);
            }
        });


        // Logic here primarily from when the user attempts to register a new account
        // Pop up registration success or failure
        Intent i = getIntent();

        // indicate 2 as we have not visited register and not registered yet
        int reg_success = i.getIntExtra("registration_success", 2);
        final TextView banner = (TextView)findViewById(R.id.banner);
//        if(reg_success == 0) { // registration failure
//            banner.setBackgroundColor(Color.parseColor("#FF0000"));
//            banner.setText("Registration failed. Please try again.");
//        }
        if(reg_success == 1) { // registration success
            banner.setBackgroundColor(Color.parseColor("#00FF00"));
            banner.setText("Registration successful!");
        }
        else if(reg_success == 2) { // default case
            banner.setBackgroundColor(Color.parseColor("#FFFFFF"));
            banner.setText("");
        }

        banner.postDelayed(new Runnable() {
            @Override
            public void run() {
                banner.setBackgroundColor(Color.parseColor("#FFFFFF"));
                banner.setText("");
            }
        }, 5000);
    }


}
