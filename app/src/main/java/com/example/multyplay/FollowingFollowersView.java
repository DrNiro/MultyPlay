package com.example.multyplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;

public class FollowingFollowersView extends AppCompatActivity {

    private FollowingFragment followingFragment;
    private FollowersFragment followersFragment;

    private String arrivedFrom;

    private TextView follow_TXT_nickname;
    private TextView follow_BTN_followers;
    private FrameLayout follow_SEP_followingIndicate;
    private TextView follow_BTN_following;
    private FrameLayout follow_SEP_followersIndicate;

    private MySharedPreferences prefs;
    private String jsAccount;
    private Account myAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_followers_view);

        arrivedFrom = getIntent().getStringExtra(Constants.KEY_SHOW_FOLLOWING_OR_FOLLOWERS);
        prefs = new MySharedPreferences(this);
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        myAccount = new Gson().fromJson(jsAccount, Account.class);

        String jsFollowers = prefs.getString(Constants.PREFS_KEY_MY_FOLLOWERS_LIST, "");
        String jsFollowing = prefs.getString(Constants.PREFS_KEY_MY_FOLLOWING_LIST, "");

        Log.d("wwwFollowView", "jsFollowers from callBackFollowersListReady: " + jsFollowers);
        Log.d("wwwFollowView", "jsFollowers from callBackFollowersListReady: " + jsFollowing);

        Log.d("www", "clicked on: " + arrivedFrom);
        follow_TXT_nickname = findViewById(R.id.follow_TXT_nickname);
        follow_BTN_followers = findViewById(R.id.follow_BTN_followers);
//        follow_TXT_numOfFollowers = findViewById(R.id.follow_TXT_numOfFollowers);
        follow_SEP_followersIndicate = findViewById(R.id.follow_SEP_followersIndicate);
        follow_BTN_following = findViewById(R.id.follow_BTN_following);
//        follow_TXT_numOfFollowing = findViewById(R.id.follow_TXT_numOfFollowing);
        follow_SEP_followingIndicate = findViewById(R.id.follow_SEP_followingIndicate);

        follow_TXT_nickname.setText(myAccount.getNickName());

        followingFragment = new FollowingFragment();
        followingFragment.setCallBackEnterOtherAccount(clickedAccount);

        followersFragment = new FollowersFragment();
        followersFragment.setCallBackEnterOtherAccount(clickedAccount);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.follow_LAY_fragmentsHolder, followingFragment);
        transaction.add(R.id.follow_LAY_fragmentsHolder, followersFragment);

        if(arrivedFrom.equals(Constants.FOLLOWING)) {
            clickedFollowing();
            transaction.show(followingFragment);
            transaction.hide(followersFragment);
        } else if(arrivedFrom.equals(Constants.FOLLOWERS)) {
            clickedFollowers();
            transaction.hide(followingFragment);
            transaction.show(followersFragment);
        }
        transaction.commit();
//        main_BTN_profile.setImageResource(R.drawable.toolbar_img_profile_color);

        follow_BTN_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.show(followingFragment);
                transaction.hide(followersFragment);
                transaction.commit();
                clickedFollowing();
            }
        });

        follow_BTN_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.hide(followingFragment);
                transaction.show(followersFragment);
                transaction.commit();

                clickedFollowers();
            }
        });

    }

    CallBackEnterOtherAccount clickedAccount = new CallBackEnterOtherAccount() {
        @Override
        public void clickedOnOtherProfile(Account account) {
            String jsVisitAcc = new Gson().toJson(account);
            prefs.putString(Constants.PREFS_KEY_FOREIN_ACCOUNT, jsVisitAcc);
            Log.d("vvvManager", "Visited Account: " + jsVisitAcc);

            Intent visitProfileIntent = new Intent(FollowingFollowersView.this, FragmentManager.class);
            visitProfileIntent.putExtra(Constants.KEY_MARK_IS_OWNER, Constants.VISITED_PROFILE);
            startActivity(visitProfileIntent);
            finish();
        }
    };

    private void clickedFollowers() {
        follow_BTN_followers.setTextColor(ContextCompat.getColor(this, R.color.theme_oragne));
//        follow_TXT_numOfFollowers.setTextColor(ContextCompat.getColor(this, R.color.theme_oragne));
        follow_SEP_followersIndicate.setBackgroundColor(ContextCompat.getColor(this, R.color.black));

        follow_BTN_following.setTextColor(ContextCompat.getColor(this, R.color.gray));
//        follow_TXT_numOfFollowing.setTextColor(ContextCompat.getColor(this, R.color.gray));
        follow_SEP_followingIndicate.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));
    }

    private void clickedFollowing() {
        follow_BTN_following.setTextColor(ContextCompat.getColor(this, R.color.theme_oragne));
//        follow_TXT_numOfFollowing.setTextColor(ContextCompat.getColor(this, R.color.theme_oragne));
        follow_SEP_followingIndicate.setBackgroundColor(ContextCompat.getColor(this, R.color.black));

        follow_BTN_followers.setTextColor(ContextCompat.getColor(this, R.color.gray));
//        follow_TXT_numOfFollowers.setTextColor(ContextCompat.getColor(this, R.color.gray));
        follow_SEP_followersIndicate.setBackgroundColor(ContextCompat.getColor(this, R.color.light_gray));

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
