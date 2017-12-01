package com.example.csm117.autotoll;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AccountInfoScreen extends AppCompatActivity {

    // Initialize variables
    EditText inputDeposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit_screen);

        Intent i = getIntent();

        inputDeposit = (EditText) findViewById(R.id.input_log_amount);
        final TextView banner3 = (TextView) findViewById(R.id.banner_deposit);
        //final TextView userBalance = (TextView) findViewById(R.id.current_balance);

        String username = i.getStringExtra("username");
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
        final TextView stopDeposit = (TextView) findViewById(R.id.current_balance);
        // deposit button functionality: deposit money into account
        final Button button_deposit = findViewById(R.id.button_deposit);
        button_deposit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //TODO: Change going back to MainActivity to the dashboard activity
                // starting a new Intent
                Intent nextScreen = new Intent(getApplicationContext(), AccountInfoScreen.class);
                TextView userBalance = (TextView) findViewById(R.id.current_balance);
                int initial = Integer.parseInt(stopDeposit.getText().toString());
                userBalance.setText(stopDeposit.getText().toString());
                // obtain inputs
                int valid = 1; // check if login is valid or not
                String strDeposit = inputDeposit.getText().toString();
                int deposit;
                if (strDeposit.length() == 0) {
                    deposit = -1;
                }
                else {
                    deposit = Integer.parseInt(strDeposit);
                }

                String errMsg = ""; // error message from server side

                // all fields are mandatory, so check if user left out any input
                if(deposit == 0) {
                    valid = 0;
                    errMsg = "Deposit must be positive";
                }
                else if(deposit == -1){
                    valid = 0;
                    errMsg = "Deposit cannot be empty";
                }
                else{
                    initial = initial + deposit;
                    userBalance.setText("" + initial);
                }
                // TODO: Add database logic here (read and write database, update balance, etc


                // switch activity
                if(valid == 1) {
                    banner3.setText("Deposit successful");
                    banner3.setBackgroundColor(Color.parseColor("#00FF00"));
                }
                else { // display the error message
                    banner3.setText(errMsg);
                    banner3.setBackgroundColor(Color.parseColor("#FF0000"));
                }
            }
        });


    }
}