package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SearchFriendsFragment extends Fragment {

    private View view = null;
    private CallBackEnterOtherAccount callBackEnterOtherAccount;

    private RecyclerView searchResultListRecyclerView;
    private ProfileSearchResAdapter profileSearchResAdapter;
    private ArrayList<Account> accountsListTest;

    private ImageView search_BTN_GPS;
    private RelativeLayout search_LAY_criteria;

    private MySharedPreferences prefs;
    private String jsAccount;
    private Account myAccount;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_LOCATION_CODE = 1000;
    private Location currentLocation;
    private double latitude, longitude;
    private String locationStr;
    private MyLocation updatedLocation;

    // TODO: 3/16/2020 change how accounts are saved in firebase, maybe by country
    private ArrayList<Account> allAccounts;
    private ArrayList<Account> filteredAccounts;


    public void setCallBackEnterOtherAccount(CallBackEnterOtherAccount callback) {
        this.callBackEnterOtherAccount = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.activity_search_friends_fragment, container, false);
        }

        findViews();

        prefs = new MySharedPreferences(view.getContext());
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        myAccount = new Gson().fromJson(jsAccount, Account.class);

        accountsListTest = new ArrayList<>();

//        searchResultListRecyclerView = view.findViewById(R.id.search_RCL_searchResults);
//        searchResultListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
//        profileSearchResAdapter = new ProfileSearchResAdapter(view.getContext(), accountsListTest);
//        profileSearchResAdapter.setClickListener(profileItemClickedListener);
//        searchResultListRecyclerView.setAdapter(profileSearchResAdapter);

        search_BTN_GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jsAcc = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
                myAccount = new Gson().fromJson(jsAcc, Account.class);
                if(myAccount.getMyLocation().getAddress().equals(Constants.MSG_LOCATION_NOT_FOUND)) {
                    askToTurnOnGps();
                    getLocation();
                    if (ActivityCompat.checkSelfPermission(
                            view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        Log.d("bbbRequestLocation", "Request Permission");
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
                    }
                    return;
                }

                if(allAccounts == null) {
                    MyFirebase.getAccounts(new CallBackAccountsReady() {
                        @Override
                        public void accountsReady(ArrayList<Account> accounts) {
                            allAccounts = accounts;
                            searchByGps(2000);
                        }

                        @Override
                        public void error() {
                        }
                    });
                } else {
                    searchByGps(2000);
                }

            }
        });

        createProfileResultRecycler();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        searchResultListRecyclerView.setVisibility(View.GONE);
        search_BTN_GPS.setVisibility(View.VISIBLE);
    }

    private void searchByGps(double radius) {
        filteredAccounts = new ArrayList<>();
        HashMap<String, Double> distancesMap = new HashMap<>();
        for(Account acc : allAccounts) {
            if(acc.getMyLocation() != null) {
                if(!acc.getMyLocation().getAddress().equals(Constants.MSG_LOCATION_NOT_FOUND)) {
                    double distance = distanceInKm(myAccount.getMyLocation().getLatitude(), myAccount.getMyLocation().getLongitute(), acc.getMyLocation().getLatitude(), acc.getMyLocation().getLongitute());
                    if(distance <= radius && !acc.getEmail().toString().equalsIgnoreCase(myAccount.getEmail().toString()) && acc.isLocationAllowed()) {
                        filteredAccounts.add(acc);
                        distancesMap.put(acc.getEmail().toString(), distance);
                    }
                }
            }
        }
        searchResultListRecyclerView.setVisibility(View.VISIBLE);
        search_LAY_criteria.setVisibility(View.GONE);
        showResult(distancesMap);
    }

    @Override
    public void onResume() {
        super.onResume();
        searchResultListRecyclerView.setVisibility(View.GONE);
        search_LAY_criteria.setVisibility(View.VISIBLE);
    }

    private void showResult(HashMap<String, Double> distancesMap) {
        searchResultListRecyclerView = view.findViewById(R.id.search_RCL_searchResults);
        searchResultListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        profileSearchResAdapter = new ProfileSearchResAdapter(view.getContext(), filteredAccounts);
        profileSearchResAdapter.setMap(distancesMap);
        profileSearchResAdapter.setClickListener(profileItemClickedListener);
        searchResultListRecyclerView.setAdapter(profileSearchResAdapter);
    }

    private void createProfileResultRecycler() {
        //      creating GameCards recyclerView
        searchResultListRecyclerView = view.findViewById(R.id.search_RCL_searchResults);
        searchResultListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        profileSearchResAdapter = new ProfileSearchResAdapter(view.getContext(), accountsListTest);
        profileSearchResAdapter.setClickListener(profileItemClickedListener);
        searchResultListRecyclerView.setAdapter(profileSearchResAdapter);
    }

    private void findViews() {
        search_BTN_GPS = view.findViewById(R.id.search_BTN_GPS);
        search_LAY_criteria = view.findViewById(R.id.search_LAY_criteria);
    }

    private ProfileSearchResAdapter.ItemClickListener profileItemClickedListener = new ProfileSearchResAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.d("vvvResClicked", "Clicked a profile.");
            Account visitAccount = profileSearchResAdapter.getItem(position);
            callBackEnterOtherAccount.clickedOnOtherProfile(visitAccount);
        }
    };

    private double distanceInKm(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public boolean isGpsAvailable() {
        LocationManager lm = (LocationManager)view.getContext().getSystemService(view.getContext().LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        return gps_enabled;
    }

    public boolean isNetworkAvailable() {
        LocationManager lm = (LocationManager)view.getContext().getSystemService(view.getContext().LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        } catch(Exception ex) {}

        return network_enabled;
    }

    public void askToTurnOnGps() {
        new AlertDialog.Builder(view.getContext())
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

    private void getLocation() {
        //check location permissions
        if (ActivityCompat.checkSelfPermission(
                view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                view.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            return;
        }
        //set location
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                    updatedLocation = new MyLocation(longitude, latitude, setLocation(latitude, longitude));
//                    update Profiles location.
                    String jsAcc = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
                    Account acc = new Gson().fromJson(jsAcc, Account.class);
                    if (!acc.getMyLocation().getAddress().equals(Constants.MSG_LOCATION_NOT_FOUND) && updatedLocation.getAddress().equals(Constants.MSG_LOCATION_NOT_FOUND)) {
                        return;
                    } else {
                        acc.setMyLocation(updatedLocation);
                        jsAcc = new Gson().toJson(acc);
                        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAcc);
                        MyFirebase.setAccount(acc);
                        myAccount = acc;
                    }
                    Log.d("bbbLocationSearch", "onSuccess: " + latitude + " " + longitude);
                } else {
                    longitude = 1;
                    latitude = 1;
                    updatedLocation = new MyLocation(longitude, latitude, Constants.MSG_LOCATION_NOT_FOUND);
                    Log.d("bbbLocationSearch", "else (not success): " + latitude + " " + longitude);
                }
            }
        });
    }

    private String setLocation(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(view.getContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String knownName = addresses.get(0).getFeatureName();

                if (knownName != null) {
                    locationStr = knownName + " ," + city + ", " + country;
                } else {
                    if (address != null)
                        locationStr = address + " ," + city + ", " + country;
                }
                return locationStr;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Constants.MSG_LOCATION_NOT_FOUND;
    }

}
