package com.example.csm117.autotoll;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.lang.String;

public class RegisterScreen extends AppCompatActivity {

    // Initialize variables
    EditText inputNFC;
    EditText inputUsername;
    EditText inputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        inputNFC = (EditText) findViewById(R.id.input_reg_nfc);
        inputUsername = (EditText) findViewById(R.id.input_reg_username);
        inputPassword = (EditText) findViewById(R.id.input_reg_password);
        final TextView banner = (TextView) findViewById(R.id.banner_reg);

        // cancel button functionality: go back to main screen
        final Button button_cancel = findViewById(R.id.button_cancel);
        button_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(nextScreen);
            }
        });

        // register button functionality: go back to main screen, consult database on saved inputs
        final Button button_register = findViewById(R.id.button_register2);
        button_register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), MainActivity.class);

                // obtain inputs
                String nfc = inputNFC.getText().toString();
                String username = inputUsername.getText().toString();
                String password = inputPassword.getText().toString();
                int valid = 1; // check if registration is valid or not

                // TODO: Add database logic here
                String errMsg = ""; // error message from server side

                // switch activity
                nextScreen.putExtra("registration_success", valid);
                if(valid == 1)
                    startActivity(nextScreen);
                else { // display the error message
                    banner.setText(errMsg);
                    banner.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }
        });
    }
}
