package com.example.multyplay;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class TestLocation extends AppCompatActivity {

    private Button test_BTN_getLocation;
    private TextView test_TXT_location;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        initiateLocationRequests();

        test_TXT_location = findViewById(R.id.test_TXT_loaction);
        test_BTN_getLocation = findViewById(R.id.test_BTN_getLocation);
        test_BTN_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("vvv", "No permissions");
                    requestPermission();
//                    return;
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(TestLocation.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("vvv", "in onSuccess");
                        if (location != null) {
                            Log.d("vvv", "location received");
                            test_TXT_location.setText("( " + location.getLongitude() + " , " + location.getLatitude() + " )");
                            if(locationManager != null)
                                locationManager.removeUpdates(listener);
                        } else {
                            Log.d("vvv", "location == null");
                            test_TXT_location.setText("Problem");
                            if (!isGpsAvailable())
                                askToTurnOnGps();
                            initiateLocationRequests();
                        }
                    }
                });
            }
        });
    }

    private void initiateLocationRequests() {
        Log.d("vvv", "calling location service");
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission();
        } else {
            Log.d("vvv", "initiate location listener");
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions( TestLocation.this, new String[] { ACCESS_FINE_LOCATION }, Constants.PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions( TestLocation.this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION }, Constants.PERMISSION_REQUEST_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    Toast.makeText(this, "Location Permission granted!", Toast.LENGTH_SHORT).show();
                } else {
                    if (grantResults.length > 0)
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private LocationListener listener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d("vvv", "good");
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("vvv", "Status changed.");
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("vvv", "Provider enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("vvv", "Provider DISabled");
        }
    };

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



}
