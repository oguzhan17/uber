package com.ao.uber;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.ao.uber.Modules.DirectionFinder;
import com.ao.uber.Modules.DirectionFinderListener;
import com.ao.uber.Modules.Route;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.SyncFailedException;
import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, DirectionFinderListener {

    int counter = 0;
    private GoogleMap mMap;
    private Button mapBack,buttonSendRequest;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarker = new ArrayList<>();
    private List<Polyline> polyLinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    String kms;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mapBack = findViewById(R.id.mapBack);
        buttonSendRequest = findViewById(R.id.buttonSendRequest);

        sendRequest();



        mapBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendIntent();
            }
        });



    }



    private void sendIntent() {
        Intent intent = new Intent(MapsActivity.this, MapsSidebarActivity.class);
        startActivity(intent);
    }

    private void sendRequest() {


        Intent intentReceived = getIntent();
        Bundle data = intentReceived.getExtras();
        if( getIntent().getExtras() != null){
        String origin = data.getString("origin");
        String destination = data.getString("destination");


        if (origin == null) {
            Toast.makeText(this, "Please enter origin!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination == null) {
            Toast.makeText(this, "Please enter destination!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }}

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        int reqCode = 1;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, reqCode);
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait", "Finding direction", true);
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }
        if (destinationMarker != null) {
            for (Marker marker : destinationMarker) {
                marker.remove();
            }
        }
        if (polyLinePaths != null) {
            for (Polyline polylinePath : polyLinePaths) {
                polylinePath.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polyLinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarker = new ArrayList<>();


        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView) findViewById(R.id.textViewDistance)).setText(route.distance.text);
            ((TextView) findViewById(R.id.textViewTime)).setText(route.duration.text);
            String kmtext = route.distance.text;
            String replaceString=kmtext.replaceAll("km","");
            double km=Double.parseDouble(replaceString.trim());
            double kmm = km/4;
            kms = String.valueOf(kmm);
            ((TextView) findViewById(R.id.tvPrice)).setText("$" +kms);




            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));

            destinationMarker.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.GREEN)
                    .width(10);


            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }

            polyLinePaths.add(mMap.addPolyline(polylineOptions));
        }
        buttonSendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intnt = new Intent(getApplicationContext(), CreditCardActivity.class);
                Bundle data1 = new Bundle();
                data1.putString("kms",kms);
                intnt.putExtras(data1);
                startActivity(intnt);
            }
        });



    }
}