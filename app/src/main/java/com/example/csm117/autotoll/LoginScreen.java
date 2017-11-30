package com.example.csm117.autotoll;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginScreen extends AppCompatActivity {

    // Initialize variables
    EditText inputUsername;
    EditText inputPassword;

    @Override
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
                //TODO: Change going back to MainActivity to the dashboard activity
                // starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);

                // obtain inputs
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                int valid = 1; // check if login is valid or not

                String errMsg = ""; // error message from server side

                // all fields are mandatory, so check if user left out any input
                if(username.length() == 0 || password.length() == 0) {
                    valid = 0;
                    errMsg = "All fields must be filled out";
                }

                // TODO: Add database logic here


                // switch activity
                if(valid == 1)
                    startActivity(nextScreen);
                else { // display the error message
                    banner2.setText(errMsg);
                    banner2.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }
        });
    }
}
