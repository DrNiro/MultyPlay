package com.example.multyplay;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyFirebase {

    public static void updateOnline(Account account, boolean isOnline) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Accounts");

        myRef.child(account.getId().getSerialNum()+"").child("online").setValue(isOnline);
    }

    public static void setProfilePic(Account account, Picture newProfilePic) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Accounts");
        myRef.child(account.getId().getSerialNum()+"").child(Constants.PROFILE_PIC).setValue(newProfilePic);
    }

    public static void getProfilePicUrl(final CallBackUrlReady callBackUrlReady, String serialNum) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Accounts").child(serialNum);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picture pic;
                String profilePicUrl = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().equals(Constants.PROFILE_PIC)) {
                        pic = ds.getValue(Picture.class);
                        profilePicUrl = pic.getUrl();
                    }
                }
                callBackUrlReady.urlReady(profilePicUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void setCoverPic(Account account, Picture newCoverPic) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Accounts");
        myRef.child(account.getId().getSerialNum()+"").child(Constants.COVER_PIC).setValue(newCoverPic);
    }

    public static void getCoverPicUrl(final CallBackUrlReady callBackUrlReady, String serialNum) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Accounts").child(serialNum);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picture pic;
                String coverPicUrl = "";
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if(ds.getKey().equals(Constants.PROFILE_PIC)) {
                        pic = ds.getValue(Picture.class);
                        coverPicUrl = pic.getUrl();
                    }
                }
                callBackUrlReady.urlReady(coverPicUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void setAccount(Account account) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Accounts");
        myRef.child("" + account.getId().getSerialNum()).setValue(account);
    }

    public static void getAccounts(final CallBackAccountsReady callBackAccountsReady) {
        final ArrayList<Account> accounts = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Accounts");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null)
                    callBackAccountsReady.error();

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Account account = ds.getValue(Account.class);
                    accounts.add(account);
                }
                callBackAccountsReady.accountsReady(accounts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBackAccountsReady.error();
            }
        });
    }

}
