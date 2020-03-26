package com.example.multyplay;

import java.util.ArrayList;

public class Followers {

    private long myAccountSerialNumber;
    private ArrayList<String> followers;

    public Followers() {
    }

    public Followers(long myAccountSerialNumber, ArrayList<String> followers) {
        setMyAccountSerialNumber(myAccountSerialNumber);
        setFollowers(followers);
    }


    public long getMyAccountSerialNumber() {
        return myAccountSerialNumber;
    }

    public void setMyAccountSerialNumber(long myAccountSerialNumber) {
        this.myAccountSerialNumber = myAccountSerialNumber;
    }

    public ArrayList<String> getFollowers() {
        return followers;
    }

    public void setFollowers(ArrayList<String> followers) {
        this.followers = followers;
    }
}
