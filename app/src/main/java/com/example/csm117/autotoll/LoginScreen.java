package com.example.csm117.autotoll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

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
                //TODO: Add new activity to go to (the dashboard)
            }
        });
    }
}
