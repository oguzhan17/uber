package com.ao.uber;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ao.uber.Modules.DirectionFinder;
import com.ao.uber.Modules.DirectionFinderListener;

public class MapsSidebarActivity extends AppCompatActivity {


    private Button btnFindPath;
    private EditText editTextOrigin;
    private EditText editTextDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_sidebar);


        btnFindPath = findViewById(R.id.buttonFindPath);
        editTextOrigin = findViewById(R.id.editTextOrigin);
        editTextDestination = findViewById(R.id.editTextDestination);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });
    }

    private void sendRequest() {
        String origin = editTextOrigin.getText().toString();
        String destination = editTextDestination.getText().toString();

        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent i = new Intent(getApplicationContext(), MapsActivity.class);
        Bundle data1 = new Bundle();
        data1.putString("origin",origin);
        data1.putString("destination",destination);
        i.putExtras(data1);
        startActivity(i);

    }
}