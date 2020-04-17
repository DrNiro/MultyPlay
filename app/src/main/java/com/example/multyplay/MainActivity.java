package com.example.multyplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.LocationListener;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Random;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class MainActivity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Button test_BTN_getLocation;
    private TextView test_TXT_location;
    Context context = this;

    LocationRequest mLocationRequest;
    Location mCurrentLocation;

    private static final int MILLISECONDS_PER_SECOND = 1000;

    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;

    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    String lastLoc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        test_TXT_location = findViewById(R.id.test_TXT_loaction);
        test_BTN_getLocation = findViewById(R.id.test_BTN_getLocation);
        test_BTN_getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("keykey", "in on click");
                if (checkSelfPermission(ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("keykey", "in if");
                    askToTurnOnGps();
//                    return;
                } else if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    Log.d("keykey", "in else if");
                    askToTurnOnGps();
                }
                if (ContextCompat.checkSelfPermission( context, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
                    ActivityCompat.requestPermissions( MainActivity.this, new String[] {  Manifest.permission.ACCESS_COARSE_LOCATION  },
                            getRequestCode() );
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        Log.d("keykey", "in onSuccess");
                        if(location != null) {
                            test_TXT_location.setText(lastLoc);
                        } else {
                            test_TXT_location.setText("Problem");
                            if(!isGpsAvailable())
                                askToTurnOnGps();
                        }
                    }
                });
//                mLocationRequest = LocationRequest.create();
//                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//                mLocationRequest.setInterval(UPDATE_INTERVAL);
//                mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//                mLocationClient = new LocationClient(this, this, this);
//
//                fusedLocationProviderClient.requestLocationUpdates().addOnSuccessListener()

            }
        });
        LocationManager locationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLoc = "( " + location.getLongitude() + " , " + location.getLatitude() + " )";
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

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
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    private int getRequestCode(){
        Random random = new Random();
        return random.nextInt(10000);
    }
}
