package com.example.multyplay;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MyLocationFinder extends Activity {

    private FusedLocationProviderClient fusedLocationProviderClient;

    private Context context;
    private Location myLocation;

    private LocationManager locationManager;
    private LocationListener listener;
    private String lastLoc;

    public MyLocationFinder(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this.context);
        lastLoc = "";
        myLocation = null;
    }

    public void getLocation() {

        if (this.context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && this.context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "No permissions", Toast.LENGTH_SHORT).show();
            requestPermission();
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    myLocation = location;
                } else {
                    if (!isGpsAvailable()) {
                        askToTurnOnGps();
                    }
                    myLocation = null;
                }
            }
        });

//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                lastLoc = "( " + location.getLongitude() + " , " + location.getLatitude() + " )";
                Log.d("vvv", "lastLoc: " + lastLoc);
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
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
    }

    public void stopListener() {
        if(!lastLoc.equals("")){
            locationManager.removeUpdates(listener);
        }
    }

    public Location getMyLocation() {
        return myLocation;
    }

    public boolean isNetworkAvailable() {
        LocationManager lm = (LocationManager)this.context.getSystemService(this.context.LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        } catch(Exception ex) {}

        return network_enabled;
    }

    public boolean isGpsAvailable() {
        LocationManager lm = (LocationManager)this.context.getSystemService(this.context.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled;
    }

    public void askToTurnOnGps() {
        new AlertDialog.Builder(this.context)
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
