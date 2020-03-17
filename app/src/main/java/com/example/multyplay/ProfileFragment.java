package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ProfileFragment extends Fragment {

    private View view = null;
    private CallBackClickedBtn callBackClickedBtn;

    private RelativeLayout profile_LAY_profile;
    private LinearLayout profile_LAY_active_status;
    private TextView profile_TXT_name;
    private TextView profile_TXT_age;
    private LinearLayout profile_LAY_relativeLocation;
    // at first show "Insert profile Description"
    private TextView profile_TXT_description;
    private ImageView profile_BTN_editDescription;
    private LinearLayout profile_LAY_topButtons;
    private TextView profile_TXT_numOfGamesOwned;
    private ImageView profile_IMG_changeProfilePicBtn;
    private ImageView profile_IMG_changecoverPicBtn;
    private ImageView profile_IMG_settingsBtn;
    private RelativeLayout profile_LAY_changeProfilePic;
    private RelativeLayout profile_LAY_changeCoverPic;
    private TextView profile_TXT_onlineState;
    private ImageView profile_IMG_onlineState;
    private Button profile_BTN_follow;

    private RoundedImageView profile_RDV_pic;
    private ImageView profile_IMG_coverPic;

    private RecyclerView scoreListRecyclerView;
    private LangListAdapter adapter;

    private RecyclerView gamesRecyclerView;
    private GameCardViewListAdapter gamesAdapter;

    private Account account;

    private boolean isOwner;

    private MySharedPreferences prefs;
    private String jsAccount;
    private String jsId;

    private String jsVisitedAccount;
    private Account visitedAccount;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.activity_profile_fragment, container, false);
        }

        findViews();

        Log.d("bbb", "profileFrag, isOwnerProfile: " + isOwner);

//      initialize MySharedPreferences
        prefs = new MySharedPreferences(view.getContext());
        if(isOwner) {
            Log.d("vvvProfileOwnerCheck", "OWNER, isOwner:" + isOwner);
            updateOwnersProfile();
        } else {
            Log.d("vvvOwnerCheck", "VISITING, isOwner:" + isOwner);
            updateVisitedProfile();
        }

        profile_BTN_editDescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2/22/2020 open an edit description window (maybe fragment)
            }
        });

        profile_IMG_changeProfilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                return to framentManager to select a profile picture
                callBackClickedBtn.buttonClicked(view);
            }
        });

        profile_IMG_changecoverPicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                return to framentManager to select a profile picture
                callBackClickedBtn.buttonClicked(view);
            }
        });

        profile_IMG_settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBackClickedBtn.buttonClicked(view);
            }
        });

//        profile_BTN_follow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(profile_BTN_follow.isEnabled()) {
//                    String emailVis = visitedAccount.getEmail().toString();
//                    ArrayList<String> iFollow = account.getFollowing();
//                    iFollow.add(emailVis);
//                    account.setFollowing(iFollow);
//
//                    String myEmail = account.getEmail().toString();
//                    ArrayList<String> visFollowers = visitedAccount.getFollowMe();
//                    visFollowers.add(myEmail);
//                    visitedAccount.setFollowMe(visFollowers);
//                    profile_BTN_follow.setEnabled(false);
//                } else {
//                    // TODO: 3/17/2020 unfollow with by drawable id
//                }
//
//            }
//        });

        return view;
    }

    public void setCallback(CallBackClickedBtn callback) {
        this.callBackClickedBtn = callback;
    }

    public void setIsOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    private void findViews() {
        profile_BTN_follow = view.findViewById(R.id.profile_BTN_follow);
        profile_IMG_changeProfilePicBtn = view.findViewById(R.id.profile_IMG_changeProfilePicBtn);
        profile_IMG_changecoverPicBtn = view.findViewById(R.id.profile_IMG_changecoverPicBtn);
        profile_IMG_settingsBtn = view.findViewById(R.id.profile_IMG_settingsBtn);
        profile_LAY_profile = view.findViewById(R.id.profile_LAY_profile);
        profile_LAY_active_status = view.findViewById(R.id.profile_LAY_active_status);
        profile_TXT_name = view.findViewById(R.id.profile_TXT_name);
        profile_TXT_age = view.findViewById(R.id.profile_TXT_age);
        profile_LAY_relativeLocation = view.findViewById(R.id.profile_LAY_relativeLocation);
        profile_TXT_description = view.findViewById(R.id.profile_TXT_description);
        profile_BTN_editDescription = view.findViewById(R.id.profile_BTN_editDescription);
        profile_LAY_topButtons = view.findViewById(R.id.profile_LAY_topButtons);
        profile_TXT_numOfGamesOwned = view.findViewById(R.id.profile_TXT_numOfGamesOwned);
        profile_RDV_pic = view.findViewById(R.id.profile_RDV_pic);
        profile_IMG_coverPic = view.findViewById(R.id.profile_IMG_coverPic);
        profile_LAY_changeProfilePic = view.findViewById(R.id.profile_LAY_changeProfilePic);
        profile_LAY_changeCoverPic = view.findViewById(R.id.profile_LAY_changeCoverPic);
        profile_IMG_onlineState = view.findViewById(R.id.profile_IMG_onlineState);
        profile_TXT_onlineState = view.findViewById(R.id.profile_TXT_onlineState);
    }

    private void updateOwnersProfile() {
        updateCurrentAccount();
        // TODO: 2/29/2020 Place the account's profile picture after building the storage system (using Glide?).
        profile_LAY_active_status.setVisibility(View.GONE);
        String nameWithComma = account.getNickName() + ",";
        profile_TXT_name.setText(nameWithComma);
        profile_TXT_age.setText(account.getAge()+"");
        profile_LAY_relativeLocation.setVisibility(View.GONE);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) profile_TXT_description.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.profile_LAY_nameAndAge);
//        show account's description
        String accDescription = account.getDescription();
        if(!accDescription.trim().equals(""))
            profile_TXT_description.setText(account.getDescription());
        profile_LAY_topButtons.setVisibility(View.GONE);
//        show account's games
        if(account.getGames() == null) {
            profile_TXT_numOfGamesOwned.setText(String.valueOf(0));
        } else {
            profile_TXT_numOfGamesOwned.setText(String.valueOf(account.getGames().getGames().size()));
            createGamesRecyclerGrid(account);
        }
//        show account's languages
        if(account.getLanguages() != null) {
            createLanguagesRecycler(account);
        }
//        show account's profile picture
        if(account.getProfilePic().getUrl() == null) {
            Log.d("vvvProfile==null", "no profile picture");
            Glide.with(this)
                    .load(R.drawable.ic_empty_profile_pic)
                    .centerCrop()
                    .into(profile_RDV_pic);
//            profile_RDV_pic.setBackground(getResources().getDrawable(R.drawable.ic_empty_profile_pic));
        } else if(account.getProfilePic().getUrl() != null) {
            Log.d("vvvProfile != null", "url: " + account.getProfilePic().getUrl());
            Glide.with(this)
                    .load(account.getProfilePic().getUrl())
                    .centerCrop()
                    .into(profile_RDV_pic);
        }
//        show account's cover picture
        if(account.getCoverPic().getUrl() == null) {
            Log.d("vvvCover==null", "no cover picture");
//            Glide.with(this)
//                    .load(R.drawable.cover_placeholder)
//                    .centerCrop()
//                    .into(profile_IMG_coverPic);
        } else if(account.getCoverPic().getUrl() != null) {
            Log.d("vvvCover != null", "url: " + account.getCoverPic().getUrl());
            Glide.with(this)
                    .load(account.getCoverPic().getUrl())
                    .centerCrop()
                    .into(profile_IMG_coverPic);
        }
    }

    private void updateVisitedProfile() {
        setVisitedAccount();
        if(visitedAccount.isOnline()) {
            profile_IMG_onlineState.setImageResource(R.drawable.ic_green_dot);
            profile_TXT_onlineState.setText("Online");
        } else {
            profile_IMG_onlineState.setImageResource(R.drawable.ic_red_dot);
            profile_TXT_onlineState.setText("Offline");
        }
//        if(account.getFollowing().contains(visitedAccount.getEmail().toString())) {
//            profile_BTN_follow.setEnabled(false);
//        } else {
//            profile_BTN_follow.setEnabled(true);
//        }
        profile_BTN_editDescription.setVisibility(View.GONE);
        profile_IMG_onlineState.setVisibility(View.VISIBLE);
        profile_TXT_onlineState.setVisibility(View.VISIBLE);
        profile_LAY_changeProfilePic.setVisibility(View.GONE);
        profile_LAY_changeCoverPic.setVisibility(View.GONE);
        profile_LAY_active_status.setVisibility(View.VISIBLE);
        String nameWithComma = visitedAccount.getNickName() + ",";
        profile_TXT_name.setText(nameWithComma);
        String stringAge = visitedAccount.getAge()+"";
        profile_TXT_age.setText(stringAge);
        if(visitedAccount.isLocationAllowed()) {
            profile_LAY_relativeLocation.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) profile_TXT_description.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.profile_LAY_nameAndAge);
        } else {
            profile_LAY_relativeLocation.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) profile_TXT_description.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, R.id.profile_LAY_relativeLocation);
        }
//        show account's description
        String accDescription = visitedAccount.getDescription();
        if(!accDescription.trim().equals(""))
            profile_TXT_description.setText(accDescription);
        profile_LAY_topButtons.setVisibility(View.VISIBLE);
//        show account's games
        if(visitedAccount.getGames() == null) {
            profile_TXT_numOfGamesOwned.setText(String.valueOf(0));
        } else {
            profile_TXT_numOfGamesOwned.setText(String.valueOf(visitedAccount.getGames().getGames().size()));
            createGamesRecyclerGrid(visitedAccount);
        }
//        show account's languages
        if(visitedAccount.getLanguages() != null) {
            createLanguagesRecycler(visitedAccount);
        }
//        show account's profile picture
        if(visitedAccount.getProfilePic().getUrl() == null) {
            Log.d("vvvProfile==null", "no profile picture");
            Glide.with(this)
                    .load(R.drawable.ic_empty_profile_pic)
                    .centerCrop()
                    .into(profile_RDV_pic);
//            profile_RDV_pic.setBackground(getResources().getDrawable(R.drawable.ic_empty_profile_pic));
        } else if(visitedAccount.getProfilePic().getUrl() != null) {
            Log.d("vvvProfile != null", "url: " + visitedAccount.getProfilePic().getUrl());
            Glide.with(this)
                    .load(visitedAccount.getProfilePic().getUrl())
                    .centerCrop()
                    .into(profile_RDV_pic);
        }
//        show account's cover picture
        if(visitedAccount.getCoverPic().getUrl() == null) {
            Log.d("vvvCover==null", "no cover picture");
//            Glide.with(this)
//                    .load(R.drawable.cover_placeholder)
//                    .centerCrop()
//                    .into(profile_IMG_coverPic);
        } else if(visitedAccount.getCoverPic().getUrl() != null) {
            Log.d("vvvCover != null", "url: " + visitedAccount.getCoverPic().getUrl());
            Glide.with(this)
                    .load(visitedAccount.getCoverPic().getUrl())
                    .centerCrop()
                    .into(profile_IMG_coverPic);
        }
    }

    private void updateCurrentAccount() {
        jsAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        Log.d("vvvProfileFragUpdate", "account: " + jsAccount);
//        Log.d("vvv", "jsAccount: " + jsAccount);
        account = new Gson().fromJson(jsAccount, Account.class);
        prefs.putString(Constants.PREFS_KEY_ACCOUNT_SERIAL, account.getId().getSerialNum()+"");
        MyFirebase.setAccount(account);
    }

    private void setVisitedAccount() {
        jsVisitedAccount = prefs.getString(Constants.PREFS_KEY_FOREIN_ACCOUNT, "");
        visitedAccount = new Gson().fromJson(jsVisitedAccount, Account.class);
    }

    private void createLanguagesRecycler(Account account) {
//        creating language recyclerView
        scoreListRecyclerView = view.findViewById(R.id.lang_list);
        scoreListRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new LangListAdapter(view.getContext(), account.getLanguages().getLanguages());
        adapter.setClickListener(myLangAdapterClickListener);
        scoreListRecyclerView.setAdapter(adapter);
    }

    private void createGamesRecyclerGrid(Account account) {
        //      creating GameCards recyclerView
        gamesRecyclerView = view.findViewById(R.id.profile_RCL_games);
        gamesRecyclerView.setLayoutManager(new GridLayoutManager(view.getContext(), 3));
        gamesAdapter = new GameCardViewListAdapter(view.getContext(), account.getGames().getGames());
        gamesAdapter.setClickListener(myGamesAdapterClickListener);
        gamesRecyclerView.setAdapter(gamesAdapter);
    }

    public void setProfilePic(Picture profilePic) {
        Glide.with(view.getContext())
                .load(profilePic.getUrl())
                .centerCrop()
                .into(profile_RDV_pic);
        account.setProfilePic(profilePic);
        jsAccount = new Gson().toJson(account);
        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
        updateCurrentAccount();
    }

    public void setProfilePic() {
        MyFirebase.getProfilePicUrl(new CallBackUrlReady() {
            @Override
            public void urlReady(String url) {
                Log.d("vvv", "url: " + url);
                Glide.with(view.getContext())
                        .load(url)
                        .centerCrop()
                        .into(profile_RDV_pic);
                account.setProfilePic(new Picture(Constants.PROFILE_PIC, url));
                jsAccount = new Gson().toJson(account);
                prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
            }
        }, account.getId().getSerialNum()+"");
    }

    public void setCoverPic(Picture coverPic) {
        Glide.with(view.getContext())
                .load(coverPic.getUrl())
                .centerCrop()
                .into(profile_IMG_coverPic);
        account.setCoverPic(coverPic);
        jsAccount = new Gson().toJson(account);
        prefs.putString(Constants.PREFS_KEY_ACCOUNT, jsAccount);
        updateCurrentAccount();
    }

    GameCardViewListAdapter.ItemClickListener myGamesAdapterClickListener = new GameCardViewListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.d("vvv", "clicked on: " + position + " game");
        }
    };

    LangListAdapter.ItemClickListener myLangAdapterClickListener = new LangListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.d("vvv", "clicked on: " + position + " language");
        }
    };

    private int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

}
