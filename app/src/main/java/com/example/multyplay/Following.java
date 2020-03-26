package com.example.multyplay;

import java.util.ArrayList;

public class Following {

    private long myAccountSerialNumber;
    private ArrayList<String> following;

    public Following() {
    }

    public Following(long myAccountSerialNumber, ArrayList<String> following) {
        setMyAccountSerialNumber(myAccountSerialNumber);
        setFollowing(following);
    }

    public long getMyAccountSerialNumber() {
        return myAccountSerialNumber;
    }

    public void setMyAccountSerialNumber(long myAccountSerialNumber) {
        this.myAccountSerialNumber = myAccountSerialNumber;
    }

    public ArrayList<String> getFollowing() {
        return following;
    }

    public void setFollowing(ArrayList<String> following) {
        this.following = following;
    }
}
