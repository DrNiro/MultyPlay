package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class FollowersFragment extends Fragment {

    private View view = null;
    private CallBackEnterOtherAccount callBackEnterOtherAccount;

    private MySharedPreferences prefs;
    private String jsFollowersList;
    private Followers myFollowersList;

    private RecyclerView followersRecyclerView;
    private FollowersListAdapter followersListAdapter;
    private ArrayList<Account> followersAccounts;

    private boolean gotFollowersAccounts;
    private EditText followers_EDT_search;


    public void setCallBackEnterOtherAccount(CallBackEnterOtherAccount callBackEnterOtherAccount) {
        this.callBackEnterOtherAccount = callBackEnterOtherAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_followers, container, false);
        }

        findViews();
        gotFollowersAccounts = false;

        prefs = new MySharedPreferences(view.getContext());
        jsFollowersList = prefs.getString(Constants.PREFS_KEY_MY_FOLLOWERS_LIST, "");
        Log.d("www", "jsFollowersList: " + jsFollowersList);
        myFollowersList = new Gson().fromJson(jsFollowersList, Followers.class);

        followersAccounts = new ArrayList<>();
        getMyFollowersAccounts();
        Log.d("www", "followingAccounts size: " + followersAccounts.size());

        followers_EDT_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(gotFollowersAccounts) {
                    followersListAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void createFollowersListRecycler(ArrayList<Account> accountsList) {
        followersRecyclerView = view.findViewById(R.id.following_RCL_myFollowers);
        followersRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        followersListAdapter = new FollowersListAdapter(view.getContext(), accountsList);
        followersListAdapter.setClickListener(myFollowersAdapterClickListener);
        followersListAdapter.setMyActionClickListener(followActionListener);
        followersRecyclerView.setAdapter(followersListAdapter);
    }

    //    Not efficient at all. wrote the brute force approach to submit assignment on time.
    private void getMyFollowersAccounts() {
        MyFirebase.getAccounts(new CallBackAccountsReady() {
            @Override
            public void accountsReady(ArrayList<Account> accounts) {
                int counter = 0;
                if (myFollowersList.getFollowers() == null) {
                    if(!isFragHidden())
                        Toast.makeText(view.getContext(), "No followers", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    for (Account acc : accounts) {
                        for (String followersId : myFollowersList.getFollowers()) {
                            if ((acc.getId().getSerialNum() + "").equals(followersId)) {
                                followersAccounts.add(acc);
                                counter++;
                                break;
                            }
                        }
                        if (counter == myFollowersList.getFollowers().size()) {
                            break;
                        }
                    }
                    gotFollowersAccounts = true;
                    createFollowersListRecycler(followersAccounts);
                }
            }

            @Override
            public void error() {
            }
        });
    }

    private boolean isFragHidden() {
        return this.isHidden();
    }

    private void findViews() {
        followers_EDT_search = view.findViewById(R.id.followers_EDT_search);
    }

    FollowersListAdapter.ItemClickListener myFollowersAdapterClickListener = new FollowersListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.d("www", "clicked on follower: " + position);
            Account visitAccount = followersListAdapter.getItem(position);
            callBackEnterOtherAccount.clickedOnOtherProfile(visitAccount);
        }
    };

    MyActionClickListener followActionListener = new MyActionClickListener() {
        @Override
        public void onActionClicked(View view, int position) {
            // TODO: 3/22/2020 open dialog suggesting unfollow/cancel (show image with text saying what will happen after unfollow approved
            Log.d("www", "clicked on action of follower: " + position);
        }
    };

}
