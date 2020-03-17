package com.example.multyplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class InitialProfileSettings extends AppCompatActivity {

    private static int current_page_number;

    private String jsAccount;
    private Account myAccount;
    private MySharedPreferences prefs;

    private PickLanguageFragment pickLanguageFragment;
    private PickGamesFragment pickGamesFragment;
    private PickProfilePicsFragment pickProfilePicsFragment;

    private ChangeProfilePicFragment changeProfilePicFragment;
    private ChangeCoverPicFragment changeCoverPicFragment;
    private LinearLayout profileSetup_LAY_pagesDots;

    private ImageView profileSetup_IMG_dotPage1;
    private ImageView profileSetup_IMG_dotPage2;
    private ImageView profileSetup_IMG_dotPage3;

    private ImageView profileSetup_IMG_back;

    private String nickname;
    private int age;
    private ArrayList<Language> selectedLanguages;
    private boolean isLocationAllowed;
    private MyLocation myLocation;
    private ArrayList<Game> selectedGames;
    private Picture profilePic;
    private Picture coverPic;
    private String description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_profile_settings);

        prefs = new MySharedPreferences(this);
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT,"");
        myAccount = new Gson().fromJson(jsAccount, Account.class);

        Log.d("vvvInitProfile", "account: " + jsAccount);

        pickLanguageFragment = new PickLanguageFragment();
        pickLanguageFragment.setCallback(myProceedCallback);

        pickGamesFragment = new PickGamesFragment();
        pickGamesFragment.setCallback(myProceedCallback);

        pickProfilePicsFragment = new PickProfilePicsFragment();
        pickProfilePicsFragment.setCallback(myProceedCallback);
        pickProfilePicsFragment.setClickedChangePicCallback(btnClickInPickPic);

        changeProfilePicFragment = new ChangeProfilePicFragment();
        changeProfilePicFragment.setCallback(approvedPic);

        changeCoverPicFragment = new ChangeCoverPicFragment();
        changeCoverPicFragment.setCallback(approvedPic);

        profileSetup_LAY_pagesDots = findViewById(R.id.profileSetup_LAY_pagesDots);
        profileSetup_IMG_dotPage1 = findViewById(R.id.profileSetup_IMG_dotPage1);
        profileSetup_IMG_dotPage2 = findViewById(R.id.profileSetup_IMG_dotPage2);
        profileSetup_IMG_dotPage3 = findViewById(R.id.profileSetup_IMG_dotPage3);

        current_page_number = 1;

        // initialize arrayLists to keep selected languages and games from fragments.
        selectedLanguages = new ArrayList<>();
        selectedGames = new ArrayList<>();

        profileSetup_IMG_back = findViewById(R.id.profileSetup_IMG_back);
        profileSetup_IMG_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myProceedCallback.back(current_page_number);
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.profileSetup_LAY_mainWindow ,pickLanguageFragment);
        transaction.add(R.id.profileSetup_LAY_mainWindow ,pickGamesFragment);
        transaction.add(R.id.profileSetup_LAY_mainWindow ,pickProfilePicsFragment);
        transaction.show(pickLanguageFragment);
        transaction.hide(pickGamesFragment);
        transaction.hide(pickProfilePicsFragment);
        transaction.commit();

        showStartDialog();

    }

    private void showStartDialog() {
        CustomDialog customDialog = new CustomDialog();
        customDialog.show(getSupportFragmentManager(), "custom dialog");
    }

    private CallBackProceed myProceedCallback = new CallBackProceed() {
        @Override
        public void next(final int pageNum) {
            if(pageNum == PickLanguageFragment.PAGE_NUMBER) {
                nickname = pickLanguageFragment.getNickNameInserted();
                age = pickLanguageFragment.getAge();
                description = pickLanguageFragment.getDescription();
                selectedLanguages = pickLanguageFragment.getSelectedLangs();
                isLocationAllowed = pickLanguageFragment.isLocationAllowed();
                myLocation = pickLanguageFragment.getMyLocation();
            } else if(pageNum == PickGamesFragment.PAGE_NUMBER) {
                selectedGames = pickGamesFragment.getSelectedGames();
            } else if(pageNum == PickProfilePicsFragment.PAGE_NUMBER) {

                DoneDialog doneDialog = new DoneDialog();
                doneDialog.setCallBackApproved(new CallBackApproved() {
                    @Override
                    public void onOkClick() {
                        goToPage(pageNum + 1);
                    }
                });
                doneDialog.show(getSupportFragmentManager(), "custom dialog");
                //              receive pictures from callbacks
            }
            if(pageNum != PickProfilePicsFragment.PAGE_NUMBER)
                goToPage(pageNum + 1);
        }

        @Override
        public void back(int pageNum) {
            goToPage(pageNum - 1);
        }
    };

// TODO: 3/3/2020 can make shorter and dynamic by add all fragments to array and all dots to array, and control the transitions by pageNumber only.
    private void goToPage(int page) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(page == 0) {
            Toast.makeText(this, "Please complete set up profile", Toast.LENGTH_SHORT).show();
        }
        else if(page == 1) {
            profileSetup_IMG_dotPage1.setImageResource(R.drawable.ic_dot_blue);
            profileSetup_IMG_dotPage2.setImageResource(R.drawable.ic_dot_gray);
            profileSetup_IMG_dotPage3.setImageResource(R.drawable.ic_dot_gray);
            transaction.show(pickLanguageFragment);
            transaction.hide(pickGamesFragment);
            transaction.hide(pickProfilePicsFragment);
            transaction.commit();
            current_page_number = page;
        } else if(page == 2) {
            profileSetup_IMG_dotPage1.setImageResource(R.drawable.ic_dot_gray);
            profileSetup_IMG_dotPage2.setImageResource(R.drawable.ic_dot_blue);
            profileSetup_IMG_dotPage3.setImageResource(R.drawable.ic_dot_gray);
            transaction.hide(pickLanguageFragment);
            transaction.show(pickGamesFragment);
            transaction.hide(pickProfilePicsFragment);
            transaction.commit();
            current_page_number = page;
        } else if(page == 3) {
            profileSetup_IMG_dotPage1.setImageResource(R.drawable.ic_dot_gray);
            profileSetup_IMG_dotPage2.setImageResource(R.drawable.ic_dot_gray);
            profileSetup_IMG_dotPage3.setImageResource(R.drawable.ic_dot_blue);
            transaction.hide(pickLanguageFragment);
            transaction.hide(pickGamesFragment);
            transaction.show(pickProfilePicsFragment);
            transaction.commit();
            current_page_number = page;
        } else if(page == 4) {
            transaction.remove(pickLanguageFragment);
            transaction.remove(pickGamesFragment);
            transaction.remove(pickProfilePicsFragment);
            transaction.commit();
            current_page_number = page;

            myAccount.setNickName(nickname);
            myAccount.setAge(age);
            myAccount.setDescription(description);
            myAccount.setLanguages(new LanguageList(selectedLanguages));
            myAccount.setLocationAllowed(isLocationAllowed);
            myAccount.setGames(new GameList(selectedGames));
            if(coverPic != null)
                myAccount.setCoverPic(coverPic);
            if(profilePic != null)
                myAccount.setProfilePic(profilePic);
            if(myLocation == null) {
                myLocation = new MyLocation(1, 1, Constants.MSG_LOCATION_NOT_FOUND);
            }
            Log.d("bbbLoc", "myLocation: " + myLocation.getAddress());
            myAccount.setMyLocation(myLocation);

            MyFirebase.setAccount(myAccount);
            jsAccount = new Gson().toJson(myAccount);
            prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
            prefs.putString(Constants.PREFS_KEY_CURRENT_LOGGED_IN, Constants.LOGGED_IN);

            Intent goToProfileIntent = new Intent(InitialProfileSettings.this, FragmentManager.class);
//            goToProfileIntent.putExtra(Constants.KEY_MY_ACCOUNT_JSON, jsonAccount);
            Log.d("vvvInitProfileFINISH", "account: " + jsAccount);
            goToProfileIntent.putExtra(Constants.KEY_MARK_FOREIN_ACCOUNT, true);
            startActivity(goToProfileIntent);
            finish();
        }

    }

//    MAYBE DELETE
    //    callback triggers when clicking in profile fragment on change profile picture.
    public CallBackClickedBtn btnClickInInitProfilePic = new CallBackClickedBtn() {
        @Override
        public void buttonClicked(View btn) {
            if(btn.getId() == R.id.profile_RDV_pic) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(pickProfilePicsFragment);
                transaction.add(R.id.profileSetup_LAY_mainWindow ,changeProfilePicFragment);
                transaction.show(changeProfilePicFragment);
                profileSetup_IMG_back.setVisibility(View.INVISIBLE);
                profileSetup_LAY_pagesDots.setVisibility(View.INVISIBLE);
                transaction.commit();
            }
        }
    };

    //    callback triggers when clicking in pick profile/cover picture fragment on change picture.
    public CallBackClickedBtn btnClickInPickPic = new CallBackClickedBtn() {
        @Override
        public void buttonClicked(View btn) {
            Log.d("vvvInit", "clicked " + btn.getId());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if(btn.getId() == R.id.pickPics_RDV_changeCoverPic) {
                transaction.hide(pickProfilePicsFragment);
                transaction.add(R.id.profileSetup_LAY_mainWindow ,changeCoverPicFragment);
                transaction.show(changeCoverPicFragment);
            } else if(btn.getId() == R.id.pickPics_RDV_changeprofilePic) {
                transaction.hide(pickProfilePicsFragment);
                transaction.add(R.id.profileSetup_LAY_mainWindow ,changeProfilePicFragment);
                transaction.show(changeProfilePicFragment);
            }

            profileSetup_IMG_back.setVisibility(View.INVISIBLE);
            profileSetup_LAY_pagesDots.setVisibility(View.INVISIBLE);
            transaction.commit();
        }
    };

    public CallBackPictureReady approvedPic = new CallBackPictureReady() {
        @Override
        public void pictureReady(Picture pic) {
            if(pic.getName().equals(Constants.PROFILE_PIC)) {
                profilePic = pic;
                pickProfilePicsFragment.setProfilePic(pic);
            } else if(pic.getName().equals(Constants.COVER_PIC)) {
                coverPic = pic;
                pickProfilePicsFragment.setCoverPic(pic);
            }
            exitFrag(pic.getName());
        }
    };

    private void exitFrag(String picName) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(picName.equals(Constants.PROFILE_PIC)) {
            transaction.remove(changeProfilePicFragment);
        } else if(picName.equals(Constants.COVER_PIC)) {
            transaction.remove(changeCoverPicFragment);
        }
        transaction.show(pickProfilePicsFragment);
        profileSetup_IMG_back.setVisibility(View.VISIBLE);
        profileSetup_LAY_pagesDots.setVisibility(View.VISIBLE);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!pickLanguageFragment.isHidden()) {
//            do nothing
        } else if(!pickGamesFragment.isHidden()) {
            profileSetup_IMG_dotPage1.setImageResource(R.drawable.ic_dot_blue);
            profileSetup_IMG_dotPage2.setImageResource(R.drawable.ic_dot_gray);
            profileSetup_IMG_dotPage3.setImageResource(R.drawable.ic_dot_gray);
            transaction.show(pickLanguageFragment);
            transaction.hide(pickGamesFragment);
            transaction.hide(pickProfilePicsFragment);
            transaction.commit();
            current_page_number = 1;
        } else if(!pickProfilePicsFragment.isHidden()) {
            profileSetup_IMG_dotPage1.setImageResource(R.drawable.ic_dot_gray);
            profileSetup_IMG_dotPage2.setImageResource(R.drawable.ic_dot_blue);
            profileSetup_IMG_dotPage3.setImageResource(R.drawable.ic_dot_gray);
            transaction.hide(pickLanguageFragment);
            transaction.show(pickGamesFragment);
            transaction.hide(pickProfilePicsFragment);
            transaction.commit();
            current_page_number = 2;
        } else if(changeCoverPicFragment.isAdded()) {
            exitFrag(Constants.COVER_PIC);
        } else if(changeProfilePicFragment.isAdded()) {
            exitFrag(Constants.PROFILE_PIC);
        }
    }

}
