package com.example.multyplay;

import java.util.ArrayList;

public interface CallBackFollowListReady {

    void followingListReady(Following following);
    void followersListReady(Followers followers);
    void listIsEmpty();

}
