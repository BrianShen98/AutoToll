package com.example.csm117.autotoll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    }


}
