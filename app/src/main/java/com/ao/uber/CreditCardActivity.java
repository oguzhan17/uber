package com.ao.uber;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CreditCardActivity extends AppCompatActivity {

    String kms = "10";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        Button buttonBack = findViewById(R.id.buttonBack);
        Button buttonAddCard = (Button) findViewById(R.id.buttonAddCard);

        if( getIntent().getExtras() != null) {
            Intent intentReceived = getIntent();
            Bundle data = intentReceived.getExtras();
             kms = data.getString("kms");
        }



        buttonAddCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intnt = new Intent(getApplicationContext(), RideActivity.class);
                Bundle data1 = new Bundle();
                data1.putString("kms",kms);
                intnt.putExtras(data1);
                startActivity(intnt);

            }
        });

        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intnt = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intnt);
            }
        });
    }
}