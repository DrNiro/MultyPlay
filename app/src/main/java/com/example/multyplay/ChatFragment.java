package com.example.multyplay;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class ChatFragment extends Fragment {

    private View view = null;
    private CallBackEnterOtherAccount callBackEnterChatWithAccount;

    private RecyclerView chatsRecyclerView;
    private ChatListAdapter chatListAdapter;
    private ArrayList<Account> accountsIChatWith;

    private MySharedPreferences prefs;
    private String jsChattingList;
    private ChatWith myOpenChatsList;

    private boolean gotChattingAccounts;
    private EditText chat_EDT_search;

    public void setJsChattingWith(String jsChattingWith) {
        this.jsChattingList = jsChattingWith;
        this.myOpenChatsList = new Gson().fromJson(jsChattingList, ChatWith.class);
    }

    public void setCallBackEnterChatWithAccount(CallBackEnterOtherAccount callBackEnterChatWithAccount) {
        this.callBackEnterChatWithAccount = callBackEnterChatWithAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view == null) {
            view = inflater.inflate(R.layout.fragment_chat, container, false);
        }

        gotChattingAccounts = false;

        prefs = new MySharedPreferences(view.getContext());
        String jsMyAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        Account myAccount = new Gson().fromJson(jsMyAccount, Account.class);

        findViews();

        chat_EDT_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(gotChattingAccounts) {
                    chatListAdapter.getFilter().filter(charSequence);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private void findViews() {
        chat_EDT_search = view.findViewById(R.id.chat_EDT_search);
    }

//    Not efficient at all. wrote the brute force approach to submit assignment on time.
    public void getMyChattingWithAccounts() {
        accountsIChatWith = new ArrayList<>();
        MyFirebase.getAccounts(new CallBackAccountsReady() {
            @Override
            public void accountsReady(ArrayList<Account> accounts) {
                int counter = 0;
                if(myOpenChatsList.getChattingWith() == null) {
                    if(!isFragHidden()) {
                        Toast.makeText(view.getContext(), "Start to chat with gamers!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                } else {
                    Log.d("testChat", "getMyChatting...() myOpenChatsList: " + myOpenChatsList.getChattingWith().get(0));
                    Log.d("testChat", "myOpenChatsList size: " + myOpenChatsList.getChattingWith().size());
                    for(Account acc : accounts) {
                        for(String chatterId : myOpenChatsList.getChattingWith()) {
                            if((acc.getId().getSerialNum()+"").equals(chatterId)) {
                                accountsIChatWith.add(acc);
                                Log.d("testChat", "chatting with: " + acc.getNickName());
                                counter++;
                                break;
                            }
                        }
                        if(counter == myOpenChatsList.getChattingWith().size()) {
                            break;
                        }
                    }
                    gotChattingAccounts = true;
                    createChattingWithListRecycler(accountsIChatWith);
                    Log.d("testChat", "updating list");
                    chatListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error() {
            }
        });
    }

    private void createChattingWithListRecycler(ArrayList<Account> chatWith) {
        chatsRecyclerView = view.findViewById(R.id.chat_RCL_allChats);
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        chatListAdapter = new ChatListAdapter(view.getContext(), chatWith);
        chatListAdapter.setClickListener(myChatAdapterClickListener);
        chatsRecyclerView.setAdapter(chatListAdapter);
    }

    public void updateList() {
        Log.d("testChat", "updating list after message button clicked");
        jsChattingList = prefs.getString(Constants.PREFS_KEY_MY_OPEN_CHATS, "");
        Log.d("testChat", "new jsChatList: " + jsChattingList);
        myOpenChatsList = new Gson().fromJson(jsChattingList, ChatWith.class);
        getMyChattingWithAccounts();
    }

    ChatListAdapter.ItemClickListener myChatAdapterClickListener = new ChatListAdapter.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Account visitAccount = chatListAdapter.getItem(position);
            callBackEnterChatWithAccount.clickedOnOtherProfile(visitAccount);
        }
    };

    public void updateOpenChats() {
        getMyChattingWithAccounts();
        chatListAdapter.notifyDataSetChanged();
    }

    private boolean isFragHidden() {
        return this.isHidden();
    }
}
