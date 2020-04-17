package com.example.multyplay;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

public class ClosingService extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);

        MySharedPreferences prefs = new MySharedPreferences(getApplication());
        Account myAccount = new Gson().fromJson(prefs.getString(Constants.PREFS_KEY_ACCOUNT, ""), Account.class);;

        // Handle application closing
        MyFirebase.updateOnline(myAccount, false);

        // Destroy the service
        stopSelf();
    }

}