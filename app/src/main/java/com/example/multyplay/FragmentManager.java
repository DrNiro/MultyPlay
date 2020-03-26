package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class FragmentManager extends AppCompatActivity {

    private ProfileFragment profileFragment;
    private SearchFriendsFragment searchFriendsFragment;
    private ProfileSettingsFragment profileSettingsFragment;
    private ChatFragment chatFragment;
    private ProPageFragment proPageFragment;
    private FeedFragment feedFragment;

    private ChangeProfilePicFragment changeProfilePicFragment;
    private ChangeCoverPicFragment changeCoverPicFragment;

    private ArrayList<Fragment> allProfileFragments;
    private ArrayList<ImageView> allToolbarViews;
    private ArrayList<Integer> allToolbarImagesGray;
    private ArrayList<Integer> allToolbarImagesColor;
    private Fragment ownersLastPage;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
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
    private Following myFollowing;
    private String jsFollowing;
    private Followers myFollowers;
    private String jsFollowers;
    private ChatWith myChats;
    private String jsChattingWith;
    private MySharedPreferences prefs;

    private boolean isOwnerProfile;

    private boolean finishedLoadingFollowers;
    private boolean finishedLoadingFollowing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_manager);
        Log.d("bbbMANAGER", "onCreate() called");
        isOwnerProfile = getIntent().getBooleanExtra(Constants.KEY_MARK_IS_OWNER, true);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        findViews();

//        main fragments initialization
        profileFragment = new ProfileFragment();
        profileFragment.setCallback(btnClickInProfile);

        searchFriendsFragment = new SearchFriendsFragment();
        profileFragment.setIsOwner(isOwnerProfile);
        searchFriendsFragment.setCallBackEnterOtherAccount(visitAccountFromSearch);
        searchFriendsFragment.setCallBackLocation(setLocationCallback);

//        temp fragments setup
        changeProfilePicFragment = new ChangeProfilePicFragment();
        changeProfilePicFragment.setCallback(approvedProfilePic);

        changeCoverPicFragment = new ChangeCoverPicFragment();
        changeCoverPicFragment.setCallback(approvedCoverPic);

        profileSettingsFragment = new ProfileSettingsFragment();
        profileSettingsFragment.setCallback(btnClickInSettings);

        chatFragment = new ChatFragment();
        chatFragment.setCallBackEnterChatWithAccount(startChatCallback);
        proPageFragment = new ProPageFragment();
        feedFragment = new FeedFragment();

//        get myAccount json from login and save it on sharedPreferences, also create myAccount from json.
        prefs = new MySharedPreferences(this);
//        jsAccount = getIntent().getStringExtra(Constants.KEY_MY_ACCOUNT_JSON);
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
//        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
        myAccount = new Gson().fromJson(jsAccount, Account.class);

        if (isOwnerProfile) {
            myAccount.setOnline(true);
            jsAccount = new Gson().toJson(myAccount);
            prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
            MyFirebase.setAccount(myAccount);
            finishedLoadingFollowing = false;
            MyFirebase.getFollowing(callBackFollowingListReady, myAccount);
            finishedLoadingFollowers = false;
            MyFirebase.getFollowers(callBackFollowersListReady, myAccount);
            MyFirebase.getOpenChats(callBackOpenChatsReady, myAccount);
        }

//        Initialize the first window to see after login.
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.main_LAY_mainWindow, profileFragment);
        transaction.add(R.id.main_LAY_mainWindow, searchFriendsFragment);
        transaction.add(R.id.main_LAY_mainWindow, chatFragment);
        transaction.add(R.id.main_LAY_mainWindow, proPageFragment);
        transaction.add(R.id.main_LAY_mainWindow, feedFragment);
        transaction.add(R.id.main_LAY_mainWindow, changeProfilePicFragment);
        transaction.add(R.id.main_LAY_mainWindow, changeCoverPicFragment);
        transaction.show(profileFragment);
        main_BTN_profile.setImageResource(R.drawable.toolbar_img_profile_color);
        transaction.hide(searchFriendsFragment);
        transaction.hide(chatFragment);
        transaction.hide(proPageFragment);
        transaction.hide(feedFragment);
        transaction.hide(changeProfilePicFragment);
        transaction.hide(changeCoverPicFragment);
        transaction.commit();



        if(isOwnerProfile) {
            ownersLastPage = profileFragment;
            initAllLists();
        }

//        Toolbar buttons click listeners to navigate through app fragments.
        main_BTN_profile.setOnClickListener(profileClickListener);
        main_BTN_chat.setOnClickListener(comingSoonBtnClickListener);
        main_BTN_search.setOnClickListener(searchClickListener);
        main_BTN_feed.setOnClickListener(comingSoonBtnClickListener);
        main_BTN_pro.setOnClickListener(comingSoonBtnClickListener);



    }

    private void findViews() {
        //        findViews
        main_LAY_toolbar = findViewById(R.id.main_LAY_toolbar);
        main_BTN_profile = findViewById(R.id.main_BTN_profile);
        main_BTN_search = findViewById(R.id.main_BTN_search);
        main_BTN_feed = findViewById(R.id.main_BTN_feed);
        main_BTN_chat = findViewById(R.id.main_BTN_chat);
        main_BTN_pro = findViewById(R.id.main_BTN_pro);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("bbbMANAGER", "onPause() called");
        if(isOwnerProfile) {
            for(int i = 0; i < allProfileFragments.size(); i++) {
                if(!allProfileFragments.get(i).isHidden()) {
                    ownersLastPage = allProfileFragments.get(i);
                    Log.d("bbbMANAGER", "ownersLastPage: " + ownersLastPage.getTag());
                }
                break;
            }
        }
    }

    private void initAllLists() {
        allProfileFragments = new ArrayList<>();
        allProfileFragments.add(profileFragment);
        allProfileFragments.add(chatFragment);
        allProfileFragments.add(searchFriendsFragment);
        allProfileFragments.add(feedFragment);
        allProfileFragments.add(proPageFragment);

        allToolbarViews = new ArrayList<>();
        allToolbarViews.add(main_BTN_profile);
        allToolbarViews.add(main_BTN_chat);
        allToolbarViews.add(main_BTN_search);
        allToolbarViews.add(main_BTN_feed);
        allToolbarViews.add(main_BTN_pro);

        allToolbarImagesGray = new ArrayList<>();
        allToolbarImagesGray.add(R.drawable.toolbar_img_profile);
        allToolbarImagesGray.add(R.drawable.toolbar_img_chat);
        allToolbarImagesGray.add(R.drawable.toolbar_img_bird_search);
        allToolbarImagesGray.add(R.drawable.toolbar_img_feed);
        allToolbarImagesGray.add(R.drawable.toolbar_img_pro);

        allToolbarImagesColor = new ArrayList<>();
        allToolbarImagesColor.add(R.drawable.toolbar_img_profile_color);
        allToolbarImagesColor.add(R.drawable.toolbar_img_chat_color);
        allToolbarImagesColor.add(R.drawable.toolbar_img_bird_color);
        allToolbarImagesColor.add(R.drawable.toolbar_img_feed_color);
        allToolbarImagesColor.add(R.drawable.toolbar_img_pro_color);

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("bbbMANAGER", "onStop() called");
        if (isOwnerProfile) {
            startService(new Intent(this, ClosingService.class));
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
//        if visiting another profile and hit the profile button, do it and go back to own profile.
        if (hasFocus && isOwnerProfile) {
            Log.d("vvvManager", "Owner: " + isOwnerProfile);
            main_BTN_profile.callOnClick();
//            switchToolbarFocus(ownersLastPage);
        }
    }


    //    show profile fragment
    private View.OnClickListener profileClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isOwnerProfile) {
                finish();
            } else if (profileFragment.isHidden() && changeCoverPicFragment.isHidden() && changeProfilePicFragment.isHidden()) {
                switchToolbarFocus(profileFragment);
            }
        }
    };

    //    show search fragmentse
    private View.OnClickListener searchClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isOwnerProfile) {
                finish();
            } else if (searchFriendsFragment.isHidden()) {
                switchToolbarFocus(searchFriendsFragment);
            }
        }
    };

    private void switchToolbarFocus(Fragment fragment) {
        if(!fragment.isHidden())
            return;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for(int i = 0; i < allProfileFragments.size(); i++) {
            if(allProfileFragments.indexOf(fragment) == i) {
                allToolbarViews.get(i).setImageResource(allToolbarImagesColor.get(i));
                transaction.show(allProfileFragments.get(i));
                Log.d("ddd", "i color: " + i);
            } else if(!allProfileFragments.get(i).isHidden()) {
                allToolbarViews.get(i).setImageResource(allToolbarImagesGray.get(i));
                ownersLastPage = allProfileFragments.get(i);
                transaction.hide(allProfileFragments.get(i));
                if(allProfileFragments.get(i) == searchFriendsFragment) {
                    if(searchFriendsFragment.isFinishedSearch()) {
                        searchFriendsFragment.resetSearch();
                    }
                }
                Log.d("ddd", "i gray: " + i);
            }
        }
        transaction.commit();
    }

    private View.OnClickListener comingSoonBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!isOwnerProfile) {
                finish();
            } else if(view.getId() == R.id.main_BTN_chat) {
                switchToolbarFocus(chatFragment);
//                transaction.hide(profileFragment);
//                transaction.show(chatFragment);
//                transaction.hide(searchFriendsFragment);
//                transaction.commit();
            } else if(view.getId() == R.id.main_BTN_pro) {
                switchToolbarFocus(proPageFragment);
//                Toast.makeText(FragmentManager.this, "COMING SOON", Toast.LENGTH_SHORT).show();
            } else if(view.getId() == R.id.main_BTN_feed) {
                switchToolbarFocus(feedFragment);
//                Toast.makeText(FragmentManager.this, "COMING SOON", Toast.LENGTH_SHORT).show();
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
            } else if(btn.getId() == R.id.profile_BTN_followingOwner) {
                if(finishedLoadingFollowing) {
                    Intent followingIntent = new Intent(FragmentManager.this, FollowingFollowersView.class);
                    followingIntent.putExtra(Constants.KEY_SHOW_FOLLOWING_OR_FOLLOWERS, Constants.FOLLOWING);
                    startActivity(followingIntent);
                } else {
                    Toast.makeText(FragmentManager.this, "Loading following", Toast.LENGTH_SHORT).show();
                }
            } else if(btn.getId() == R.id.profile_BTN_followersOwner) {
                if(finishedLoadingFollowers) {
                    Intent followersIntent = new Intent(FragmentManager.this, FollowingFollowersView.class);
                    followersIntent.putExtra(Constants.KEY_SHOW_FOLLOWING_OR_FOLLOWERS, Constants.FOLLOWERS);
                    startActivity(followersIntent);
                } else {
                    Toast.makeText(FragmentManager.this, "Loading followers", Toast.LENGTH_SHORT).show();
                }
            } else if(btn.getId() == R.id.profile_BTN_Message) {
                if(isOwnerProfile) {
                    chatFragment.updateList();
                }
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
                transaction.remove(feedFragment);
                transaction.remove(proPageFragment);
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
        if(!isOwnerProfile) {
            finish();
        } else if (profileSettingsFragment.isAdded()) {
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
        else if (!searchFriendsFragment.isHidden()) {
            switchToolbarFocus(ownersLastPage);
        } else if(!chatFragment.isHidden()) {
            switchToolbarFocus(ownersLastPage);
        } else if(!feedFragment.isHidden()) {
            switchToolbarFocus(ownersLastPage);
        } else if(!proPageFragment.isHidden()) {
            switchToolbarFocus(ownersLastPage);
        }
//        if on profile -> minimize app
        else if (!profileFragment.isHidden()) {
            super.onBackPressed();
        }
        transaction.commit();

    }

    private CallBackEnterOtherAccount visitAccountFromSearch = new CallBackEnterOtherAccount() {
        @Override
        public void clickedOnOtherProfile(Account account) {
//            save visited Account in prefs.
            String jsVisitAcc = new Gson().toJson(account);
            prefs.putString(Constants.PREFS_KEY_FOREIN_ACCOUNT, jsVisitAcc);
            Log.d("vvvManager", "Visited Account: " + jsVisitAcc);

            Intent visitProfileIntent = new Intent(FragmentManager.this, FragmentManager.class);
            visitProfileIntent.putExtra(Constants.KEY_MARK_IS_OWNER, Constants.VISITED_PROFILE);
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
                .setMessage("Cannot search for gamers while GPS network is not enabled.\nPlease turn on GPS and try again.")
                .setPositiveButton("Open location settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel ", null)
                .show();

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


    private void updateCurrentAccount() {
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        myAccount = new Gson().fromJson(jsAccount, Account.class);
        Log.d("bbbMANAGER", "updateCurrentAccount() called, MyFirebase.setAccount");
        MyFirebase.setAccount(myAccount);
    }

    private CallBackOpenChatsReady callBackOpenChatsReady = new CallBackOpenChatsReady() {
        @Override
        public void chatsOpenListReady(ChatWith chatWith) {
            myChats = chatWith;
            jsChattingWith = new Gson().toJson(chatWith);
            prefs.putString(Constants.PREFS_KEY_MY_OPEN_CHATS, jsChattingWith);
            chatFragment.setJsChattingWith(jsChattingWith);
            Log.d("eee", "my chats from prefs: " + jsChattingWith);
            chatFragment.getMyChattingWithAccounts();
        }

        @Override
        public void listIsEmpty() {
        }
    };

    private CallBackFollowListReady callBackFollowersListReady = new CallBackFollowListReady() {
        @Override
        public void followingListReady(Following following) {
        }

        @Override
        public void followersListReady(Followers followers) {
            myFollowers = followers;
            jsFollowers = new Gson().toJson(followers);
            Log.d("wwwFragManager", "jsFollowers from callBackFollowersListReady: " + jsFollowers);
            prefs.putString(Constants.PREFS_KEY_MY_FOLLOWERS_LIST, jsFollowers);
            finishedLoadingFollowers = true;
        }

        @Override
        public void listIsEmpty() {
            myFollowers = new Followers(myAccount.getId().getSerialNum(), new ArrayList<String>());
            jsFollowers = new Gson().toJson(myFollowers);
            prefs.putString(Constants.PREFS_KEY_MY_FOLLOWERS_LIST, jsFollowers);
            finishedLoadingFollowers = true;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(isOwnerProfile) {
            chatFragment.updateList();
        }
    }

    private CallBackFollowListReady callBackFollowingListReady = new CallBackFollowListReady() {
        @Override
        public void followingListReady(Following following) {
            myFollowing = following;
            jsFollowing = new Gson().toJson(following);
            prefs.putString(Constants.PREFS_KEY_MY_FOLLOWING_LIST, jsFollowing);
            finishedLoadingFollowing = true;
        }

        @Override
        public void followersListReady(Followers followers) {
        }

        @Override
        public void listIsEmpty() {
            myFollowing = new Following(myAccount.getId().getSerialNum(), new ArrayList<String>());
            jsFollowing = new Gson().toJson(myFollowing);
            prefs.putString(Constants.PREFS_KEY_MY_FOLLOWING_LIST, jsFollowing);
            finishedLoadingFollowing = true;
        }
    };

    CallBackApproved setLocationCallback = new CallBackApproved() {
        @Override
        public void onOkClick() {
            startGetLoction();
        }
    };

    private void startGetLoction() {
        initiateLocationRequests();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("nnn", "No permissions");
            requestPermission();
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(FragmentManager.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    Log.d("nnn", "location received");
                    searchFriendsFragment.goSearchGO(location);
                    if(locationManager != null)
                        locationManager.removeUpdates(listener);
                } else {
                    Log.d("nnn", "location == null");
                    searchFriendsFragment.goSearchGO(location);
                    if (!isGpsAvailable())
                        askToTurnOnGps();
                    initiateLocationRequests();
                }
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

    private CallBackEnterOtherAccount startChatCallback = new CallBackEnterOtherAccount() {
        @Override
        public void clickedOnOtherProfile(Account account) {
            String jsChatWithAcc = new Gson().toJson(account);
            prefs.putString(Constants.PREFS_KEY_CHAT_WITH_ACCOUNT, jsChatWithAcc);
//            Log.d("vvvManager", "Visited Account: " + jsVisitAcc);
            Intent startChatWindowIntent = new Intent(FragmentManager.this, ChatWindow.class);
            startChatWindowIntent.putExtra(Constants.KEY_CHAT_WITH, jsChatWithAcc);
            startActivity(startChatWindowIntent);
        }
    };

    private void requestPermission() {
        ActivityCompat.requestPermissions( FragmentManager.this, new String[] { ACCESS_FINE_LOCATION }, Constants.PERMISSION_REQUEST_CODE);
        ActivityCompat.requestPermissions( FragmentManager.this, new String[] { android.Manifest.permission.ACCESS_COARSE_LOCATION }, Constants.PERMISSION_REQUEST_CODE);
    }

}
