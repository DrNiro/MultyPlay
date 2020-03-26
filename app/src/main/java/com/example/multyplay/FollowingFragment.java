package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class FollowingFragment extends Fragment {

    private View view = null;
    private CallBackEnterOtherAccount callBackEnterOtherAccount;

    private MySharedPreferences prefs;
    private String jsFollowingList;
    private Following myFollowingList;

    private RecyclerView followingRecyclerView;
    private FollowingListAdapter followingListAdapter;
    private ArrayList<Account> followingAccounts;

    private boolean gotFollowingAccounts;
    private EditText following_EDT_search;

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
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_following, container, false);
        }

        findViews();
        gotFollowingAccounts = false;

        prefs = new MySharedPreferences(view.getContext());
        jsFollowingList = prefs.getString(Constants.PREFS_KEY_MY_FOLLOWING_LIST, "");
        Log.d("www", "jsFollowingList: " + jsFollowingList);
        myFollowingList = new Gson().fromJson(jsFollowingList, Following.class);

        followingAccounts = new ArrayList<>();
        getMyFollowingsAccounts();
        Log.d("www", "followingAccounts size: " + followingAccounts.size());

        following_EDT_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(gotFollowingAccounts) {
                    followingListAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void createFollowingListRecycler() {
        followingRecyclerView = view.findViewById(R.id.following_RCL_myFollowing);
        followingRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        followingListAdapter = new FollowingListAdapter(view.getContext(), followingAccounts);
        followingListAdapter.setClickListener(myFollowingAdapterClickListener);
        followingListAdapter.setMyActionClickListener(followActionListener);
        followingRecyclerView.setAdapter(followingListAdapter);
    }

//    Not efficient at all. wrote the brute force approach to submit assignment on time.
    private void getMyFollowingsAccounts() {
        MyFirebase.getAccounts(new CallBackAccountsReady() {
            @Override
            public void accountsReady(ArrayList<Account> accounts) {
                int counter = 0;
                if(myFollowingList.getFollowing() == null) {
                    if(!isFragHidden())
                        Toast.makeText(view.getContext(), "Follow other gamers!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    for(Account acc : accounts) {
                        for(String followingId : myFollowingList.getFollowing()) {
                            if((acc.getId().getSerialNum()+"").equals(followingId)) {
                                followingAccounts.add(acc);
                                counter++;
                                break;
                            }
                        }
                        if(counter == myFollowingList.getFollowing().size()) {
                            break;
                        }
                    }
                    gotFollowingAccounts = true;
                    createFollowingListRecycler();
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
        following_EDT_search = view.findViewById(R.id.following_EDT_search);
    }

    FollowingListAdapter.ItemClickListener myFollowingAdapterClickListener = new FollowingListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Log.d("www", "clicked on followin: " + position);
            Account visitAccount = followingListAdapter.getItem(position);
            callBackEnterOtherAccount.clickedOnOtherProfile(visitAccount);
        }
    };

    MyActionClickListener followActionListener = new MyActionClickListener() {
        @Override
        public void onActionClicked(View view, int position) {
            // TODO: 3/22/2020 open dialog suggesting unfollow / cancel (show image with text saying what will happen after unfollow approved
            Log.d("www", "clicked on action of following: " + position);
        }
    };



}
