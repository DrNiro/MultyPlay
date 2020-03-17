package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FragmentManager extends AppCompatActivity {

    private ProfileFragment profileFragment;
    private SearchFriendsFragment searchFriendsFragment;
    private ProfileSettingsFragment profileSettingsFragment;
    private ChatFragment chatFragment;
//    private ProPageFragment proPageFragment;
//    private FeedFragment feedFragment;

    private ChangeProfilePicFragment changeProfilePicFragment;
    private ChangeCoverPicFragment changeCoverPicFragment;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_LOCATION_CODE = 1000;
    private Location currentLocation;
    private double latitude, longitude;
    private String locationStr;
    private MyLocation updatedLocation;

    private LinearLayout main_LAY_toolbar;
    private ImageView main_BTN_profile;
    private ImageView main_BTN_search;
    private ImageView main_BTN_feed;
    private ImageView main_BTN_chat;
    private ImageView main_BTN_pro;

    private Account myAccount;
    private String jsAccount;
    private MySharedPreferences prefs;

    private boolean isOwnerProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_manager);

        isOwnerProfile = getIntent().getBooleanExtra(Constants.KEY_MARK_FOREIN_ACCOUNT, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        main fragments initialization
        profileFragment = new ProfileFragment();
        profileFragment.setCallback(btnClickInProfile);

        searchFriendsFragment = new SearchFriendsFragment();
        profileFragment.setIsOwner(isOwnerProfile);
        searchFriendsFragment.setCallBackEnterOtherAccount(visitAccountFromSearch);

//        temp fragments setup
        changeProfilePicFragment = new ChangeProfilePicFragment();
        changeProfilePicFragment.setCallback(approvedProfilePic);

        changeCoverPicFragment = new ChangeCoverPicFragment();
        changeCoverPicFragment.setCallback(approvedCoverPic);

        profileSettingsFragment = new ProfileSettingsFragment();
        profileSettingsFragment.setCallback(btnClickInSettings);

        chatFragment = new ChatFragment();
//        proPageFragment = new ProPageFragment();
//        feedFragment = new FeedFragment();

//        get myAccount json from login and save it on sharedPreferences, also create myAccount from json.
        prefs = new MySharedPreferences(this);
//        jsAccount = getIntent().getStringExtra(Constants.KEY_MY_ACCOUNT_JSON);
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
//        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
        myAccount = new Gson().fromJson(jsAccount, Account.class);

//        if(isOwnerProfile) {
//            myAccount.setOnline(true);
//            MyFirebase.updateOnline(myAccount, true);
//            Log.d("vvvWHATSATATW", "online: YES");
//        }

//        Log.d("vvvCheckLocation", "Location: " + myAccount.getMyLocation().getAddress());
        isOwnerProfile = getIntent().getBooleanExtra(Constants.KEY_MARK_FOREIN_ACCOUNT, false);
        profileFragment.setIsOwner(isOwnerProfile);

        Log.d("bbb", "manager, isOwnerProfile: " + isOwnerProfile);

//        findViews
        main_LAY_toolbar = findViewById(R.id.main_LAY_toolbar);
        main_BTN_profile = findViewById(R.id.main_BTN_profile);
        main_BTN_search = findViewById(R.id.main_BTN_search);
        main_BTN_feed = findViewById(R.id.main_BTN_feed);
        main_BTN_chat = findViewById(R.id.main_BTN_chat);
        main_BTN_pro = findViewById(R.id.main_BTN_pro);
//        main_BTN_home = findViewById(R.id.main_BTN_home);

        Log.d("vvvFragManager", "myAccount: " + jsAccount);

//        Initialize the first window to see after login.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_LAY_mainWindow, profileFragment);
        transaction.add(R.id.main_LAY_mainWindow, searchFriendsFragment);
        transaction.add(R.id.main_LAY_mainWindow, chatFragment);
//        transaction.add(R.id.main_LAY_mainWindow, proPageFragment);
//        transaction.add(R.id.main_LAY_mainWindow, feedFragment);
        transaction.add(R.id.main_LAY_mainWindow, changeProfilePicFragment);
        transaction.add(R.id.main_LAY_mainWindow, changeCoverPicFragment);
        transaction.show(profileFragment);
        transaction.hide(searchFriendsFragment);
        transaction.hide(chatFragment);
//        transaction.hide(proPageFragment);
//        transaction.hide(feedFragment);
        transaction.hide(changeProfilePicFragment);
        transaction.hide(changeCoverPicFragment);
        transaction.commit();

//        Toolbar buttons click listeners to navigate through app fragments.
        main_BTN_profile.setOnClickListener(profileClickListener);
        main_BTN_search.setOnClickListener(searchClickListener);
//        main_BTN_home.setOnClickListener(homeClickListener);

        main_BTN_chat.setOnClickListener(comingSoonBtnClickListener);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(isOwnerProfile) {
//            myAccount.setOnline(false);
//            MyFirebase.updateOnline(myAccount, false);
//        }
    }

    @Override
    protected void onStart() {
        isOwnerProfile = getIntent().getBooleanExtra(Constants.KEY_MARK_FOREIN_ACCOUNT, true);
        Log.d("bbbOnStartManager", "isOwner: " + isOwnerProfile);
//        updateAccountLocation();
        if (isOwnerProfile) {
            myAccount.setOnline(true);
            MyFirebase.updateOnline(myAccount, true);
            Log.d("vvvWHATSATATW", "online: YES");
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isOwnerProfile) {
            myAccount.setOnline(false);
            MyFirebase.updateOnline(myAccount, false);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if visiting another profile and hit the profile button, do it and go back to own profile.
        if (hasFocus && isOwnerProfile) {
            Log.d("vvvManager", "Owner: " + isOwnerProfile);
            main_BTN_profile.callOnClick();
        }
    }


    //    show profile fragment
    private View.OnClickListener profileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isOwnerProfile) {
                finish();
            }
            if (profileFragment.isHidden() && changeCoverPicFragment.isHidden() && changeProfilePicFragment.isHidden()) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(profileFragment);
                transaction.hide(searchFriendsFragment);
                transaction.hide(chatFragment);
//                transaction.hide(proPageFragment);
//                transaction.hide(feedFragment);
                transaction.commit();

            }
        }
    };

    //    show search fragment
    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isOwnerProfile) {
                finish();
            }
            if (searchFriendsFragment.isHidden()) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(profileFragment);
                transaction.show(searchFriendsFragment);
                transaction.commit();
            }
        }
    };

    private View.OnClickListener comingSoonBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isOwnerProfile) {
                finish();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//            chat
            if(view.getId() == R.id.main_BTN_chat) {
                transaction.hide(profileFragment);
                transaction.show(chatFragment);
                transaction.hide(searchFriendsFragment);
                transaction.commit();
            } else if(view.getId() == R.id.main_BTN_pro) {
                Toast.makeText(FragmentManager.this, "COMING SOON", Toast.LENGTH_SHORT).show();
            } else if(view.getId() == R.id.main_BTN_feed) {
                Toast.makeText(FragmentManager.this, "COMING SOON", Toast.LENGTH_SHORT).show();
            }
        }
    };


    //    callback triggers when clicking buttons in profile fragment.
    public CallBackClickedBtn btnClickInProfile = new CallBackClickedBtn() {
        @Override
        public void buttonClicked(View btn) {
//            clicked change profile picture in profile fragment
            if (btn.getId() == R.id.profile_IMG_changeProfilePicBtn) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(profileFragment);
                transaction.show(changeProfilePicFragment);
                main_LAY_toolbar.setVisibility(View.INVISIBLE);
                transaction.commit();
            }
//            clicked change cover picture in profile fragment
            else if (btn.getId() == R.id.profile_IMG_changecoverPicBtn) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(profileFragment);
                transaction.show(changeCoverPicFragment);
                main_LAY_toolbar.setVisibility(View.INVISIBLE);
                transaction.commit();
            }
//            clicked settings in profile fragment
            else if (btn.getId() == R.id.profile_IMG_settingsBtn) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.main_LAY_mainWindow, profileSettingsFragment);
                transaction.hide(profileFragment);
                transaction.show(profileSettingsFragment);
                main_LAY_toolbar.setVisibility(View.GONE);
                transaction.commit();
            }
        }
    };

    //    callback triggers when clicking buttons in settings fragment (reached from profile fragment).
    public CallBackClickedBtn btnClickInSettings = new CallBackClickedBtn() {
        @Override
        public void buttonClicked(View btn) {
//            clicked LOG OUT in settings fragment
            if (btn.getId() == R.id.settings_LAY_logout) {
//                main_LAY_toolbar.setVisibility(View.VISIBLE);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.remove(profileFragment);
                transaction.remove(searchFriendsFragment);
                transaction.remove(profileSettingsFragment);
                transaction.remove(chatFragment);
                transaction.remove(changeProfilePicFragment);
                transaction.remove(changeCoverPicFragment);
                transaction.commit();

                Intent backToLoginIntent = new Intent(FragmentManager.this, Login.class);
                startActivity(backToLoginIntent);
                finish();
            }
        }
    };

    //    callback triggers when approving selected new profile picture from temp changeProfilePic frag.
    public CallBackPictureReady approvedProfilePic = new CallBackPictureReady() {
        @Override
        public void pictureReady(Picture pic) {
//            update profile picture
            updateProfilePicture(pic);

//            exit change fragment, show profile again.
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(changeProfilePicFragment);
            transaction.show(profileFragment);
            main_LAY_toolbar.setVisibility(View.VISIBLE);
            transaction.commit();
        }
    };

    //    callback triggers when approving selected new profile picture from temp changeProfilePic frag.
    public CallBackPictureReady approvedCoverPic = new CallBackPictureReady() {
        @Override
        public void pictureReady(Picture pic) {
//            update profile picture
            updateCoverPicture(pic);

//            exit change fragment, show profile again.
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.hide(changeCoverPicFragment);
            transaction.show(profileFragment);
            main_LAY_toolbar.setVisibility(View.VISIBLE);
            transaction.commit();
        }
    };

    private void updateCoverPicture(Picture coverPic) {
        profileFragment.setCoverPic(coverPic);
    }

    public void updateProfilePicture() {
        profileFragment.setProfilePic();
    }

    public void updateProfilePicture(Picture profilePic) {
        profileFragment.setProfilePic(profilePic);
    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Log.d("vvvFragManager", "Pressed back button");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        if in settings -> go back to profile
        if (profileSettingsFragment.isAdded()) {
            transaction.remove(profileSettingsFragment);
            transaction.show(profileFragment);
            main_LAY_toolbar.setVisibility(View.VISIBLE);
        }
//        if in changePic -> go back to profile
        else if (!changeProfilePicFragment.isHidden()) {
            transaction.hide(changeProfilePicFragment);
            transaction.show(profileFragment);
            main_LAY_toolbar.setVisibility(View.VISIBLE);
        } else if(!changeCoverPicFragment.isHidden()) {
            transaction.hide(changeCoverPicFragment);
            transaction.show(profileFragment);
            main_LAY_toolbar.setVisibility(View.VISIBLE);
        }
//        if on any other page -> back to profile (maybe home later)
//        for now i only have the search window.
        // TODO: 3/15/2020 When adding more windows to fragmentManager need to add || between all frags.
        else if (!searchFriendsFragment.isHidden()) {
            transaction.hide(searchFriendsFragment);
            transaction.show(profileFragment);
        } else if(!chatFragment.isHidden()) {
            transaction.hide(chatFragment);
            transaction.show(profileFragment);
        }
//        if on profile -> minimize app
        else if (!profileFragment.isHidden()) {
            super.onBackPressed();
        }
        transaction.commit();

    }

    CallBackEnterOtherAccount visitAccountFromSearch = new CallBackEnterOtherAccount() {
        @Override
        public void clickedOnOtherProfile(Account account) {
//            save visited Account in prefs.
            String jsVisitAcc = new Gson().toJson(account);
            prefs.putString(Constants.PREFS_KEY_FOREIN_ACCOUNT, jsVisitAcc);
            Log.d("vvvManager", "Visited Account: " + jsVisitAcc);

            Intent visitProfileIntent = new Intent(FragmentManager.this, FragmentManager.class);
            visitProfileIntent.putExtra(Constants.KEY_MARK_FOREIN_ACCOUNT, false);
            startActivity(visitProfileIntent);
        }
    };

    public boolean isGpsAvailable() {
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        boolean gps_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled;
    }

    public boolean isNetworkAvailable() {
        LocationManager lm = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
        boolean network_enabled = false;

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return network_enabled;
    }

    public void askToTurnOnGps() {
        new AlertDialog.Builder(this)
                .setMessage("GPS network not enabled")
                .setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel ", null)
                .show();

    }

    private void getLocation() {
        //check location permissions
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
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
                    }
                    Log.d("vvvLocation", "onSuccess: " + latitude + " " + longitude);
                } else {
                    longitude = 1;
                    latitude = 1;
                    updatedLocation = new MyLocation(longitude, latitude, Constants.MSG_LOCATION_NOT_FOUND);
                    Log.d("vvvLocation", "else (not success): " + latitude + " " + longitude);
                }
            }
        });
    }

    private String setLocation(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

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

    private void updateAccountLocation() {
        getLocation();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1010:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    Activity#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for Activity#requestPermissions for more details.
                        return;
                    }
                    fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                longitude = location.getLongitude();
                                latitude = location.getLatitude();
                                Log.d("bbbLocation", "Location= long: " + longitude + " lat: " + latitude);
                                myAccount.setMyLocation(new MyLocation(longitude, latitude, "Israel"));
                                updateCurrentAccount();
                            }
                        }
                    });
                } else {
                    if (grantResults.length > 0)
                        Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
        }
    }


    private void updateCurrentAccount() {
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        myAccount = new Gson().fromJson(jsAccount, Account.class);
        MyFirebase.setAccount(myAccount);
    }

}
