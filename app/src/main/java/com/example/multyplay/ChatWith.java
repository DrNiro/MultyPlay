package com.example.multyplay;

import java.util.ArrayList;

public class ChatWith {

    private long myAccountSerialNumber;
    private ArrayList<String> chattingWith;

    public ChatWith() {
    }

    public ChatWith(long myAccountSerialNumber, ArrayList<String> chattingWith) {
        setMyAccountSerialNumber(myAccountSerialNumber);
        setChattingWith(chattingWith);
    }

    public long getMyAccountSerialNumber() {
        return myAccountSerialNumber;
    }

    public void setMyAccountSerialNumber(long myAccountSerialNumber) {
        this.myAccountSerialNumber = myAccountSerialNumber;
    }

    public ArrayList<String> getChattingWith() {
        return chattingWith;
    }

    public void setChattingWith(ArrayList<String> chattingWith) {
        this.chattingWith = chattingWith;
    }
}
