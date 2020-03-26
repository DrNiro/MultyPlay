package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class GetLocation extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Button test_BTN_getLocation;
    private TextView test_TXT_location;

    private MyLocationFinder myLocationFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        myLocationFinder = new MyLocationFinder(this);
//        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        test_TXT_location = findViewById(R.id.test_TXT_loaction);
        test_BTN_getLocation = findViewById(R.id.test_BTN_getLocation);
        test_BTN_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myLocationFinder.getLocation();
                Location location = myLocationFinder.getMyLocation();


                if(location != null) {
                    test_TXT_location.setText("( " + location.getLongitude() + " , " + location.getLatitude() + " )");
                } else {
                    test_TXT_location.setText(Constants.MSG_LOCATION_NOT_FOUND);
                    if(!isGpsAvailable())
                        askToTurnOnGps();
                }

//                if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    return;
//                }
//
//                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(GetLocation.this, new OnSuccessListener<Location>() {
//                    @Override
//                    public void onSuccess(Location location) {
//                        if(location != null) {
//                            test_TXT_location.setText("( " + location.getLongitude() + " , " + location.getLatitude() + " )");
//                        } else {
//                            test_TXT_location.setText(Constants.MSG_LOCATION_NOT_FOUND);
//                            if(!isGpsAvailable())
//                                askToTurnOnGps();
//                        }
//                    }
//                });
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
//        myLocationFinder.getLocation();
    }

    public boolean isNetworkAvailable() {
        LocationManager lm = (LocationManager)this.getSystemService(this.LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        } catch(Exception ex) {}

        return network_enabled;
    }

    public boolean isGpsAvailable() {
        LocationManager lm = (LocationManager)this.getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled;
    }

    public void askToTurnOnGps() {
        new AlertDialog.Builder(this)
                .setMessage("Cannot perform search while GPS network not enabled.\nPlease turn GPS on and try again.\nIf already turned GPD on, please wait a minute (or few) for a connection")
                .setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel ",null)
                .show();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
    }
}
