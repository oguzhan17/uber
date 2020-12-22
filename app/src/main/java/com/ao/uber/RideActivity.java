package com.ao.uber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RideActivity extends AppCompatActivity {


    private Button rideBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        rideBack = findViewById(R.id.rideBack);

        if( getIntent().getExtras() != null) {
            Intent intentReceived = getIntent();
            Bundle data = intentReceived.getExtras();
            String kms = data.getString("kms");

            ((TextView) findViewById(R.id.tvPrice)).setText("$" + kms);
        }

        rideBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent();
            }
        });
    }

    private void sendIntent() {
        Intent intnt = new Intent(getApplicationContext(), CreditCardActivity.class);
        startActivity(intnt);
        }
    }
