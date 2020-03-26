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
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.jaygoo.widget.RangeSeekBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SearchFriendsFragment extends Fragment {

    private View view = null;
    private CallBackEnterOtherAccount callBackEnterOtherAccount;
    private CallBackApproved callBackLocation;

    private RecyclerView searchResultListRecyclerView;
    private ProfileSearchResAdapter profileSearchResAdapter;
    private ArrayList<Account> accountsListTest;

    private ImageView search_BTN_GPS;
    private RelativeLayout search_LAY_criteria;
    private ImageView search_BTN_filters;
    private boolean filtersOpen;
    private RelativeLayout search_LAY_searchFilters;
    private TextView seachFilters_BTN_done;

    private SeekBar searchFilters_SKB_distanceBar;
    private TextView searchFilters_TXT_actualLocation;
    private RangeSeekBar searchFilters_SKB_minMaxAge;
    private TextView searchFilters_TXT_actualAgeRange;

    private MySharedPreferences prefs;
    private String jsAccount;
    private Account myAccount;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private double latitude, longitude;
    private String locationStr;
    private Location myLocation;
    private MyLocation updatedLocation;
    private HashMap<String, Double> distancesMap;

    private SearchFilters mySearchFilters;
    private String jsSearchFilters;

    // TODO: 3/16/2020 change how accounts are saved in firebase, maybe by country
    private ArrayList<Account> allAccounts;
    private ArrayList<Account> filteredAccounts;

    private boolean finishedSearch;


    public void setCallBackEnterOtherAccount(CallBackEnterOtherAccount callback) {
        this.callBackEnterOtherAccount = callback;
    }

    public void setCallBackLocation(CallBackApproved callBackLocation) {
        this.callBackLocation = callBackLocation;
    }
    public void setMyLocation(Location location) {
        this.myLocation = location;
    }
    public Location getMyLocation() {
        return this.myLocation;
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
        distancesMap = new HashMap<>();

        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        myAccount = new Gson().fromJson(jsAccount, Account.class);

        finishedSearch = false;
        jsSearchFilters = prefs.getString(Constants.PREFS_KEY_SEARCH_FILTERS, "");
        mySearchFilters = new Gson().fromJson(jsSearchFilters, SearchFilters.class);

        accountsListTest = new ArrayList<>();

        filtersOpen = false;
        myLocation = null;

        search_BTN_filters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finishedSearch) {
                    resetSearch();
                }
                if(filtersOpen) {
                    search_LAY_searchFilters.setVisibility(View.GONE);
                    mySearchFilters.setMaxDistance(Integer.parseInt(searchFilters_TXT_actualLocation.getText().toString().split(" ")[0]));
                    mySearchFilters.setMinAge(Integer.parseInt(searchFilters_TXT_actualAgeRange.getText().toString().split(" ")[0]));
                    mySearchFilters.setMaxAge(Integer.parseInt(searchFilters_TXT_actualAgeRange.getText().toString().split(" ")[2]));
                    jsSearchFilters = new Gson().toJson(mySearchFilters);
                    prefs.putString(Constants.PREFS_KEY_SEARCH_FILTERS, jsSearchFilters);
                } else {
                    search_LAY_searchFilters.setVisibility(View.VISIBLE);
                }
                filtersOpen = !filtersOpen;
            }
        });

        seachFilters_BTN_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_BTN_filters.callOnClick();
            }
        });

        searchFilters_SKB_distanceBar.setOnSeekBarChangeListener(setDistanceChangeListener);

        searchFilters_SKB_minMaxAge.setValue(mySearchFilters.getMinAge(), mySearchFilters.getMaxAge());
        final String showRange = mySearchFilters.getMinAge() + " - " + mySearchFilters.getMaxAge();
        searchFilters_TXT_actualAgeRange.setText(showRange);
        searchFilters_SKB_minMaxAge.setOnRangeChangedListener(new RangeSeekBar.OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float min, float max, boolean isFromUser) {
                String range = (int)min + " - " + (int)max;
                searchFilters_TXT_actualAgeRange.setText(range);
            }
        });

//        This button start search with all filters, not only gps.
//        Gps influence only when account have location.
        search_BTN_GPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                myLocation = null;
                String jsAcc = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
                myAccount = new Gson().fromJson(jsAcc, Account.class);

                callBackLocation.onOkClick();

            }
        });

        createProfileResultRecycler();

        return view;
    }

    public void goSearchGO(Location location) {
        setMyLocation(location);
        if (myLocation != null) {
            Log.d("nnn", "Check location found, proceed to results");
            setProfilesLocation(myLocation);
            MyFirebase.getAccounts(new CallBackAccountsReady() {
                @Override
                public void accountsReady(ArrayList<Account> accounts) {
                    allAccounts = accounts;
                    searchByGps(mySearchFilters.getMaxDistance());
                    searchByAge(mySearchFilters.getMinAge(), mySearchFilters.getMaxAge());

                    searchResultListRecyclerView.setVisibility(View.VISIBLE);
                    search_LAY_criteria.setVisibility(View.GONE);
                    showResult(distancesMap);
                    finishedSearch = true;
                }

                @Override
                public void error() {
                }
            });
        } else {
            Log.d("nnn", "Check location == null, showing please wait");
            if(isGpsAvailable()) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getContext())
                        .setTitle("Message")
                        .setMessage("\nLooking for location!\n\nPlease wait and try again in a few seconds.\n")
                        .setPositiveButton("OK", null);
                alertDialog.show();
            }
        }
    }

    public void resetSearch() {
        searchResultListRecyclerView.setVisibility(View.GONE);
        search_LAY_criteria.setVisibility(View.VISIBLE);
    }

    private void searchByAge(int minAge, int maxAge) {
        ArrayList<Account> filteredAccounts2 = new ArrayList<>();
        filteredAccounts2.addAll(filteredAccounts);
        for(Account acc : filteredAccounts2) {
            if(minAge > acc.getAge() || acc.getAge() > maxAge) {
                filteredAccounts.remove(acc);
            }
        }
    }

    private SeekBar.OnSeekBarChangeListener setDistanceChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
            String newDistance = progress + Constants.MIN_SEARCH_DISTANCE + " km";
            searchFilters_TXT_actualLocation.setText(newDistance);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    public boolean isFinishedSearch() {
        return finishedSearch;
    }

    @Override
    public void onPause() {
        super.onPause();
        searchResultListRecyclerView.setVisibility(View.GONE);
        search_BTN_GPS.setVisibility(View.VISIBLE);
    }

    private void searchByGps(double radius) {
        filteredAccounts = new ArrayList<>();
        for(Account acc : allAccounts) {
            if(acc.getMyLocation() != null) {
                if(!acc.getMyLocation().getAddress().equals(Constants.MSG_LOCATION_NOT_FOUND)) {
                    double distance = distanceInKm(myAccount.getMyLocation().getLatitude(), myAccount.getMyLocation().getLongitute(), acc.getMyLocation().getLatitude(), acc.getMyLocation().getLongitute());
                    Log.d("www", "searchByGps() distance: " + distance + " " + acc.getNickName());
                    if(distance <= radius && !acc.getEmail().toString().equalsIgnoreCase(myAccount.getEmail().toString()) && acc.isLocationAllowed()) {
                        filteredAccounts.add(acc);
                        distancesMap.put(acc.getEmail().toString(), distance);
                    }
                }
            }
        }
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

    private void showResult() {
        Log.d("ggg", "here?");
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
        seachFilters_BTN_done = view.findViewById(R.id.seachFilters_BTN_done);
        search_BTN_GPS = view.findViewById(R.id.search_BTN_GPS);
        search_LAY_criteria = view.findViewById(R.id.search_LAY_criteria);
        search_BTN_filters = view.findViewById(R.id.search_BTN_filters);
        search_LAY_searchFilters= view.findViewById(R.id.search_LAY_searchFilters);
        searchFilters_TXT_actualLocation = view.findViewById(R.id.searchFilters_TXT_actualLocation);
        searchFilters_SKB_distanceBar = view.findViewById(R.id.searchFilters_SKB_distanceBar);
        searchFilters_SKB_minMaxAge = view.findViewById(R.id.searchFilters_SKB_minMaxAge);
        searchFilters_TXT_actualAgeRange = view.findViewById(R.id.searchFilters_TXT_actualAgeRange);
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
//        dist = Double.parseDouble(new DecimalFormat("##.####").format(dist));
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

    private void setProfilesLocation(Location location) {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        updatedLocation = new MyLocation(longitude, latitude, applyGcoder(latitude, longitude));
//                    update Profiles location.
        String jsAcc = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        Account acc = new Gson().fromJson(jsAcc, Account.class);
        acc.setMyLocation(updatedLocation);
        jsAcc = new Gson().toJson(acc);
        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAcc);
//        acc.setOnline(true);
        MyFirebase.setAccount(acc);
        myAccount = acc;
    }

    private String applyGcoder(double latitude, double longitude) {
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

    public boolean isGpsAvailable() {
        LocationManager lm = (LocationManager) view.getContext().getSystemService(view.getContext().LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled;
    }

}
