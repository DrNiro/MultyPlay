package com.example.multyplay;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MyFirebase {

    public static void updateOnline(Account account, boolean isOnline) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Accounts");

        myRef.child(account.getId().getSerialNum()+"").child("online").setValue(isOnline);
        Log.d("bbbFirebase", "updatedOnline");
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
        Log.d("bbbFirebase", "updatedAccount finished");

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

    public static void sendMessage(Account myAccount, Account sentToAccount, String message) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chats").child("messages");

        HashMap<String, Object> msgHM = new HashMap<>();
        msgHM.put("sender", myAccount.getId().getSerialNum()+"");
        msgHM.put("receiver", sentToAccount.getId().getSerialNum()+"");
        msgHM.put("message", message);

        myRef.push().setValue(msgHM);
    }

    public static void readMessages(final Account myAccount, final Account readMsgFromAccount, final CallBackMsgsReady callBackMsgsReady) {
        final ArrayList<Chat> mChat = new ArrayList<>();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chats").child("messages");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChat.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Chat chat = ds.getValue(Chat.class);
                    if((chat.getReceiver().equals(myAccount.getId().getSerialNum()+"") && chat.getSender().equals(readMsgFromAccount.getId().getSerialNum()+""))
                            || (chat.getReceiver().equals(readMsgFromAccount.getId().getSerialNum()+"") && chat.getSender().equals(myAccount.getId().getSerialNum()+""))){
                        mChat.add(chat);
                    }
                }
                callBackMsgsReady.msgsReady(mChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void setFollowing(Account account, Following followingList) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Following");
        myRef.child("" + account.getId().getSerialNum()).setValue(followingList);
    }

    public static void setFollowers(Account account, Followers followersList) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Followers");
        myRef.child("" + account.getId().getSerialNum()).setValue(followersList);
    }

    public static void getFollowing(final CallBackFollowListReady callBackFollowListReady, final Account myAccount) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Following");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Following someFollowingList = ds.getValue(Following.class);
                    if(someFollowingList.getMyAccountSerialNumber() == myAccount.getId().getSerialNum()) {
                        callBackFollowListReady.followingListReady(someFollowingList);
                        return;
                    }
                }
                callBackFollowListReady.listIsEmpty();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void getFollowers(final CallBackFollowListReady callBackFollowListReady, final Account myAccount) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Followers");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Followers someFollowersList = ds.getValue(Followers.class);
                    if(someFollowersList.getMyAccountSerialNumber() == myAccount.getId().getSerialNum()) {
                        callBackFollowListReady.followersListReady(someFollowersList);
                        return;
                    }
                }
                callBackFollowListReady.listIsEmpty();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void setChat(Account myAccount, ChatWith chatsWithList) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chats").child("OpenChats");
        myRef.child("" + myAccount.getId().getSerialNum()).setValue(chatsWithList);
    }

    public static void getOpenChats(final CallBackOpenChatsReady callBackOpenChatsReady, final Account myAccount) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Chats").child("OpenChats");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ChatWith someChatWithList = ds.getValue(ChatWith.class);
                    if(someChatWithList.getMyAccountSerialNumber() == myAccount.getId().getSerialNum()) {
                        callBackOpenChatsReady.chatsOpenListReady(someChatWithList);
                        return;
                    }
                }
                callBackOpenChatsReady.listIsEmpty();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
