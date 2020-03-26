package com.example.multyplay;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChatWindow extends AppCompatActivity {

    private ImageView chatWindow_BTN_send;
    private EditText chatWindow_EDT_message;

    private TextView chatWindow_TXT_chatWithName;
    private RoundedImageView chatWindow_IMG_pic;

    private MySharedPreferences prefs;
    private String jsMyAccount;
    private Account myAccount;

    private String jsChatWithAccount;
    private Account chatWithAccount;

    private RecyclerView chatWindow_RCL_allMessages;
    private MessagesAdapter messagesAdapter;
    private ArrayList<Chat> messagesList;

    private ChatWith receiverChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);

        chatWindow_BTN_send = findViewById(R.id.chatWindow_BTN_send);
        chatWindow_EDT_message = findViewById(R.id.chatWindow_EDT_message);
        chatWindow_TXT_chatWithName = findViewById(R.id.chatWindow_TXT_chatWithName);
        chatWindow_IMG_pic = findViewById(R.id.chatWindow_IMG_pic);

        jsChatWithAccount = getIntent().getStringExtra(Constants.KEY_CHAT_WITH);
        chatWithAccount = new Gson().fromJson(jsChatWithAccount, Account.class);

        prefs = new MySharedPreferences(this);
        jsMyAccount = prefs.getString(Constants.PREFS_KEY_ACCOUNT, "");
        myAccount = new Gson().fromJson(jsMyAccount, Account.class);
//        jsChatWithAccount = prefs.getString(Constants.PREFS)

        if(chatWithAccount.getProfilePic().getUrl() != null) {
            Glide.with(this)
                    .load(chatWithAccount.getProfilePic().getUrl())
                    .centerCrop()
                    .into(chatWindow_IMG_pic);
        }
        chatWindow_TXT_chatWithName.setText(chatWithAccount.getNickName());

        MyFirebase.getOpenChats(new CallBackOpenChatsReady() {
            @Override
            public void chatsOpenListReady(ChatWith chatWith) {
                if(chatWith.getChattingWith() != null) {
                    receiverChats = chatWith;
//                    if(!chatWith.getChattingWith().contains(myAccount.getId().getSerialNum()+"")) {
//                        ArrayList<String> chatsList = new ArrayList<>(chatWith.getChattingWith());
//                        chatsList.add(myAccount.getId().getSerialNum()+"");
//                        ChatWith receiverChatWith = chatWith;
//                        receiverChatWith.setChattingWith(chatsList);
//                        MyFirebase.setChat(chatWithAccount, receiverChatWith);
//                    }
                } else {
                    ArrayList<String> chatsList = new ArrayList<>();
                    chatsList.add(myAccount.getId().getSerialNum()+"");
                    ChatWith receiverChats = new ChatWith(chatWithAccount.getId().getSerialNum(), chatsList);
                    receiverChats.setChattingWith(chatsList);
                    MyFirebase.setChat(chatWithAccount, receiverChats);
                }
            }

            @Override
            public void listIsEmpty() {
                ArrayList<String> chatsList = new ArrayList<>();
                chatsList.add(chatWithAccount.getId().getSerialNum()+"");
                ChatWith receiverChats = new ChatWith(chatWithAccount.getId().getSerialNum(), chatsList);
                receiverChats.setChattingWith(chatsList);
                MyFirebase.setChat(chatWithAccount, receiverChats);
            }
        }, chatWithAccount);

        MyFirebase.readMessages(myAccount, chatWithAccount, new CallBackMsgsReady() {
            @Override
            public void msgsReady(ArrayList<Chat> messages) {
                createMessagesRecyclerView(messages);
            }
        });

        chatWindow_BTN_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(receiverChats != null) {
                    if(!receiverChats.getChattingWith().contains(myAccount.getId().getSerialNum()+"")) {
                        ArrayList<String> chatsList = new ArrayList<>(receiverChats.getChattingWith());
                        chatsList.add(myAccount.getId().getSerialNum() + "");
                        receiverChats.setChattingWith(chatsList);
                        MyFirebase.setChat(chatWithAccount, receiverChats);
                    }
                }
                String msg = chatWindow_EDT_message.getText().toString().trim();
                if(!msg.equals("")){
                    MyFirebase.sendMessage(myAccount, chatWithAccount, msg);
                }
                chatWindow_EDT_message.getText().clear();
            }
        });

    }

    private void createMessagesRecyclerView(ArrayList<Chat> messages) {
        chatWindow_RCL_allMessages = findViewById(R.id.chatWindow_RCL_allMessages);
        chatWindow_RCL_allMessages.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        chatWindow_RCL_allMessages.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(this, messages, myAccount);
//        messagesAdapter.setClickListener(myFollowingAdapterClickListener);
        chatWindow_RCL_allMessages.setAdapter(messagesAdapter);

    }
}
